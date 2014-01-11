package trading.app.neural;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import trading.app.neural.history.NeuralDataManager;

import com.google.common.eventbus.EventBus;

/**
 * Base class for all neural services
 * 
 * @author dima
 * 
 */
public abstract class NeuralServiceBase implements NeuralService {
	/**
	 * Guava eventBus for events like training iteration completed etc.
	 */
	EventBus eventBus = new EventBus();

	/**
	 * Data manager to work with history
	 */
	protected NeuralDataManager neuralDataManager;

	/**
	 * Neural related application-wide context with network, training, testing
	 * configuration data
	 */
	protected NeuralContext neuralContext;

	/**
	 * Construct with neural context data
	 * 
	 * @param neuralContext
	 */
	public NeuralServiceBase(NeuralContext neuralContext,
			NeuralDataManager neuralDataManager) {
		this.neuralDataManager = neuralDataManager;
		this.neuralContext = neuralContext;
	}

	/**
	 * Stop training or testing process
	 */
	@Override
	public abstract void stop();

	/**
	 * @see trading.app.neural.NeuralService#createNetwork(java.util.List)
	 */
	@Override
	public abstract BasicNetwork createNetwork(List<Integer> layers);

	/**
	 * {@link NeuralService#getEventBus()}
	 */
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * @see NeuralService#getFirstLayerSize(int)
	 */
	@Override
	public abstract int getFirstLayerSize(int entityListSize);

	/**
	 * @see NeuralService#getLastLayerSize()
	 */
	public abstract int getLastLayerSize();

	/**
	 * @see trading.app.neural.NeuralService#getNeuralContext()
	 */
	@Override
	public NeuralContext getNeuralContext() {
		return neuralContext;
	}

	/**
	 * @see trading.app.neural.NeuralService#loadNetwork(java.io.File)
	 */
	@Override
	public void loadNetwork(File file) {
		BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence
				.loadObject(file);
		neuralContext.setNetwork(network);
	}

	/**
	 * @see trading.app.neural.NeuralService#resetNetwork()
	 */
	@Override
	public void resetNetwork() {
		neuralContext.getNetwork().reset();
	}

	/**
	 * @see NeuralService#resetTraining()
	 */
	@Override
	public abstract void resetTraining();

	/**
	 * @see trading.app.neural.NeuralService#saveNetwork(java.io.File)
	 */
	@Override
	public void saveNetwork(File file) {
		EncogDirectoryPersistence.saveObject(file, neuralContext.getNetwork());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * trading.app.neural.NeuralService#setNeuralContext(trading.app.neural.
	 * NeuralContext)
	 */
	@Override
	public void setNeuralContext(NeuralContext neuralContext) {
		this.neuralContext = neuralContext;
	}

	/**
	 * General train of network
	 */
	@Override
	public abstract void trainNetwork(Date trainEndDateTime);

	/**
	 * @see NeuralService#testNetwork()
	 */
	@Override
	public abstract void testNetwork();

}
