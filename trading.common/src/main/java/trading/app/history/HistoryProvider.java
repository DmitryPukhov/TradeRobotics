package trading.app.history;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Provides history
 * @author pdg
 *
 */
public interface HistoryProvider {

	/**
	 * Get all instrument from database
	 * @return
	 */
	public abstract List<Instrument> findInstrumentAll();

	/**
	 * Find level 1 items with date > startDate
	 * @param instrumentId
	 * @param startDate latest date before our range. Not included in result
	 * @param count item count
	 * @return
	 */
	public abstract List<Level1> findLevel1After(int instrumentId, Date startDate, int count);	
	
	/**
	 * Find level1 items with date >= startData
	 * @param instrumentId
	 * @param startDate first date included in result.
	 * @param count
	 * @return
	 */
	public abstract List<Level1> findLevel1From(int instrumentId, Date startDate, int count);
	
	/**
	 * Find level 1 items with date <= endDate
	 * @param instrumentId
	 * @param endDate latest date included in result
	 * @param count item count
	 * @return
	 */
	public abstract List<Level1> findLevel1NotAfter(int instrumentId, Date endDate, int count);	
	/**
	 * Get last n level1 items
	 * @param lastCount
	 * @return
	 */
	public abstract List<Level1> findLevel1Last(int instrumentId, int lastCount);
	/**
	 * Get level1 data for specific interval
	 * @param start
	 * @param end
	 * @return
	 */
	public abstract List<Level1> findLevel1Range(int instrumentId, Date start, Date end);

}