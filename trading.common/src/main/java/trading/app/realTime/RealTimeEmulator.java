package trading.app.realTime;

import java.util.Date;

/**
 * Pseudo real time provider. Emulates dynamic data, get it for example, from history
 * @author dima
 *
 */
public interface RealTimeEmulator extends RealTimeProvider {

	/**
	 * @return the startDate
	 */
	public abstract Date getStartDate();

	/**
	 * @param startDate the startDate to set
	 */
	public abstract void setStartDate(Date startDate);

}