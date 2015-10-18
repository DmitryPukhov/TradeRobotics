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
	private int level1WindowSize = 100;

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

	/**
	 * Get level1 item count in data window
	 * 
	 * @return the level1WindowSize
	 */
	public Integer getLevel1WindowSize() {
		return level1WindowSize;
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

}
