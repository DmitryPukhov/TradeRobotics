package trading.view.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import trading.app.neural.NeuralContext;
import trading.app.neural.NeuralService;
import trading.app.neural.events.TestCompletedEvent;
import trading.app.neural.events.TestIterationCompletedEvent;

import com.google.common.eventbus.Subscribe;

/**
 * Test neural network view with charts
 * 
 * @author dima
 * 
 */
public class TestPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Neural context with prediction samples, prediction step data
	 */
	private final NeuralContext neuralContext;

	/**
	 * Neural service to start/stop testing
	 */
	private final NeuralService neuralService;

	/**
	 * Button to start test on historical data
	 */
	private final JToggleButton testButton;

	/**
	 * Historical test progress
	 */
	private final JProgressBar progressBar;

	/**
	 * Chart with absolute bid/ask price
	 */
	JFreeChart chartAbsolute;

	/**
	 * Ideal ask chart data
	 */
	XYSeries idealHighSeriesAbsolute;

	/**
	 * Ideal bid chart data
	 */
	XYSeries idealLowSeriesAbsolute;

	/**
	 * Predicted ask chart data
	 */
	XYSeries predictedHighSeriesAbsolute;

	/**
	 * Predicted bid chart data
	 */
	XYSeries predictedLowSeriesAbsolute;

	/**
	 * Change percent chart
	 */
	JFreeChart chartRelative;

	/**
	 * Ask change percent data ideal
	 */
	XYSeries idealHighSeriesRelative;

	/**
	 * Bid change percent data ideal
	 */
	XYSeries idealLowSeriesRelative;

	/**
	 * Ask change percent data predicted
	 */
	XYSeries predictedHighSeriesRelative;

	/**
	 * Bid change percent data predicted
	 */
	XYSeries predictedLowSeriesRelative;

	/**
	 * Neural network error chart
	 */
	private JFreeChart chartError;

	/**
	 * Error values
	 */
	private XYSeries errorXYSeries;

	/**
	 * Label to display current iteration info
	 */
	private final JLabel lblIteration;

	/**
	 * Constructor with context and neural service passed
	 */
	public TestPanel(NeuralContext context, NeuralService service) {
		neuralContext = context;
		neuralService = service;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		// Start test button
		testButton = new JToggleButton("Test");
		springLayout.putConstraint(SpringLayout.EAST, testButton, -10,
				SpringLayout.EAST, this);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testButton.isSelected()) {
					startTest();
				} else {
					stopTest();
				}
			}
		});
		add(testButton);

		// Progress bar
		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.SOUTH, testButton, 0,
				SpringLayout.SOUTH, progressBar);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -110,
				SpringLayout.EAST, this);
		add(progressBar);

		// Split panel to place charts in
		JSplitPane chartsPanel = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 0,
				SpringLayout.WEST, chartsPanel);
		springLayout.putConstraint(SpringLayout.NORTH, chartsPanel, 107,
				SpringLayout.NORTH, this);
		chartsPanel.setResizeWeight(0.3);
		chartsPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		springLayout.putConstraint(SpringLayout.WEST, chartsPanel, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, chartsPanel, 0,
				SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, chartsPanel, 0,
				SpringLayout.SOUTH, this);
		add(chartsPanel);

		// Init charts variables
		createChartAbsolute();
		createChartRelative();
		createChartError();

		JSplitPane chartsSubPanel = new JSplitPane();
		chartsSubPanel.setResizeWeight(0.5);
		chartsSubPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);

		// Add charts to panels
		ChartPanel absoluteChartPanel = new ChartPanel(chartAbsolute);
		ChartPanel relativeChartPanel = new ChartPanel(chartRelative);
		ChartPanel errorChartPanel = new ChartPanel(chartError);
		chartsPanel.setTopComponent(absoluteChartPanel);
		chartsPanel.setBottomComponent(chartsSubPanel);

		chartsSubPanel.setTopComponent(relativeChartPanel);
		chartsSubPanel.setBottomComponent(errorChartPanel);

		// Attach to events
		neuralService.getEventBus().register(this);

		lblIteration = new JLabel("Test iteration:");
		springLayout.putConstraint(SpringLayout.WEST, lblIteration, 10,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -6,
				SpringLayout.NORTH, lblIteration);
		springLayout.putConstraint(SpringLayout.SOUTH, lblIteration, -23,
				SpringLayout.NORTH, chartsPanel);
		add(lblIteration);

		// Continious training check boc
		JCheckBox chckbxContinuousTraining = new JCheckBox(
				"Continuous Training");
		chckbxContinuousTraining.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				// Update continuous training in context
				neuralContext.setContinuousTraining(source.isSelected());
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH,
				chckbxContinuousTraining, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, chckbxContinuousTraining,
				10, SpringLayout.WEST, this);
		add(chckbxContinuousTraining);

	}

	/**
	 * Clear all series in charts
	 */
	private void clearCharts() {
		// Price (absolute) chart clear
		idealHighSeriesAbsolute.clear();
		idealLowSeriesAbsolute.clear();
		;
		predictedHighSeriesAbsolute.clear();
		predictedLowSeriesAbsolute.clear();

		// Relative (percentage) chart clear
		idealHighSeriesRelative.clear();
		idealLowSeriesRelative.clear();
		predictedHighSeriesRelative.clear();
		predictedLowSeriesRelative.clear();

		// Error chart clear
		errorXYSeries.clear();

	}

	/**
	 * Init JFreeChart
	 */
	private void createChartAbsolute() {
		// Create dataset and series
		// TableXYDataset ds = new DefaultTableXYDataset();
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		idealHighSeriesAbsolute = new XYSeries("Ideal High");
		idealHighSeriesAbsolute
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		idealLowSeriesAbsolute = new XYSeries("Ideal Low");
		idealLowSeriesAbsolute
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		predictedHighSeriesAbsolute = new XYSeries("Predicted High");
		predictedHighSeriesAbsolute
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		predictedLowSeriesAbsolute = new XYSeries("Predicted Low");
		predictedLowSeriesAbsolute
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		seriesCollection.addSeries(idealHighSeriesAbsolute);
		seriesCollection.addSeries(idealLowSeriesAbsolute);
		seriesCollection.addSeries(predictedHighSeriesAbsolute);
		seriesCollection.addSeries(predictedLowSeriesAbsolute);
		// Create chartAbsolute
		// chartAbsolute = ChartFactory.createXYLineChart("Price values",
		// "Iteration", "Price", xySeriesCollection, PlotOrientation.VERTICAL,
		// true, true, true);
		chartAbsolute = ChartFactory.createTimeSeriesChart("Price values", // title
				"Iteration", // x-axis label
				"Price", // y-axis label
				seriesCollection, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);
		initChart(chartAbsolute);
	}

	/**
	 * Create error chart
	 */
	private void createChartError() {
		// Create dataset and series
		// TableXYDataset ds = new DefaultTableXYDataset();
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		errorXYSeries = new XYSeries("Error");
		xySeriesCollection.addSeries(errorXYSeries);
		errorXYSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);
		// Create chart
		chartError = ChartFactory.createXYLineChart("Error value", "Iteration",
				"Error, %", xySeriesCollection, PlotOrientation.VERTICAL, true,
				true, true);
		XYPlot plot = (XYPlot) chartError.getPlot();
		plot.getRangeAxis().setAutoRange(true);
		// plot.getDomainAxis().setRange(1,
		// neuralContext.getTrainingContext().getMaxEpochCount());
		// X auto range to proper display last error values
		plot.getDomainAxis().setAutoRange(true);
	}

	/**
	 * Init JFreeChart
	 */
	private void createChartRelative() {
		// Create dataset and series
		// TableXYDataset ds = new DefaultTableXYDataset();
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		idealHighSeriesRelative = new XYSeries("Ideal High");
		idealHighSeriesRelative
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		idealLowSeriesRelative = new XYSeries("Ideal Low");
		idealLowSeriesRelative
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		predictedHighSeriesRelative = new XYSeries("Predicted High");
		predictedHighSeriesRelative
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		predictedLowSeriesRelative = new XYSeries("Predicted Low");
		predictedLowSeriesRelative
				.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		seriesCollection.addSeries(idealHighSeriesRelative);
		seriesCollection.addSeries(idealLowSeriesRelative);
		seriesCollection.addSeries(predictedHighSeriesRelative);
		seriesCollection.addSeries(predictedLowSeriesRelative);
		// Create chartAbsolute
		// chartRelative = ChartFactory.createXYLineChart("Price change %",
		// "Iteration", "Change %", xySeriesCollection,
		// PlotOrientation.VERTICAL, true, true, true);
		chartRelative = ChartFactory.createTimeSeriesChart("Price change %", // title
				"Iteration", // x-axis label
				"Change %", // y-axis label
				seriesCollection, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);
		initChart(chartRelative);

		// plot.getDomainAxis().setRange(1,
		// (double)NeuralContext.Test.getMaxIterationCount());
	}

	/**
	 * Initialization, common for both charts
	 * 
	 * @param chart
	 */
	private void initChart(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getRangeAxis().setAutoRange(true);
		// Auto range
		NumberAxis valueAxis = (NumberAxis) plot.getRangeAxis();
		valueAxis.setAutoRangeIncludesZero(false);
		// Set line colors
		plot.getRenderer().setSeriesPaint(0, Color.GREEN);
		plot.getRenderer().setSeriesPaint(1, Color.GREEN);
		plot.getRenderer().setSeriesPaint(2, Color.BLUE);
		plot.getRenderer().setSeriesPaint(3, Color.BLUE);
	}

	/**
	 * Train completed, update view
	 * 
	 * @param e
	 */
	@Subscribe
	public void onTestCompleted(TestCompletedEvent e) {
		testButton.setSelected(false);
	}

	/**
	 * Add predicted and ideal data to charts
	 * 
	 * @param e
	 */
	@Subscribe
	public synchronized void onTestIterationCompleted(
			TestIterationCompletedEvent e) {
		// Get x value (time axis)
		// Calendar cal = GregorianCalendar.getInstance();
		// cal.setTime(e.getLevel1().getDate());
		// RegularTimePeriod x =
		// RegularTimePeriod.createInstance(Millisecond.class,
		// e.getLevel1().getDate(), cal.getTimeZone());
		// Add xy values to the charts

		double x = new Integer(e.getIteration()).doubleValue();
		// Relative chart
		predictedLowSeriesRelative.add(x, e.getPredictedLow() / 0.01);
		predictedHighSeriesRelative.add(x, e.getPredictedHigh() / 0.01);
		idealLowSeriesRelative.add(x, e.getIdealLow() / 0.01);
		idealHighSeriesRelative.add(x, e.getIdealHigh() / 0.01);
		// Absolute prices chart
		predictedLowSeriesAbsolute.add(x, e.getLevel1().getBid().doubleValue()
				* (1 + e.getPredictedLow()));
		predictedHighSeriesAbsolute.add(x, e.getLevel1().getAsk().doubleValue()
				* (1 + e.getPredictedHigh()));
		idealLowSeriesAbsolute.add(x, e.getLevel1().getBid().doubleValue()
				* (1 + e.getIdealLow()));
		idealHighSeriesAbsolute.add(x, e.getLevel1().getAsk().doubleValue()
				* (1 + e.getIdealHigh()));
		// Error chart - set error in percents
		errorXYSeries.add(x, 100 * e.getError());

		lblIteration.setText(String.format("Iteration: %d of %d",
				e.getIteration(), neuralContext.getPredictionSamples()));
		progressBar.setMaximum(neuralContext.getPredictionSamples());
		progressBar.setValue(e.getIteration());

	}

	/**
	 * Test neural network
	 */
	private void startTest() {
		// Delete previous chart
		clearCharts();

		// Start testing thread and continue this form
		new Thread(new Runnable() {
			@Override
			public void run() {
				neuralService.testNetwork();
			}
		}).start();
	}

	/**
	 * Set neural service stop flag to interrupt testing
	 */
	private void stopTest() {
		neuralService.stop();
	}
}
