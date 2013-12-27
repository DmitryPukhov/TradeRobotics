package trading.app.adapter.plaza2;

import ru.micexrts.cgate.Connection;
import ru.micexrts.cgate.ErrorCode;
import ru.micexrts.cgate.ISubscriber;
import ru.micexrts.cgate.Listener;
import ru.micexrts.cgate.messages.Message;
import trading.app.adapter.plaza2.scheme.FutInfo;
import trading.data.model.Instrument;
import trading.app.realTime.*;


/**
 * Instrument util
 * 
 * @author dima
 * 
 */
class InstrumentHelper {

	public static final String MESSAGE_NAME = "fut_instruments";

	/**
	 * Convert plaza fut_instruments message to Instrument
	 * 
	 * @param schemeEntity
	 * @return
	 */
	private static Instrument convert(FutInfo.fut_instruments schemeEntity) {
	
		Instrument entity = new Instrument();

		entity.setId(schemeEntity.get_isin_id());
		entity.setShortCode(schemeEntity.get_short_isin());
		entity.setCode(schemeEntity.get_isin());
		entity.setName(schemeEntity.get_name());

		return entity;

	}

	/**
	 * Message received
	 */
	public Instrument convert(Message message) {
		// Convert scheme to entity
		FutInfo.fut_instruments schemeEntity = new FutInfo.fut_instruments(
				message.getData());
		Instrument instrument = convert(schemeEntity);
		System.out.println(instrument.getCode());
		return instrument;
	}

}
