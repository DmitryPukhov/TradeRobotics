package trading.view.swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import trading.data.model.Level1;

/**
 * Level1 ticks chart
 * 
 * @author dima
 * 
 */
public class Level1Chart extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Chart title
	 */
	private String title;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return priceChart.getTitle().getText();
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		priceChart.setTitle(title);
	}

	/**
	 * Price quote series for price chart
	 */
	TimeSeries priceSeries;

	/**
	 * Bid data series for price chart
	 */
	TimeSeries bidSeries;

	/**
	 * Ask data series for price chart
	 */
	TimeSeries askSeries;

	/**
	 * For volume chart - volume series for price chart
	 */
	TimeSeries volumeSeries;

	/**
	 * Price chart
	 */
	final JFreeChart priceChart;

	/**
	 * Volume chart
	 */
	final JFreeChart volumeChart;

	/**
	 * Volume series index in it's series collection
	 */
	final static int VOLUME_SERIES_INDEX = 0;

	/**
	 * Price series index in level1 data series collection
	 */
	final static int PRICE_SERIES_INDEX = 0;

	/**
	 * Bid series index in level1 data series collection
	 */
	final static int BID_SERIES_INDEX = 1;

	/**
	 * Ask series index in level1 data series collection
	 */
	final static int ASK_SERIES_INDEX = 2;

	/**
	 * Price color in price chart
	 */
	final static Color PRICE_SERIES_COLOR = Color.blue;

	/**
	 * Bid color in price chart
	 */
	final static Color BID_SERIES_COLOR = Color.cyan;

	/**
	 * Ask color in price chart
	 */
	final static Color ASK_SERIES_COLOR = Color.cyan;

	/**
	 * Volume color in volume chart
	 */
	final static Color VOLUME_SERIES_COLOR = Color.red;

	/**
	 * Ctor, jfreechart init
	 * 
	 * @param title
	 */
	public Level1Chart(String title) {
		this.title = title;
		setBackground(SystemColor.control);

		// Split pane with price and volume charts
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		// Price chart
		final XYDataset priceDataSet = createPriceDataSet();
		priceChart = createPriceChart(priceDataSet);
		setLayout(new GridLayout(0, 1, 0, 0));

		// Panel for price chart
		final ChartPanel priceChartPanel = new ChartPanel(priceChart);
		splitPane.setLeftComponent(priceChartPanel);
		FlowLayout flowLayout_1 = (FlowLayout) priceChartPanel.getLayout();
		flowLayout_1.setAlignOnBaseline(true);
		priceChartPanel.setPreferredSize(new java.awt.Dimension(500, 370));

		// Volume chart
		final XYDataset volumeDataSet = createVolumeDataSet();
		volumeChart = createVolumeChart(volumeDataSet);
		setLayout(new GridLayout(0, 1, 0, 0));

		// Panel for volume chart
		final ChartPanel volumeChartPanel = new ChartPanel(volumeChart);
		splitPane.setRightComponent(volumeChartPanel);
		FlowLayout flowLayout_2 = (FlowLayout) volumeChartPanel.getLayout();
		flowLayout_2.setAlignOnBaseline(true);
		volumeChartPanel.setPreferredSize(new java.awt.Dimension(500, 100));
	}

	/**
	 * Add level1 item to the chart
	 * 
	 * @param level1
	 */
	public void addLevel1(Level1 level1) {
		FixedMillisecond fm = new FixedMillisecond(level1.getLastTime());
		priceSeries.addOrUpdate(fm, level1.getLastPrice());
		bidSeries.addOrUpdate(fm, level1.getBid());
		askSeries.addOrUpdate(fm, level1.getAsk());
		volumeSeries.addOrUpdate(fm, level1.getLastSize());
	}

	/**
	 * Add set of level1 data
	 * 
	 * @param level1Data
	 */
	public void addLevel1(List<Level1> level1Data) {
		for (Level1 level1 : level1Data) {
			addLevel1(level1);
		}
	}

	/**
	 * Clear chart data
	 */
	public void clear() {
		priceSeries.clear();
		bidSeries.clear();
		askSeries.clear();
		volumeSeries.clear();
	}

	/**
	 * Data set for price chart
	 * 
	 * @return
	 */
	private XYDataset createPriceDataSet() {
		// Add empty time series to fill later
		// Create price series
		priceSeries = new TimeSeries("Last price");
		priceSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);
		bidSeries = new TimeSeries("Bid");
		bidSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);
		askSeries = new TimeSeries("Ask");
		askSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);

		// Create and return DataSet with all time series
		TimeSeriesCollection dataSet = new TimeSeriesCollection();
		dataSet.addSeries(priceSeries);
		dataSet.addSeries(bidSeries);
		dataSet.addSeries(askSeries);
		return dataSet;
	}

	/**
	 * Some test data
	 * 
	 * @param priceSeries
	 */
	private void fillTestData(TimeSeries priceSeries) {
		// Generates random data every second
		Calendar cal = Calendar.getInstance();
		Random generator = new Random();
		for (int i = 0; i < 10; i++) {
			FixedMillisecond fm = new FixedMillisecond(cal.getTime());
			cal.add(Calendar.SECOND, 1);
			double price = generator.nextDouble();
			volumeSeries.addOrUpdate(fm, price);
		}
	}

	/**
	 * Price chart creation
	 * 
	 * @param dataset
	 * @return
	 */
	private JFreeChart createPriceChart(final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(this.title,
				null, "price", dataset, false, false, false);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		// Price series
		renderer.setSeriesPaint(PRICE_SERIES_INDEX, PRICE_SERIES_COLOR);
		renderer.setSeriesLinesVisible(PRICE_SERIES_INDEX, true);
		renderer.setSeriesShapesVisible(PRICE_SERIES_INDEX, false);
		// Bid series
		renderer.setSeriesPaint(BID_SERIES_INDEX, BID_SERIES_COLOR);
		renderer.setSeriesLinesVisible(BID_SERIES_INDEX, true);
		renderer.setSeriesShapesVisible(BID_SERIES_INDEX, false);
		// Price series
		renderer.setSeriesPaint(ASK_SERIES_INDEX, ASK_SERIES_COLOR);
		renderer.setSeriesLinesVisible(ASK_SERIES_INDEX, true);
		renderer.setSeriesShapesVisible(ASK_SERIES_INDEX, false);

		plot.setRenderer(renderer);

		return chart;

	}

	/**
	 * Data set for volume chart
	 * 
	 * @return
	 */
	private XYDataset createVolumeDataSet() {
		// Add empty time series to fill later
		// Create price series
		volumeSeries = new TimeSeries("Volume");
		volumeSeries.setMaximumItemCount(Constants.MAX_CHART_ITEM_C0UNT);
		// Create and return DataSet with all time series
		TimeSeriesCollection dataSet = new TimeSeriesCollection();
		dataSet.addSeries(volumeSeries);
		return dataSet;
	}

	/**
	 * Create chart for volume data
	 * 
	 * @param dataset
	 * @return
	 */
	private JFreeChart createVolumeChart(final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null,
				"volume", dataset, false, false, false);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		final ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
		renderer.setSeriesPaint(VOLUME_SERIES_INDEX, VOLUME_SERIES_COLOR);
		renderer.setShadowVisible(false);
		plot.setRenderer(renderer);
		return chart;
	}
}
