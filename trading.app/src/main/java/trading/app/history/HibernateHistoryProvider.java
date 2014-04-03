package trading.app.history;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import trading.data.HibernateUtil;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * History data provider
 * 
 * @author dima
 * 
 */
public class HibernateHistoryProvider implements HistoryProvider {
	private Session hibernateSession;

	/**
	 * Ctor
	 * 
	 * @param adapter
	 */
	public HibernateHistoryProvider() {
		// Init hibernate session
		hibernateSession = HibernateUtil.getSessionFactory().openSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Instrument> findInstrumentAll() {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.INSTRUMENT_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<Instrument> instruments = query.list();
		return instruments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> findLevel1After(int instrumentId, Date startDate,
			int count) {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.LEVEL1_FIND_AFTER);
		query.setParameter(trading.data.Constants.QueryParamName.INSTRUMENT_ID,
				instrumentId);
		query.setParameter(trading.data.Constants.QueryParamName.START_TIME,
				new Timestamp(startDate.getTime()));
		query.setParameter(trading.data.Constants.QueryParamName.COUNT, count);

		@SuppressWarnings("unchecked")
		List<Level1> data = query.list();

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> findLevel1From(int instrumentId, Date startDate,
			int count) {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.LEVEL1_FIND_FROM);
		query.setParameter(trading.data.Constants.QueryParamName.INSTRUMENT_ID,
				instrumentId);
		query.setParameter(trading.data.Constants.QueryParamName.START_TIME,
				startDate);
		query.setParameter(trading.data.Constants.QueryParamName.COUNT, count);

		@SuppressWarnings("unchecked")
		List<Level1> data = query.list();

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> findLevel1Last(int instrumentId, int lastCount) {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.LEVEL1_FIND_LAST);
		// query.setParameter(trading.data.Constants.QueryParamName.COUNT,
		// lastCount);
		query.setParameter(trading.data.Constants.QueryParamName.INSTRUMENT_ID,
				instrumentId);
		query.setParameter(trading.data.Constants.QueryParamName.COUNT,
				lastCount);

		// query.setFirstResult(0);
		// query.setMaxResults(lastCount);

		@SuppressWarnings("unchecked")
		List<Level1> data = query.list();

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> findLevel1NotAfter(int instrumentId, Date endDate,
			int count) {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.LEVEL1_FIND_NOT_AFTER);
		query.setParameter(trading.data.Constants.QueryParamName.INSTRUMENT_ID,
				instrumentId);
		query.setParameter(trading.data.Constants.QueryParamName.END_TIME,
				endDate);
		query.setParameter(trading.data.Constants.QueryParamName.COUNT, count);

		@SuppressWarnings("unchecked")
		List<Level1> data = query.list();

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Level1> findLevel1Range(int instrumentId, Date start, Date end) {
		Query query = hibernateSession
				.getNamedQuery(trading.data.Constants.QueryName.LEVEL1_FIND_RANGE);
		query.setParameter(trading.data.Constants.QueryParamName.INSTRUMENT_ID,
				instrumentId);
		query.setParameter(trading.data.Constants.QueryParamName.START_TIME,
				start);
		query.setParameter(trading.data.Constants.QueryParamName.END_TIME, end);

		@SuppressWarnings("unchecked")
		List<Level1> data = query.list();

		return data;
	}

}
