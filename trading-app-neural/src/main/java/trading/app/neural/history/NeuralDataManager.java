package trading.app.neural.history;

import java.util.Date;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

import trading.data.model.Level1;

/**
 * Data loader for neural network
 * @author dima
 *
 */
public interface NeuralDataManager {

	/**
	 * Load data for train. Input windows <= lastTrainDate, prediction data > lastTrainDate
	 * Number of train and prediction samples is in context
	 * @param lastTrainDate
	 */
	public abstract MLDataSet loadTrainMLDataSet(Date lastTrainDate);
	
	/**
	 * Gets MLDataPair with input and output for specific position in data
	 * @param data All entities
	 * @param index Index of last item in window. Prediction window starts index+1 item
	 * @return
	 */
	public abstract MLDataPair getMLDataPair(List<Level1> data, int index);

	/**
	 * Load list of items for neural network test
	 * @return
	 */
	public abstract List<Level1> loadTestData(Date endTime);
	
	/**
	 * Returns input for prediction
	 * 
	 * @param data
	 * @param index
	 *            Current item index. Prediction window starts next item after
	 *            index
	 * @return input data for neural network
	 */
	public abstract MLData getInputData(List<Level1> data, int index);
	
	/**
	 * @param data
	 * @param index
	 *            Current item index. Prediction window starts next item after
	 *            index
	 * @return
	 */
	public abstract MLData getOutputData(List<Level1> data, int index);	

}