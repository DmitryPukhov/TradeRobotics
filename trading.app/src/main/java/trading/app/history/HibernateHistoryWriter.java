package trading.app.history;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import trading.app.realTime.MarketListener;
import trading.app.realTime.RealTimeProvider;
import trading.data.HibernateUtil;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Receives data from adapter, writes to history
 * 
 * @author dima
 * 
 */
public class HibernateHistoryWriter implements HistoryWriter {
	/** Real time provider to get data from */
	private RealTimeProvider realTimeProvider = null;

	/** Hibernate session where to write data */
	private Session hibernateSession = null;

	/** Instruments */
	private List<Integer> instrumentIds = new ArrayList<Integer>();

	/** If enabled, write data, received from realTimeProvider */
	private boolean enabled = true;

	/**
	 * Listener to instrument data
	 */
	MarketListener<Instrument> instrumentListener = new MarketListener<Instrument>() {
		@Override
		public void OnMarketDataChanged(Instrument entity) {
			onInstrument(entity);

		}
	};

	/**
	 * Listener to level1 data
	 */
	MarketListener<List<Level1>> level1Listener = new MarketListener<List<Level1>>() {
		@Override
		public void OnMarketDataChanged(List<Level1> entities) {
			onLevel1(entities);
		}
	};

	/**
	 * Close session when being finalized
	 */
	public void finalize() {
		if (hibernateSession != null && hibernateSession.isOpen()) {
			hibernateSession.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getInstrumentIds() {
		return instrumentIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RealTimeProvider getRealTimeProvider() {
		return realTimeProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		// Init hibernate session
		hibernateSession = HibernateUtil.getSessionFactory().openSession();

		startListening();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Instrument update received
	 * 
	 * @param instrument
	 */
	protected void onInstrument(Instrument instrument) {
		// Write to database
		Session session = hibernateSession;
		// session.beginTransaction();
		if (!session.getTransaction().isActive()) {
			session.getTransaction().begin();
		}
		session.merge(instrument);
		session.getTransaction().commit();
	}

	/**
	 * Level1 data received
	 * 
	 * @param level1
	 */
	protected void onLevel1(List<Level1> level1s) {
		try {
			// Write to database
			Session session = hibernateSession;
			// session.beginTransaction();
			if (!session.getTransaction().isActive()) {
				session.getTransaction().begin();
			}
			// Save level1s
			for (Level1 level1 : level1s) {
				Instrument instrument = (Instrument) session.get(
						Instrument.class, level1.getInstrument().getId());
				// If instrument does not exist, save it to be updated later
				// from
				// instrument adapter
				if (instrument == null) {
					session.save(level1.getInstrument());
					// session.merge(level1.getInstrument());
				} else {
					level1.setInstrument(instrument);
				}
				// Save level1
				session.merge(level1);
			}
			// Commit
			session.getTransaction().commit();
		} catch (org.hibernate.ObjectNotFoundException ex) {
			// If not found => instrument does not exist
			System.err.println(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean isEnabled) {
		this.enabled = isEnabled;
		if (enabled) {
			startListening();
		} else {
			stopListening();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRealTimeProvider(RealTimeProvider provider) {
		this.realTimeProvider = provider;
	}

	/**
	 * Start market listening
	 */
	private void startListening() {
		// Add instrument listener
		realTimeProvider.addInstrumentListener(instrumentListener);
		// Listen level1 for all instruments
		for (int instrumentId : instrumentIds) {
			realTimeProvider.addLevel1Listener(instrumentId, level1Listener);
		}
	}

	/**
	 * Stop market listening
	 */
	private void stopListening() {
		// Do not listen data for instruments in list
		for (int instrumentId : instrumentIds) {
			realTimeProvider.removeLevel1Listener(instrumentId, level1Listener);
		}
		// Do not listen instrument infos
		realTimeProvider.removeInstrumentListener(instrumentListener);
	}

}
