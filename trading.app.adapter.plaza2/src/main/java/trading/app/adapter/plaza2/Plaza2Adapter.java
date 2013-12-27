package trading.app.adapter.plaza2;

import ru.micexrts.cgate.Connection;
import ru.micexrts.cgate.ErrorCode;
import ru.micexrts.cgate.ISubscriber;
import ru.micexrts.cgate.Listener;
import ru.micexrts.cgate.MessageType;
import ru.micexrts.cgate.messages.AbstractDataMessage;
import ru.micexrts.cgate.messages.Message;
import trading.app.realTime.RealTimeProvider;
import trading.app.realTime.RealTimeProviderBase;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * RealTime provider from plaza2
 * 
 * @author dima
 * 
 */
public class Plaza2Adapter extends RealTimeProviderBase implements ISubscriber {
	/**
	 * Plaza2 socket client. Plaza2 adapter converts plaza2 messages to model
	 * entities and send events
	 */
	private Plaza2Client plaza2Client;

	/**
	 * Instrument info adapter
	 */
	private InstrumentHelper instrumentAdapter = new InstrumentHelper();

	/**
	 * Adapter for level1 data(price, bid, ask and volumes)
	 */
	private Level1Helper level1Adapter = new Level1Helper();

	/**
	 * Ctor, creates plaza2 client member
	 */
	public Plaza2Adapter() {
		plaza2Client = new Plaza2Client(this);
	}

	/**
	 * Process new message
	 */
	public int onMessage(Connection conn, Listener listener, Message message) {
		// Process by message type
		switch (message.getType()) {
		// Data messages
		case MessageType.MSG_DATA:
		case MessageType.MSG_STREAM_DATA:
			processDataMessage(message);
			// System.out.println(message);
			break;
		// Timeout
		case MessageType.MSG_P2MQ_TIMEOUT:
			System.out.println("Timeout message");
			break;
		// Default
		default:
			// System.out.println(message);
			// System.out.println(String.format("Message type: %d",
			// message.getType()));
		}
		return ErrorCode.OK;
	}

	/**
	 * Is called from onMessage function if message type is StreamData or Data
	 */
	private void processDataMessage(Message message) {
		// Get adapter by message name
		AbstractDataMessage msgData = (AbstractDataMessage) message;
		String messageName = msgData.getMsgName();
		switch (messageName) {
		// Process instrument
		case InstrumentHelper.MESSAGE_NAME:
			Instrument instrument = instrumentAdapter.convert(message);
			fireInstrumentChangedEvent(instrument);
			break;
		// Process level1
		case Level1Helper.MESSAGE_NAME:
			Level1 level1 = level1Adapter.convert(message);
			fireLevel1ChangedEvent(level1.getInstrument().getId(), level1);
			break;
		}
	}

	/**
	 * @see RealTimeProvider#start()
	 */
	public void start() {
		this.plaza2Client = new Plaza2Client(this);
		this.plaza2Client.connect();
	}

	/**
	 * @see RealTimeProvider#stop()
	 */
	public void stop() {
		this.plaza2Client.disconnect();
	}
}
