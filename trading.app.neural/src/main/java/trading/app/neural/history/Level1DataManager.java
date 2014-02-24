package trading.app.neural.history;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.jboss.logging.Logger;

import trading.app.history.HistoryProvider;
import trading.app.neural.NeuralContext;
import trading.data.model.Level1;

/**
 * Get ML data for network learning
 * 
 * @author pdg
 * 
 */
public class Level1DataManager implements NeuralDataManager {
	/**
	 * Neural network configuration
	 */
	private NeuralContext neuralContext;

	private HistoryProvider historyProvider;

	private static Logger LOG = Logger.getLogger(Level1DataManager.class);

	/**
	 * Size of one level1 data
	 */
	public static final int LEVEL1_DATA_SIZE = 7;

	/**
	 * Output size - 2 elements: min bid and max ask in prediction interval
	 */
	public static final int OUTPUT_SIZE = 2;

	/**
	 * Constructor for context
	 * 
	 * @param context
	 */
	public Level1DataManager(NeuralContext context,
			HistoryProvider historyProvider) {
		neuralContext = context;
		this.historyProvider = historyProvider;
	}

	/**
	 * Convert level1 to array data
	 * 
	 * @param level1
	 * @return index position
	 */
	private int addLevel1Data(MLData data, int pos, Level1 level1,
			Level1 prevLevel1) {
		// List<Double> data = new ArrayList();
		// 0 - Date
		double normalizedDate = getNormalizedDate(level1.getDate());
		data.add(pos++, normalizedDate);
		// 1 - Price
		double normalizedPrice = getNormalizedPrice(level1.getLastPrice(),
				prevLevel1.getLastPrice());
		data.add(pos++, normalizedPrice);
		// 2- Size
		double normalizedSize = getNormalizedSize(level1.getLastSize(),
				prevLevel1.getLastSize());
		data.add(pos++, normalizedSize);
		// 3,4 - Bid and bid size
		double normalizedBid = getNormalizedPrice(level1.getBid(),
				prevLevel1.getBid());
		data.add(pos++, normalizedBid);
		double normalizedBidSize = getNormalizedSize(level1.getBidSize(),
				prevLevel1.getBidSize());
		data.add(pos++, normalizedBidSize);
		// 5,6 - Ask and ask size
		double normalizedAsk = getNormalizedPrice(level1.getAsk(),
				prevLevel1.getAsk());
		data.add(pos++, normalizedAsk);
		double normalizedAskSize = getNormalizedSize(level1.getAskSize(),
				prevLevel1.getAskSize());
		data.add(pos++, normalizedAskSize);

		return pos;
	}

