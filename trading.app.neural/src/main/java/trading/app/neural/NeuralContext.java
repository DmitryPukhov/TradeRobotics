package trading.app.neural;

import java.util.Date;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;

import trading.app.TradingApplicationContext;

/**
 * Context contains neural network configuration and current state
 * 
 * @author pdg
 */
public class NeuralContext extends TradingApplicationContext {
	/**
	 * Neural network to work with
	 */
	private BasicNetwork network;
	private int predictionSize = 60;
	private int level1WindowSize = 100;
	private int maxEpochCount = 10;
	private int trainSamples = 10;
	private int predictionSamples = 10;
	private int trainStep = 1;
	private Date trainingEndDateTime = new Date();
	private boolean continuousTraining = false;
	private Train train;

	/**
	 * Get level1 item count in data window
	 * 
	 * @return the level1WindowSize
	 */
	public Integer getLevel1WindowSize() {
		return level1WindowSize;
	}

	/**
	 * @return the maxEpochCount
	 */
	public int getMaxEpochCount() {
		return maxEpochCount;
	}

	/**
	 * Gets neural network
	 */
	public BasicNetwork getNetwork() {
		return network;
	}

	/**
	 * @return the predictionSamples
	 */
	public Integer getPredictionSamples() {
		return predictionSamples;
	}

	/**
	 * @return the predictionInterval
	 */
	public Integer getPredictionSize() {
		return predictionSize;
	}

	/**
	 * @return the train
	 */
	public Train getTrain() {
		return train;
	}

	/**
	 * Gets time of last training moment.
	 * 
	 * @return the trainingEndDateTime
	 */
	public Date getTrainingEndDateTime() {
		return trainingEndDateTime;
	}

	/**
	 * @return the trainSamples
	 */
	public Integer getTrainSamples() {
		return trainSamples;
	}

	/**
	 * @return the trainStep
	 */
	public Integer getTrainStep() {
		return trainStep;
	}

	/**
	 * Whether train before every computing
	 * 
	 * @return the continuousTraining
	 */
	public boolean isContinuousTraining() {
		return continuousTraining;
	}

	/**
	 * Whether train before every computing
	 * 
	 * @param continuousTraining
	 *            the continuousTraining to set
	 */
	public void setContinuousTraining(boolean continuousTraining) {
		this.continuousTraining = continuousTraining;
	}

	/**
	 * Set level1 item count in data window
	 * 
	 * @param level1WindowSize
	 *            the level1WindowSize to set
	 */
	public void setLevel1WindowSize(Integer level1WindowSize) {
		this.level1WindowSize = level1WindowSize;
	}

	/**
	 * @param maxEpochCount
	 *            the maxEpochCount to set
	 */
	public void setMaxEpochCount(int maxEpochCount) {
		this.maxEpochCount = maxEpochCount;
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(BasicNetwork network) {
		this.network = network;
	}

	/**
	 * @param predictionSamples
	 *            the predictionSamples to set
	 */
	public void setPredictionSamples(Integer predictionSamples) {
		this.predictionSamples = predictionSamples;
	}

	/**
	 * @param predictionSize
	 *            the predictionInterval to set
	 */
	public void setPredictionSize(Integer predictionSize) {
		this.predictionSize = predictionSize;
	}

	/**
	 * @param train
	 *            the train to set
	 */
	public void setTrain(Train train) {
		this.train = train;
	}

	/**
	 * @param trainingEndDateTime
	 *            the trainingEndDateTime to set
	 */
	public void setTrainingEndDateTime(Date trainingEndDateTime) {
		this.trainingEndDateTime = trainingEndDateTime;
	}

	/**
	 * @param trainSamples
	 *            the trainSamples to set
	 */
	public void setTrainSamples(Integer trainSamples) {
		this.trainSamples = trainSamples;
	}

	/**
	 * @param trainStep
	 *            the trainStep to set
	 */
	public void setTrainStep(Integer trainStep) {
		this.trainStep = trainStep;
	}
}