package trading.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Temp table level1 exported from Quik application by odbc Will be added to
 * normal Level1 table
 * 
 * @author dima
 * 
 */
@Entity
@Table(name = "level1_new")
public class Level1New implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private BigDecimal ask;
	private Integer askSize;
	private BigDecimal bid;
	private Integer bidSize;
	private Date date;
	private BigDecimal lastPrice;
	private BigDecimal lastPriceDelta;
	private Integer lastSize;
	private Date lastTime;
	private String instrumentCode;

	public Level1New() {
	}

	public BigDecimal getAsk() {
		return this.ask;
	}

	@Column(name = "ask_size")
	public Integer getAskSize() {
		return this.askSize;
	}

	public BigDecimal getBid() {
		return this.bid;
	}

	@Column(name = "bid_size")
	public Integer getBidSize() {
		return this.bidSize;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}

	/**
	 * Quik does not know about our instrument id and uses string instrument
	 * code.
	 * 
	 * @return the instrumentCode
	 */
	@Column(name = "instrument_code")
	public String getInstrumentCode() {
		return instrumentCode;
	}

	@Column(name = "last_price")
	public BigDecimal getLastPrice() {
		return this.lastPrice;
	}

	@Column(name = "last_price_delta")
	public BigDecimal getLastPriceDelta() {
		return this.lastPriceDelta;
	}

	@Column(name = "last_size")
	public Integer getLastSize() {
		return this.lastSize;
	}

	@Column(name = "last_time")
	@Temporal(TemporalType.TIME)
	public Date getLastTime() {
		return this.lastTime;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public void setAskSize(Integer askSize) {
		this.askSize = askSize;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public void setBidSize(Integer bidSize) {
		this.bidSize = bidSize;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param instrumentCode
	 *            the instrumentCode to set
	 */
	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setLastPriceDelta(BigDecimal lastPriceDelta) {
		this.lastPriceDelta = lastPriceDelta;
	}

	public void setLastSize(Integer lastSize) {
		this.lastSize = lastSize;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	@Override
	public String toString() {
		return String.format("Level1 data: date=%s, price=%s, volume=%s...",
				date, lastPrice, lastSize);
	}
}
