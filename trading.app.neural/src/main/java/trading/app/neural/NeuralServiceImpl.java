package trading.app.neural;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.Stopwatch;

import trading.app.neural.events.TestCompletedEvent;
import trading.app.neural.events.TestIterationCompletedEvent;
import trading.app.neural.events.TestStartedEvent;
import trading.app.neural.events.TrainCompletedEvent;
//import trading.data.MLBarDataConverter;
//import trading.data.MLBarDataLoader;
//import trading.data.model.BarEntity;
//import trading.data.model.DataPair;
//import trading.data.model.OutputEntity;
import trading.app.neural.events.TrainIterationCompletedEvent;
import trading.app.neural.events.TrainStartedEvent;
import trading.app.neural.history.Level1DataManager;
import trading.app.neural.history.NeuralDataManager;
import trading.data.model.Level1;

/**
 * Neural network service. Used to train, test network, predict data
 * 
 * @author pdg
 * 
 */
public class NeuralServiceImpl extends NeuralServiceBase {
	/**
	 * Flag to stop training asap
	 */
	private boolean trainStopFlag;

	/**
	 * Flag to stop testing asap
	 */
	private boolean testStopFlag;

	/**
	 * Encog training continuation object to resume training
	 */
	private TrainingContinuation trainContinuation;

	/**
	 * Ctor
	 */
	public NeuralServiceImpl(NeuralContext neuralContext,
			NeuralDataManager neuralDataManager) {
		super(neuralContext, neuralDataManager);
	}

	/**
	 * @see NeuralServiceBase#createNetwork(List)
	 */
	@Override
	public BasicNetwork createNetwork(List<Integer> layers) {
		if (layers.size() < 2) {
			throw new IllegalArgumentException("Wrong network layers count");
		}
		final FeedForwardPattern pattern = new FeedForwardPattern();
		// Input neurons
		int input = layers.get(0);
		pattern.setInputNeurons(input);
		// Hidden neurons
		for (int i = 1; ((i < layers.size() - 1)); i++) {
			int neurons = layers.get(i);
			if (neurons > 0) {
				pattern.addHiddenLayer(layers.get(i));
			}
		}
		// Output neurons
		int output = layers.get(layers.size() - 1);
		pattern.setOutputNeurons(output);

		// // Activation function
		pattern.setActivationFunction(new ActivationTANH());
		// pattern.setActivationFunction(new ActivationLinear());

		// //pattern.setActivationFunction(new ActivationElliott());

		// Create network
		final BasicNetwork network = (BasicNetwork) pattern.generate();
		// Randomize the network
		(new ConsistentRandomizer(-1, 1, 100)).randomize(network);
		neuralContext.setNetwork(network);

		return network;

	}

	/**
	 * @see NeuralServiceBase#getFirstLayerSize(int)
	 */
	@Override
	public int getFirstLayerSize(int entityListSize) {
		return entityListSize * Level1DataManager.LEVEL1_DATA_SIZE;
	}

	/**
	 * @see NeuralServiceBase#getLastLayerSize()
	 */
	public int getLastLayerSize() {
		return Level1DataManager.OUTPUT_SIZE;
	}

	/**
	 * Reset training continuation data. Will start new training, not continue
	 * previous one
	 */
	@Override
	public void resetTraining() {
		trainStopFlag = true;
		trainContinuation = null;
	}

	/**
	 * @see NeuralServiceBase#stop()
	 */
	@Override
	public void stop() {
		trainStopFlag = true;
		testStopFlag = true;
	}

