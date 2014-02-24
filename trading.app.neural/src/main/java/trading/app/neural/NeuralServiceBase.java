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
	 * {@inheritDoc}
	 */
	@Override
	public abstract void stop();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract BasicNetwork createNetwork(List<Integer> layers);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract int getFirstLayerSize(int entityListSize);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract int getLastLayerSize();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NeuralContext getNeuralContext() {
		return neuralContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadNetwork(File file) {
		BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence
				.loadObject(file);
		neuralContext.setNetwork(network);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetNetwork() {
		neuralContext.getNetwork().reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void resetTraining();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveNetwork(File file) {
		EncogDirectoryPersistence.saveObject(file, neuralContext.getNetwork());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNeuralContext(NeuralContext neuralContext) {
		this.neuralContext = neuralContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void trainNetwork(Date trainEndDateTime);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void testNetwork();

}
