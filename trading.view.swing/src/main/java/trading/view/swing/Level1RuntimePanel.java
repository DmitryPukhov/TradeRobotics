package trading.view.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

//import trading.app.TradingApplication1;
import trading.app.TradingApplicationContext;
import trading.app.history.HistoryProvider;
import trading.app.history.HistoryWriter;
import trading.app.realTime.MarketListener;
import trading.app.realTime.RealTimeProvider;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * Runtime data panel
 * 
 * @author dima
 * 
 */
public class Level1RuntimePanel extends JPanel implements
		MarketListener<List<Level1>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Capture real time data action
	 */
	@SuppressWarnings("serial")
	private class CaptureAction extends AbstractAction {
		public CaptureAction() {
			putValue(NAME, "Listen");
			putValue(SHORT_DESCRIPTION, "Listen market data");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractButton source = (AbstractButton) e.getSource();
			if (source.isSelected()) {
				historyAction.setEnabled(false);
				Instrument selectedInstrument = getInstrument();
				instrumentComboBox.setEnabled(false);
				level1Chart.clear();

				realTimeProvider.addLevel1Listener(selectedInstrument.getId(),
						Level1RuntimePanel.this);

			} else {
				Instrument selectedInstrument = (Instrument) instrumentComboBox
						.getSelectedItem();
				realTimeProvider.removeLevel1Listener(
						selectedInstrument.getId(), Level1RuntimePanel.this);
				instrumentComboBox.setEnabled(true);
				historyAction.setEnabled(true);
			}
		}
	}

	/**
	 * Connect to data provider action
	 * 
	 * @author dimaString title
	 */
	@SuppressWarnings("serial")
	private class ConnectAction extends AbstractAction {
		public ConnectAction() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Connect to data provider");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractButton source = (AbstractButton) e.getSource();
			if (source.isSelected()) {
				realTimeProvider.start();
			} else {
				realTimeProvider.stop();

			}
		}
	}

	/**
	 * Load history from data provider
	 * 
	 * @author dima
	 * 
	 */
	@SuppressWarnings("serial")
	private class HistoryAction extends AbstractAction {
		public HistoryAction() {
			putValue(NAME, "Load history");
			putValue(SHORT_DESCRIPTION, "Loads history");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			level1Chart.clear();
			// Load history
			List<Level1> data = historyProvider.findLevel1Last(getInstrument()
					.getId(), Constants.MAX_CHART_ITEM_C0UNT);
			level1Chart.addLevel1(data);
		}
	}

	/**
	 * Application context to store instrument there
	 */
	private TradingApplicationContext tradingApplicationContext;

	/**
	 * History data provider to get data for chart
	 */
	private HistoryProvider historyProvider;

	/**
	 * History writer. If write history checkbox checked, use this writer to
	 * persist data
	 */
	private HistoryWriter historyWriter;

	/**
	 * Realtime data provider
	 */
	private RealTimeProvider realTimeProvider;

	/**
	 * Level1 ticks chart
	 */
	private Level1Chart level1Chart;

	/**
	 * Instrument selector compbo box
	 */
	private JComboBox<Instrument> instrumentComboBox;
	// private JPanel frame;

	/**
	 * Connect to data provider swing action
	 */
	private final Action connectAction = new ConnectAction();

	/**
	 * Listen realTimeProvider swing action
	 */
	private final Action listenAction = new CaptureAction();

	/**
	 * Use history instead of real time data swing action
	 */
	private final Action historyAction = new HistoryAction();

	/**
	 * Enable/disable writing to history configuration settings
	 */
	private boolean isWriteEnabled = false;

	/**
	 * Enable/disable writing to history check box
	 */
	private JCheckBox writeHistoryCheckBox;

	/**
	 * Empty constructor for window builder
	 */
	public Level1RuntimePanel() {
		initialize();
	}

	/**
	 * Ctor
	 * 
	 * @param context
	 *            Trading application context
	 */
	public Level1RuntimePanel(
			TradingApplicationContext tradingApplicationContext,
			HistoryProvider historyProvider, HistoryWriter historyWriter,
			RealTimeProvider realTimeProvider) {
		this.tradingApplicationContext = tradingApplicationContext;
		this.historyProvider = historyProvider;
		this.historyWriter = historyWriter;
		this.realTimeProvider = realTimeProvider;

		// Listen instrument info
		realTimeProvider
				.addInstrumentListener(new MarketListener<Instrument>() {
					@Override
					public void OnMarketDataChanged(Instrument entity) {
						Instrument existing = null;

						// Try to select existing item first
						for (int i = 0; i < instrumentComboBox.getItemCount(); i++) {
							Instrument current = instrumentComboBox
									.getItemAt(i);
							if (current.equals(entity)) {
								existing = current;
								break;
							}
						}
						if (existing == null) {
							instrumentComboBox.addItem(entity);
						}
					}
				});

		// Standard initialization call
		initialize();
	}

	/**
	 * Returns current instrument
	 * 
	 * @return
	 */
	private Instrument getInstrument() {
		Instrument selectedInstrument = (Instrument) instrumentComboBox
				.getSelectedItem();
		return selectedInstrument;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		setBounds(100, 100, 1024, 768);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		// Main chart
		level1Chart = new Level1Chart("No instrument selected");
		springLayout.putConstraint(SpringLayout.NORTH, level1Chart, 50,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, level1Chart, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, level1Chart, 0,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, level1Chart, 0,
				SpringLayout.EAST, this);
		level1Chart.setTitle("");
		add(level1Chart);

		JPanel controlPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, controlPanel, 0,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, controlPanel, 0,
				SpringLayout.WEST, level1Chart);
		springLayout.putConstraint(SpringLayout.SOUTH, controlPanel, 50,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, controlPanel, 0,
				SpringLayout.EAST, level1Chart);
		add(controlPanel);

		// Connect to provider button
		JToggleButton connectButton = new JToggleButton("Connect");
		connectButton.setToolTipText("Connect to data provider");
		connectButton.setHorizontalAlignment(SwingConstants.LEFT);
		connectButton.setAction(connectAction);

		// Instrument selector combo
		instrumentComboBox = new JComboBox<Instrument>();
		instrumentComboBox.setToolTipText("Select instrument to work with");
		instrumentComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Set caption to selected instrument
				Instrument instrument = (Instrument) instrumentComboBox
						.getSelectedItem();
				level1Chart.setTitle(instrument.toString());
				// Add to history writer
				historyWriter.getInstrumentIds().clear();
				historyWriter.getInstrumentIds().add(
						new Integer(instrument.getId()));
				isWriteEnabled = false;
				writeHistoryCheckBox.setSelected(isWriteEnabled);

			}
		});
		// Load history button
		JButton historyButton = new JButton("History");
		historyButton.setToolTipText("Load history from database");
		historyButton.setAction(historyAction);

		// Listen market button
		JToggleButton listenButon = new JToggleButton("Listen");
		listenButon.setToolTipText("Capture real time data from market");
		listenButon.setAction(listenAction);
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		controlPanel.add(connectButton);
		controlPanel.add(instrumentComboBox);
		controlPanel.add(historyButton);
		controlPanel.add(listenButon);

		// History write checkbox
		writeHistoryCheckBox = new JCheckBox("Write history");
		writeHistoryCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Enable/disable history writer
				JCheckBox source = (JCheckBox) e.getSource();
				isWriteEnabled = source.isSelected();
				historyWriter.setEnabled(isWriteEnabled);
			}
		});
		historyWriter.setEnabled(isWriteEnabled);
		writeHistoryCheckBox.setToolTipText("Write to database when listening");
		writeHistoryCheckBox.setSelected(false);
		controlPanel.add(writeHistoryCheckBox);
	}

	/**
	 * Level1 changed event
	 */
	@Override
	public void OnMarketDataChanged(List<Level1> level1s) {
		for (Level1 level1 : level1s) {
			// Add prices to chart
			level1Chart.addLevel1(level1);
		}
	}

}
