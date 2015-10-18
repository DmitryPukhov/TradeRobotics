package trading.data.model.neural;

import java.math.BigDecimal;

/**
 * Output result of neural network
 * @author pdg
 *
 */
public class OutputEntity {
	private BigDecimal low;
	private BigDecimal high;
	/**
	 * @return the low
	 */
	public BigDecimal getLow() {
		return low;
	}
	/**
	 * @param low the low to set
	 */
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	/**
	 * @return the high
	 */
	public BigDecimal getHigh() {
		return high;
	}
	/**
	 * @param high the high to set
	 */
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
}