	/**
	 * Test and learn every iteration
	 * 
	 * @see NeuralServiceBase#testNetwork()
	 */
	@Override
	public void testNetwork() {
		eventBus.post(new TestStartedEvent());
		BasicNetwork network = neuralContext.getNetwork();
		Date trainEndTime = neuralContext.getTrainingEndDateTime();
		List<Level1> data = neuralDataManager.loadTestData(trainEndTime);

		// ??? ToDo: rework, predict
		int startIndex = neuralContext.getLevel1WindowSize();
		// + neuralContext.getPredictionSize();
		int step = 1;
		// Go through all prediction window
		int i = 0, iteration = 0;

		for (testStopFlag = false, i = startIndex, iteration = 1; i < startIndex
				+ neuralContext.getPredictionSamples() * step
				&& !testStopFlag; i += step, iteration++) {
			// Get input - ideal data pair
			MLData input = neuralDataManager.getInputData(data, i);
			// Predict
			MLData output = network.compute(input);

			// Get ideal data
			MLData ideal = neuralDataManager.getOutputData(data, i);
			// Calculate error
			MLDataPair pair = new BasicMLDataPair(input, ideal);
			MLDataSet dataSet = new BasicMLDataSet(
					Arrays.asList(new MLDataPair[] { pair }));
			double error = network.calculateError(dataSet);

			// Train if continious training is set
			if (neuralContext.isContinuousTraining()) {
				// Learning based on previous prediction and real data
				// comparison
				// Train
				trainNetwork(dataSet);
			}

			// Fire event
			// Last level1 in input window. Prediction window starts next item
			Level1 level1 = data.get(i);
			TestIterationCompletedEvent event = new TestIterationCompletedEvent(
					level1, iteration, output.getData(0), // predicted low
					output.getData(1), // predicted high
					ideal.getData(0), // real low
					ideal.getData(1), // real high
					error);
			eventBus.post(event);
		}
		// Raise completed event
		eventBus.post(new TestCompletedEvent());
	}

	/**
	 * 
	 * @see NeuralServiceBase#trainNetwork()
	 */
	@Override
	public void trainNetwork(Date trainEndDateTime) {
		MLDataSet dataSet = neuralDataManager
				.loadTrainMLDataSet(trainEndDateTime);
		trainNetwork(dataSet);
	}

	/**
	 * Train on specific dataset
	 */
	void trainNetwork(MLDataSet dataSet) {
		eventBus.post(new TrainStartedEvent());
		// neuralContext.getTrainingContext().setLastError(0);
		// neuralContext.getTrainingContext().setSamplesCount(dataSet.size());
		BasicNetwork network = neuralContext.getNetwork();

		// Propagation training
		ResilientPropagation train = new ResilientPropagation(network, dataSet);
		// Backpropagation train = new Backpropagation(network, ds);
		train.setThreadCount(10);
		// Continue if this training is continuous one
		if (trainContinuation != null) {
			train.resume(trainContinuation);
		}
		neuralContext.setTrain(train);

		Logger.getLogger(NeuralServiceImpl.class.getName()).info(
				"Start training");

		// Create watches
		Stopwatch trainWatch = new Stopwatch();
		trainWatch.reset();
		trainWatch.start();

		Stopwatch epochWatch = new Stopwatch();
		epochWatch.reset();
		epochWatch.start();
		double lastError = 0;
		double sameErrorCount = 0;
		final int maxErrorCount = 100; // If error does not change maxErrorCount
										// loops, training completed
		int epoch = 1;
		for (trainStopFlag = false, epoch = 1; epoch <= neuralContext
				.getMaxEpochCount()
				&& sameErrorCount <= maxErrorCount
				&& !trainStopFlag; epoch++) {
			epochWatch.reset();
			// Do training iteration
			train.iteration();
			// Calculate error
			double error = train.getError();
			// Increase error coujnt
			if (Double.compare(error, lastError) == 0) {
				sameErrorCount++;
			} else {
				sameErrorCount = 0;
				lastError = error;
			}
			// Raise event
			TrainIterationCompletedEvent event = new TrainIterationCompletedEvent(
					epoch, epochWatch.getElapsedMilliseconds(),
					trainWatch.getElapsedMilliseconds(), lastError);
			eventBus.post(event);

			// Log
			Logger.getLogger(NeuralServiceImpl.class.getName()).info(
					String.format("Epoch %d. Time %d sec, error %s", epoch,
							epochWatch.getElapsedMilliseconds() / 1000,
							Double.toString(error)));
		}
		trainWatch.stop();
		epochWatch.stop();
		Logger.getLogger(NeuralServiceImpl.class.getName()).info(
				String.format("Training time  %d minutes",
						trainWatch.getElapsedMilliseconds() / 1000 / 60,
						Double.toString(train.getError())));
		// train.finishTraining();
		trainContinuation = train.pause();
		// Raise completed event
		eventBus.post(new TrainCompletedEvent());
	}
}
