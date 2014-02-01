package trading.app.history;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import trading.app.TradingApplicationContext;
import trading.app.realTime.RealTimeEmulator;
import trading.app.realTime.RealTimeProviderBase;
import trading.data.model.Level1;

/**
 * Emulates real time adapter using history data
 * 
 * @author dima
 * 
 */
public class HistoricalEmulator extends RealTimeProviderBase implements
		RealTimeEmulator {
	/**
	 * Level1 data window. FIFO queue.
	 */
	private List<Level1> dataWindow = new ArrayList<Level1>();

	/**
	 * Number of items to store in data window. If after having been added new
	 * item the size is above, first item will be removed.
	 */
	private int dataWindowSize = 0;

	/**
	 * Size of buffer. Emulator loads BUFFER_SIZE items, generates events
	 * consequently, loads next data pack and again...
	 */
	private final static int BUFFER_SIZE = 10000;

	/**
	 * This flat tells emulator cycle to stop
	 */
	private boolean stopFlag = false;

	/**
	 * History provider to get emulation data from.
	 */
	private HistoryProvider historyProvider;

	/**
	 * Application context with instrument info
	 */
	private TradingApplicationContext context;

	/**
	 * Historical moment from which we start real time emulation
	 */
	private Date startDate;

	/**
	 * Construct with history provider
	 * 
	 * @param historyProvider
	 */
	public HistoricalEmulator(TradingApplicationContext context,
			HistoryProvider historyProvider) {
		this.context = context;
		this.historyProvider = historyProvider;
	}

	/**
	 * @return the dataWindow
	 */
	public List<Level1> getDataWindow() {
		return dataWindow;
	}

	/**
	 * @return the dataWindowSize
	 */
	public int getDataWindowSize() {
		return dataWindowSize;
	}

	/**
	 * @see trading.app.realtime.HistoricalEmulator#getStartDate()
	 */
	@Override
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param dataWindow
	 *            the dataWindow to set
	 */
	public void setDataWindow(List<Level1> dataWindow) {
		this.dataWindow = dataWindow;
	}

	/**
	 * @param dataWindowSize
	 *            the dataWindowSize to set
	 */
	public void setDataWindowSize(int dataWindowSize) {
		this.dataWindowSize = dataWindowSize;
	}

	/**
	 * @see trading.app.realtime.HistoricalEmulator#setStartDate(java.util.Date)
	 */
	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Goes along data and generates ticks for every level1
	 */
	@Override
	public void start() {
		if (context.getInstrument() == null) {
			throw new IllegalArgumentException("Instrument is null");
		}

		int instrumentId = context.getInstrument().getId();
		stopFlag = false;
		dataWindow.clear();
		// Set first buffer startDate one millisecond before startDate
		// to include start date to first result
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startDate.getTime() - 1);
		Date lastDate = cal.getTime();
		// Main cycle: load buffer - generate event for every item in buffer
		while (!stopFlag) {
			List<Level1> level1s = historyProvider.findLevel1After(context
					.getInstrument().getId(), lastDate, BUFFER_SIZE);

			// Buffer cycle
			for (Level1 level1 : level1s) {
				if (stopFlag) {
					break;
				}
				lastDate = level1.getDate();

				// Add to data window
				dataWindow.add(level1);
				if (dataWindow.size() > dataWindowSize) {
					dataWindow.remove(0);
				}
				// Emulate real time tick
				fireLevel1ChangedEvent(instrumentId, level1);
			}
		}
	}

	/**
	 * @see trading.app.realTime.RealTimeProviderBase#stop()
	 */
	@Override
	public void stop() {
		stopFlag = true;
	}
}