	/**
	 * Convert level1 list to mldata
	 * 
	 * @param entityList
	 * @return
	 */
	private MLData entitiesToMLData(List<Level1> entityList) {
		// Contains network input data
		MLData data = new BasicMLData(LEVEL1_DATA_SIZE
				* (entityList.size() - 1));

		Level1 lastEntity = null;
		int pos = 0;
		for (Level1 entity : entityList) {
			if (lastEntity == null) {
				lastEntity = entity;
				continue;
			}
			// Add to data and update pos
			pos = addLevel1Data(data, pos, entity, lastEntity);
			lastEntity = entity;
		}

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData getInputData(List<Level1> data, int index) {
		// Get input data
		List<Level1> inputWindow = data.subList(
				index - neuralContext.getLevel1WindowSize(), index + 1);
		MLData inputData = entitiesToMLData(inputWindow);
		return inputData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataPair getMLDataPair(List<Level1> data, int index) {
		// Get input and output
		MLData inputData = getInputData(data, index);
		MLData outputData = getOutputData(data, index);

		// Create data pair for encog and add to dataset
		MLDataPair pair = new BasicMLDataPair(inputData, outputData);
		return pair;
	}

	/**
	 * Date normalization
	 * 
	 * @param date
	 * @return
	 */
	private double getNormalizedDate(Date date) {
		return 1 / (new Long(date.getTime()).doubleValue());

	}

	/**
	 * Get normalized value for price
	 * 
	 * @param current
	 * @param prev
	 * @return
	 */
	private double getNormalizedPrice(BigDecimal current, BigDecimal prev) {
		// Convert to delta %
		double normalized = (current.doubleValue() - prev.doubleValue())
				/ prev.doubleValue();
		return normalized;
	}

	/**
	 * Get normalized value for volume
	 * 
	 * @param current
	 * @param prev
	 * @return
	 */
	private double getNormalizedSize(Integer current, Integer prev) {
		// Convert to delta %
		double normalized = (current.doubleValue() - prev.doubleValue())
				/ prev.doubleValue();
		return normalized;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData getOutputData(List<Level1> data, int index) {
		// Get output data - min, max
		Level1 currentLevel1 = data.get(index);
		List<Level1> predictionWindow = data.subList(index + 1, index + 1
				+ neuralContext.getPredictionSize());

		MLData outputData = new BasicMLData(OUTPUT_SIZE);
		Double minBid = Double.MAX_VALUE;
		Double maxAsk = Double.MIN_VALUE;
		// Go through prediction window
		for (Level1 level1 : predictionWindow) {
			Double bid = getNormalizedPrice(level1.getBid(),
					currentLevel1.getBid());
			Double ask = getNormalizedPrice(level1.getAsk(),
					currentLevel1.getAsk());
			if (bid < minBid) {
				minBid = bid;
			}
			if (ask > maxAsk) {
				maxAsk = ask;
			}
		}
		// Form outputData
		outputData.add(0, minBid);
		outputData.add(1, maxAsk);
		return outputData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> loadTestData(Date endTime) {
		// prev bar + input window, this data should be before to start
		// prediction just after endTime
		int beforeDataSize = 1 + neuralContext.getLevel1WindowSize();
		// prediction data, after endTime
		int testStep = 1;
		int predictionSize = neuralContext.getPredictionSize();
		int predictionSamples = neuralContext.getPredictionSamples();
		int predictionDataSize = predictionSize + (predictionSamples - 1)
				* testStep;
		// before + prediction
		int dataSize = predictionDataSize + beforeDataSize;

		// Load last data for current instrument from database
		int instrumentId = neuralContext.getInstrument().getId();

		// beforeData + afterData = data
		List<Level1> data = historyProvider.findLevel1NotAfter(instrumentId,
				endTime, beforeDataSize);
		List<Level1> afterData = historyProvider.findLevel1After(instrumentId,
				endTime, predictionDataSize);
		data.addAll(afterData);
		// Exception if not enough data
		if (data.size() < dataSize) {
			IllegalArgumentException ex = new IllegalArgumentException(
					"Test history data is not enough for given window size, prediction size and samples count");
			LOG.error(ex);
			throw ex;
		}
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataSet loadTrainMLDataSet(Date endDate) {

		// Calculate the size of data before or at endDate
		int windowSize = neuralContext.getLevel1WindowSize();
		int trainStep = neuralContext.getTrainStep();
		int trainSamples = neuralContext.getTrainSamples();
		int inputSize = 1 + windowSize + trainStep * (trainSamples - 1);

		int predictionSize = neuralContext.getPredictionSize();
		// Whole training data
		int trainDataSize = inputSize + predictionSize;

		int instrumentId = neuralContext.getInstrument().getId();
		// Get not above end data training data
		List<Level1> data = historyProvider.findLevel1NotAfter(instrumentId,
				endDate, trainDataSize);

		// Exception if not enough data
		if (data.size() < trainDataSize) {
			IllegalArgumentException ex = new IllegalArgumentException(
					"Training history data is not enough for given window size, prediction size and samples count");
			LOG.error(ex);
			throw ex;
		}
		// Create and fill dataset
		MLDataSet dataSet = new BasicMLDataSet();
		// Prepare samples for training
		for (int i = 0; i < trainSamples; i += trainStep) {
			MLDataPair pair = getMLDataPair(data, windowSize + i);
			dataSet.add(pair);
		}
		return dataSet;
	}
}
