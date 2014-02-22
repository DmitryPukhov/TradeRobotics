package trading.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private Instrument instrument;

	public Level1New() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getAsk() {
		return this.ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	@Column(name = "ask_size")
	public Integer getAskSize() {
		return this.askSize;
	}

	public void setAskSize(Integer askSize) {
		this.askSize = askSize;
	}

	public BigDecimal getBid() {
		return this.bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	@Column(name = "bid_size")
	public Integer getBidSize() {
		return this.bidSize;
	}

	public void setBidSize(Integer bidSize) {
		this.bidSize = bidSize;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "last_price")
	public BigDecimal getLastPrice() {
		return this.lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	@Column(name = "last_price_delta")
	public BigDecimal getLastPriceDelta() {
		return this.lastPriceDelta;
	}

	public void setLastPriceDelta(BigDecimal lastPriceDelta) {
		this.lastPriceDelta = lastPriceDelta;
	}

	@Column(name = "last_size")
	public Integer getLastSize() {
		return this.lastSize;
	}

	public void setLastSize(Integer lastSize) {
		this.lastSize = lastSize;
	}

	@Column(name = "last_time")
	@Temporal(TemporalType.TIME)
	public Date getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	// bi-directional many-to-one association to Instrument
	@ManyToOne()
	@JoinColumn(name = "instrument_id", nullable = false)
	public Instrument getInstrument() {
		return this.instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	@Override
	public String toString() {
		return String.format("Level1 data: date=%s, price=%s, volume=%s...",
				date, lastPrice, lastSize);
	}
}
