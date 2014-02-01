/**
 * 
 */
package trading.app;

import trading.data.model.Instrument;

/**
 * @author dima Trading application context class
 */
public class TradingApplicationContext {

	private Instrument instrument;

	/**
	 * @return the main instrument
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * @param instrument
	 *            the instrument to set
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

}
