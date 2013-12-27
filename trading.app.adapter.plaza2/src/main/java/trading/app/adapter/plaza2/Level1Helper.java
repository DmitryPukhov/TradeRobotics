package trading.app.adapter.plaza2;

import ru.micexrts.cgate.messages.Message;
import trading.app.adapter.plaza2.scheme.FutCommon;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Level1 util
 * 
 * @author dima
 * 
 */
public class Level1Helper  {
	/**
	 * Plaza2 message name for level1
	 */
	public static final String MESSAGE_NAME = "common";
	

	/**
	 * Convert plaza2 entity to level1 object
	 * 
	 * @param schemeEntity
	 * @return
	 */
	public static Level1 convert(FutCommon.common schemeEntity) {
		Level1 entity = new Level1();

		// Set to new instrument with proper id. Will be processed in listeners
		Instrument instrument = new Instrument();
		instrument.setId(schemeEntity.get_isin_id());
		//instrument.setName(instrument.getId().toString());
		// Set properties
		entity.setInstrument(instrument);
		// time
		//Timestamp date = new Timestamp(schemeEntity.get_deal_time().getTime());

		entity.setDate(schemeEntity.get_deal_time());
		//Time lastTime = new Time(date.getTime());
		entity.setLastTime(schemeEntity.get_deal_time());
		// Price
		entity.setLastPrice(schemeEntity.get_price());
		entity.setLastPriceDelta(schemeEntity.get_trend());
		entity.setLastSize(schemeEntity.get_amount());
		// Ask and bid
		entity.setAsk(schemeEntity.get_best_sell());
		entity.setAskSize(schemeEntity.get_amount_sell());
		entity.setBid(schemeEntity.get_best_buy());
		entity.setBidSize(schemeEntity.get_amount_buy());
		return entity;

	}

	/**
	 * Convert plaza2 message to Level1 entity
	 */
	public Level1 convert(Message message) {
		FutCommon.common schemeEntity = new FutCommon.common(message.getData());
		Level1 level1 = convert(schemeEntity);
		return level1;
	}
}
