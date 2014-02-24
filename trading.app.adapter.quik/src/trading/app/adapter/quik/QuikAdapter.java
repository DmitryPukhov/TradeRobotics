package trading.app.adapter.quik;

import trading.app.realTime.RealTimeProviderBase;
import trading.data.HibernateUtil;

/**
 * Adapter for Quik data. Quik exports data to Level1 table by ODBC. Adapter
 * listens new data appeared in database and fires events
 * 
 * @author pdg
 * 
 */
public class QuikAdapter extends RealTimeProviderBase {

	@Override
	public void start() {
		String connectionString = HibernateUtil.getConnectionString();
		

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}


}
