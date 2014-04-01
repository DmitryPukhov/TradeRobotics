package trading.app.realTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Base class for realtime providers
 * 
 * @author dima
 *
 */
public abstract class RealTimeProviderBase implements RealTimeProvider {
	/**
	 * Level1 listeners list. Can receive only one level1 item
	 */
	private Map<Integer, List<MarketListener<List<Level1>>>> level1Listeners = new HashMap<Integer, List<MarketListener<List<Level1>>>>();

	/**
	 * Instrument event listeners list
	 */
	private List<MarketListener<Instrument>> instrumentListeners = new ArrayList<MarketListener<Instrument>>();

	/**
	 * Add listener
	 * 
	 * @param marketlistener
	 */
	@Override
	public void addInstrumentListener(MarketListener<Instrument> listener) {
		instrumentListeners.add(listener);
	}

	/**
	 * @see RealTimeProvider#addLevel1Listener(int, MarketListener)
	 */
	@Override
	public void addLevel1Listener(int instrumentId,
			MarketListener<List<Level1>> listener) {
		// Create record for this parent entity if empty
		if (!level1Listeners.containsKey(instrumentId)) {
			level1Listeners.put(instrumentId,
					new ArrayList<MarketListener<List<Level1>>>());
		}
		// Now we can add the listener
		level1Listeners.get(instrumentId).add(listener);

	}

	/**
	 * Notify all listeners
	 * 
	 * @param entity
	 */
	protected void fireInstrumentChangedEvent(Instrument entity) {
		for (MarketListener<Instrument> listener : instrumentListeners) {
			listener.OnMarketDataChanged(entity);
		}
	}

	/**
	 * Fire level1 event
	 */
	protected void fireLevel1ChangedEvent(int parentEntityId,
			List<Level1> entities) {
		List<MarketListener<List<Level1>>> entityListeners = level1Listeners
				.get(parentEntityId);
		if (entityListeners != null) {
			for (MarketListener<List<Level1>> listener : entityListeners) {
				// Fire event for one listener
				listener.OnMarketDataChanged(entities);
			}
		}

	}

	/**
	 * Fire level1 event for single level1 entity
	 */
	protected void fireLevel1ChangedEvent(int parentEntityId, Level1 entity) {
		fireLevel1ChangedEvent(parentEntityId,
				new ArrayList<Level1>(Arrays.asList(entity)));
	}

	/**
	 * Remove listener
	 * 
	 * @param listener
	 */
	@Override
	public void removeInstrumentListener(MarketListener<Instrument> listener) {
		instrumentListeners.remove(listener);
	}

	/**
	 * @see RealTimeProvider#removeLevel1Listener(int, MarketListener)
	 */
	@Override
	public void removeLevel1Listener(int instrumentId,
			MarketListener<List<Level1>> listener) {
		if (level1Listeners.containsKey(instrumentId)) {
			List<MarketListener<List<Level1>>> entityListeners = level1Listeners
					.get(instrumentId);
			// Remove listener
			entityListeners.remove(listener);
		}

	}

	/**
	 * Begin data providing and data events generation
	 */
	@Override
	public abstract void start();

	/**
	 * Do not provide data, don't fire events
	 */
	@Override
	public abstract void stop();
}
