package trading.app.adapter.quik;

import java.util.Date;
import java.util.List;

import trading.app.TradingApplicationContext;
import trading.app.history.HistoryProvider;
import trading.app.realTime.RealTimeProviderBase;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Adapter for Quik data. Quik exports data to Level1 table by ODBC. Adapter
 * listens new data appeared in database and fires events.
 * 
 * @author pdg
 * 
 */
public class QuikAdapter extends RealTimeProviderBase {
	/** Flag to interrupt database capture cycle */
	private boolean stopFlag = false;

	/** Application context. Contains instrument to listen to */
	private TradingApplicationContext context;

	/** History provider to get data from */
	private HistoryProvider historyProvider;

	/** Last data received time */
	private Date lastDateTime = new Date();

	/**
	 * Constructor for quik adapter
	 * 
	 * @param context
	 * @param historyProvider
	 */
	public QuikAdapter(TradingApplicationContext context,
			HistoryProvider historyProvider) {
		this.context = context;
		this.historyProvider = historyProvider;
	}

	/** Start database capture cycle */
	@Override
	public void start() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				stopFlag = false;
				// Load instruments, fire event
				loadInstruments();

				// Load data for instrument
				for (; !stopFlag;) {
					Instrument instrument = context.getInstrument();
					if (instrument == null) {
						continue;
					}
					// Get last prices
					Date lastDateTime = QuikAdapter.this.lastDateTime;
					
					List<Level1> level1s = historyProvider.findLevel1After(
							instrument.getId(), lastDateTime, context.getLevel1WindowSize());
					if(level1s.isEmpty()){
						continue;
					}
					// Update last received time
					Level1 lastLevel1 = level1s.get(level1s.size()-1);
					QuikAdapter.this.lastDateTime = new Date(lastLevel1.getDate().getTime() + lastLevel1.getLastTime().getTime());
					// Fire event
					fireLevel1ChangedEvent(instrument.getId(), level1s);
				}
			}
		});
		thread.start();
	}

	/**
	 * Get instruments from database, fire event
	 */
	private void loadInstruments() {
		for (Instrument instrument : historyProvider.findInstrumentAll()) {
			fireInstrumentChangedEvent(instrument);
		}
	}

	/** {@InheritDoc} */
	@Override
	public void stop() {
		stopFlag = true;

	}

}
