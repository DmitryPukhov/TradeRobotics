package trading.app.adapter.quik;

import java.util.Date;
import java.util.List;

import trading.app.TradingApplicationContext;
import trading.app.history.HistoryProvider;
import trading.app.realTime.RealTimeProviderBase;
import trading.data.model.Level1;

/**
 * Adapter for Quik data. Quik exports data to Level1 table by ODBC. Adapter
 * listens new data appeared in database and fires events.
 * 
 * @author pdg
 * 
 */
public class QuikAdapter extends RealTimeProviderBase {
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

	/** {@InheritDoc} */
	@Override
	public void start() {
		List<Level1> level1s = historyProvider.findLevel1After(context.getInstrument().getId(), lastDateTime, Integer.MAX_VALUE);
		fireLevel1ChangedEvent(context.getInstrument().getId(), level1s);
	}

	/** {@InheritDoc} */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
