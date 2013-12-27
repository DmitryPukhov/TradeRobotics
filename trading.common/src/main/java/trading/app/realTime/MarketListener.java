package trading.app.realTime;

/**
 * Class who listens adapter for market changes
 * @author dima
 *
 * @param <T>
 */
public interface MarketListener<T> {
	/**
	 * Some data changed on market event
	 * @param entity
	 */
	public void OnMarketDataChanged(T entity);
}
