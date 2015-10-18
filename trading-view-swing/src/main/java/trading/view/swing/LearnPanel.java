package trading.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import trading.app.history.HistoryProvider;
import trading.app.neural.NeuralContext;
import trading.app.neural.NeuralService;
import trading.app.neural.events.TrainCompletedEvent;
import trading.app.neural.events.TrainIterationCompletedEvent;
import trading.app.neural.events.TrainStartedEvent;
import trading.data.model.Instrument;

import com.google.common.eventbus.Subscribe;

/**
 * NN learning panel with error chart and start/stop functionality
 * 
 * @author dima
 * 
 */
public class LearnPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Neural context with training settings: input size, prediction size...
	 */
	private final NeuralContext neuralContext;

	/**
	 * Neural service to train network
	 */
	private final NeuralService neuralService;

	/**
	 * History provider to load instruments for instrument combo box
	 */
	private final HistoryProvider historyProvider;

	/**
	 * Learning progrress bar control
	 */
	private final JProgressBar learnProgressBar;

	/**
	 * Epoch x of y label
	 */
	private final JLabel lblEpoch;

	/**
	 * Last epoch duration label
	 */
	private final JLabel lblLastEpochTime;

	/**
	 * Whole learning time label
	 */
	private final JLabel lblTotalTime;

	/**
	 * Instrument selector combo box
	 */
	private final JComboBox<Instrument> instrumentComboBox;

	/**
	 * Start learning button
	 */
	JToggleButton learnButton;

	/**
	 * TextField to set max epoch count
	 */
	private final JFormattedTextField maxEpochCountText;

	/**
	 * Calculated last iteration error label
	 */
	private JLabel lblLastError;

	/**
	 * Error after iterations chart
	 */
	private JFreeChart errorChart;

	/**
	 * Data series for error chart
	 */
	private XYSeries errorXYSeries;

	/**
	 * TextField to set training last bound
	 */
	private final JFormattedTextField trainingEndDateTime;

	/**
	 * Format of date displayed on charts
	 */
	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * DateFormat object for charts
	 */
	private final static DateFormat dateFormat = new SimpleDateFormat(
			DATE_FORMAT);

	/**
	 * Create the panel.
	 * 
	 * @param context
	 *            Neural network context to work with
	 */
	public LearnPanel(NeuralContext context, final NeuralService neuralService,
			HistoryProvider historyProvider) {
		// Store services objects
		this.neuralService = neuralService;
		this.neuralContext = context;
		this.historyProvider = historyProvider;
		this.neuralService.getEventBus().register(this);

		// Set up form layout
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		// Learn progress bar
		learnProgressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.NORTH, learnProgressBar, 75,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, learnProgressBar, 12,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, learnProgressBar, -162,
				SpringLayout.EAST, this);
		add(learnProgressBar);

		// Start learning button
		learnButton = new JToggleButton("Learn");
		springLayout.putConstraint(SpringLayout.NORTH, learnButton, 70,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, learnButton, -125,
				SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, learnButton, 100,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, learnButton, -27,
				SpringLayout.EAST, this);
		learnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (learnButton.isSelected()) {
					trainNetwork();
				} else {
					stopNetworkTraining();
				}
			}
		});
		add(learnButton);

		// Epoch information label
		lblEpoch = new JLabel("Epoch 0 of 0");
		springLayout.putConstraint(SpringLayout.NORTH, lblEpoch, 29,
				SpringLayout.SOUTH, learnProgressBar);
		springLayout.putConstraint(SpringLayout.WEST, lblEpoch, 10,
				SpringLayout.WEST, this);
		add(lblEpoch);

		// Last epoch duration label
		lblLastEpochTime = new JLabel("Last epoch: 0 ms");
		springLayout.putConstraint(SpringLayout.NORTH, lblLastEpochTime, 0,
				SpringLayout.NORTH, lblEpoch);
		springLayout.putConstraint(SpringLayout.WEST, lblLastEpochTime, 109,
				SpringLayout.EAST, lblEpoch);
		add(lblLastEpochTime);

		// Total learning time info label
		lblTotalTime = new JLabel("Total time: 0 sec");
		springLayout.putConstraint(SpringLayout.NORTH, lblTotalTime, 6,
				SpringLayout.SOUTH, lblEpoch);
		springLayout.putConstraint(SpringLayout.WEST, lblTotalTime, 0,
				SpringLayout.WEST, lblEpoch);
		add(lblTotalTime);

		// Instrument selector combo box with label
		instrumentComboBox = new JComboBox<Instrument>();
		springLayout.putConstraint(SpringLayout.NORTH, instrumentComboBox, 7,
				SpringLayout.NORTH, this);
		instrumentComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (neuralContext != null) {
					Instrument instrument = (Instrument) instrumentComboBox
							.getSelectedItem();
					neuralContext.setInstrument(instrument);
					updateView();
				}
			}
		});
		add(instrumentComboBox);

		JLabel lblInstrument = new JLabel("Instrument:");
		springLayout.putConstraint(SpringLayout.NORTH, lblInstrument, 3,
				SpringLayout.NORTH, instrumentComboBox);
		springLayout.putConstraint(SpringLayout.EAST, lblInstrument, -13,
				SpringLayout.WEST, instrumentComboBox);
		add(lblInstrument);

		// Max epoch count
		maxEpochCountText = new JFormattedTextField();
		springLayout.putConstraint(SpringLayout.EAST, maxEpochCountText, -187,
				SpringLayout.EAST, this);
		maxEpochCountText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateContextFromView();
			}
		});
		maxEpochCountText.setText("10");
		add(maxEpochCountText);

		JLabel lblEpochCount = new JLabel("Epoch count");
		springLayout.putConstraint(SpringLayout.EAST, lblEpochCount, -11,
				SpringLayout.WEST, maxEpochCountText);
		add(lblEpochCount);

		// Last error label
		lblLastError = new JLabel("Last error: 0");
		springLayout.putConstraint(SpringLayout.NORTH, lblLastError, 6,
				SpringLayout.SOUTH, lblTotalTime);
		springLayout.putConstraint(SpringLayout.WEST, lblLastError, 10,
				SpringLayout.WEST, this);
		add(lblLastError);

		// Training end time
		trainingEndDateTime = new JFormattedTextField();
		springLayout.putConstraint(SpringLayout.EAST, trainingEndDateTime,
				-471, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, maxEpochCountText, 0,
				SpringLayout.NORTH, trainingEndDateTime);
		springLayout.putConstraint(SpringLayout.WEST, maxEpochCountText, 245,
				SpringLayout.EAST, trainingEndDateTime);
		springLayout.putConstraint(SpringLayout.NORTH, lblEpochCount, 0,
				SpringLayout.NORTH, trainingEndDateTime);
		springLayout.putConstraint(SpringLayout.WEST, lblEpochCount, 165,
				SpringLayout.EAST, trainingEndDateTime);
		springLayout.putConstraint(SpringLayout.WEST, instrumentComboBox, 0,
				SpringLayout.WEST, trainingEndDateTime);
		add(trainingEndDateTime);

		JLabel lblLastDatetime = new JLabel("Last date&time:");
		springLayout.putConstraint(SpringLayout.WEST, trainingEndDateTime, 12,
				SpringLayout.EAST, lblLastDatetime);
		springLayout.putConstraint(SpringLayout.SOUTH, lblLastDatetime, -12,
				SpringLayout.NORTH, learnProgressBar);
		springLayout.putConstraint(SpringLayout.NORTH, trainingEndDateTime, -3,
				SpringLayout.NORTH, lblLastDatetime);
		springLayout.putConstraint(SpringLayout.WEST, lblLastDatetime, 0,
				SpringLayout.WEST, learnProgressBar);
		add(lblLastDatetime);

		// Reset network button
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				neuralService.resetTraining();
			}
		});
		resetButton.setToolTipText("Reset training. ");
		springLayout.putConstraint(SpringLayout.NORTH, resetButton, 18,
				SpringLayout.SOUTH, learnButton);
		springLayout.putConstraint(SpringLayout.WEST, resetButton, 0,
				SpringLayout.WEST, learnButton);
		springLayout.putConstraint(SpringLayout.EAST, resetButton, 0,
				SpringLayout.EAST, learnButton);
		add(resetButton);
		initInstrumentComboBox();

		// Chart creation
		createChart();
		// ChartPanel
		ChartPanel chartPanel = new ChartPanel(errorChart);
		springLayout.putConstraint(SpringLayout.NORTH, chartPanel, 200,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, chartPanel, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, chartPanel, 0,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, chartPanel, 0,
				SpringLayout.EAST, this);
		add(chartPanel);

		// Update view
		updateView();
	}

	/**
	 * Init JFreeChart
	 */
	private void createChart() {
		// Create dataset and series
		// TableXYDataset ds = new DefaultTableXYDataset();
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		errorXYSeries = new XYSeries("Error");
		xySeriesCollection.addSeries(errorXYSeries);
		errorXYSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);
		// Create chart
		errorChart = ChartFactory.createXYLineChart("Error value", "Epoch",
				"Error, %", xySeriesCollection, PlotOrientation.VERTICAL, true,
				true, true);
		XYPlot plot = (XYPlot) errorChart.getPlot();
		plot.getRangeAxis().setAutoRange(true);
		// plot.getDomainAxis().setRange(1,
		// neuralContext.getTrainingContext().getMaxEpochCount());
		// X auto range to proper display last error values
		plot.getDomainAxis().setAutoRange(true);
	}

	/**
	 * Initiate instrument combo box
	 */
	private void initInstrumentComboBox() {
		if (neuralContext == null) {
			return;
		}
		// Load instruments from data
		List<Instrument> instruments = historyProvider.findInstrumentAll();
		// Add instruments to combo box
		ComboBoxModel<Instrument> model = new DefaultComboBoxModel<Instrument>(
				instruments.toArray(new Instrument[] {}));
		instrumentComboBox.setModel(model);
		if (instrumentComboBox.getModel().getSize() > 0) {
			instrumentComboBox.setSelectedIndex(0);
		}
	}

	/**
	 * Train completed, update view
	 * 
	 * @param e
	 */
	@Subscribe
	public void onTrainCompleted(TrainCompletedEvent e) {
		learnButton.setSelected(false);
		maxEpochCountText.setEnabled(true);
		instrumentComboBox.setEnabled(true);
	}

	@Subscribe
	public synchronized void onTrainIterationCompleted(
			TrainIterationCompletedEvent event) {
		updateView(event);
		double x = new Integer(event.getLastEpoch()).doubleValue();
		errorXYSeries.add(x, event.getLastError() / 0.01);
	}

	/**
	 * Train started, update view
	 * 
	 * @param e
	 */
	@Subscribe
	public void onTrainStarted(TrainStartedEvent e) {

	}

	/**
	 * Reset context
	 */
	private void resetTrainingContext() {
		errorXYSeries.clear();
		Integer maxEpochCount = Integer.valueOf(maxEpochCountText.getText());
		neuralContext.setMaxEpochCount(maxEpochCount);
		updateView();
	}

	/**
	 * Set neural service stop flag
	 */
	private void stopNetworkTraining() {
		neuralService.stop();
		maxEpochCountText.setEnabled(true);
	}

	/**
	 * Do neural network training
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void trainNetwork() {
		// Update context
		updateContextFromView();
		// Update view
		maxEpochCountText.setEnabled(false);
		instrumentComboBox.setEnabled(false);

		// Start training thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Run training thread
				resetTrainingContext();
				neuralService.trainNetwork(neuralContext
						.getTrainingEndDateTime());

			}
		}, "trainNetwork").start();
	}

	/**
	 * Write changed in view data to context
	 * 
	 * @throws ParseException
	 */
	private void updateContextFromView() {
		try {
			// Max epoches
			Integer maxEpochCount = Integer
					.valueOf(maxEpochCountText.getText());
			neuralContext.setMaxEpochCount(maxEpochCount);
			// Training end date
			String endDateText = trainingEndDateTime.getText();
			Date endDate = dateFormat.parse(endDateText);
			neuralContext.setTrainingEndDateTime(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Update controls related to train iteration
	 * 
	 * @param e
	 *            {@link TrainIterationCompletedEvent} event data with last
	 *            iteration info: duration, error...
	 */
	private void updateView(TrainIterationCompletedEvent e) {
		learnProgressBar.setValue(e.getLastEpoch());
		// Labels
		lblEpoch.setText(String.format("Epoch %d of %d", e.getLastEpoch(),
				neuralContext.getMaxEpochCount()));
		lblLastEpochTime.setText(String.format("Last epoch: %f sec ", new Long(
				e.getLastEpochMilliseconds()).doubleValue() / 1000));
		lblTotalTime.setText(String.format("Total time: %f sec",
				new Long(e.getTrainMilliseconds()).doubleValue()));

		lblLastError.setText(String.format("Last error: %f %%",
				new Double(e.getLastError() / 0.01)));
	}

	/**
	 * Update view from context
	 */
	private void updateView() {
		if (neuralContext == null) {
			return;
		}
		// Last datetime of last input window in training
		String endDateString = dateFormat.format(neuralContext
				.getTrainingEndDateTime());
		trainingEndDateTime.setText(endDateString);
		// Train epoches
		maxEpochCountText.setText(String.valueOf(
				neuralContext.getMaxEpochCount()).toString());

		// Update progress bar
		learnProgressBar.setMaximum(neuralContext.getMaxEpochCount());

		// Enable learn button if instrument is set
		learnButton.setEnabled(neuralContext.getInstrument() != null);

	}
}
