/**
 * 
 */
package trading.app.realTime;

import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Real time data provider. Generates events
 * @author pdg
 *
 */
public interface RealTimeProvider {

	/**
	 * Connect to data
	 */
	public abstract void start();

	/**
	 * Disconnect from gate
	 */
	public abstract void stop();	
	/**
	 * Subscribe to level1 data for specific instrument
	 * @param marketlistener
	 */
	public void addLevel1Listener(int instrumentId, MarketListener<Level1> listener);
	/**
	 * Unsubscribe from level1 data for specific instrument
	 * @param Level1 listener class
	 */
	public void removeLevel1Listener(int instrumentId, MarketListener<Level1> listener);
	
	/**
	 * Subscribe to instrument events
	 * @param marketlistener
	 */
	public void addInstrumentListener(MarketListener<Instrument> listener);
	/**
	 * Unsubscribe from instrument events
	 * @param instrument listener class 
	 */
	public void removeInstrumentListener(MarketListener<Instrument> listener);
}
