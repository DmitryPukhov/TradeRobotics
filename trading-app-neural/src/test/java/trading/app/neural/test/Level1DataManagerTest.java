/**
 * 
 */

package trading.app.neural.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import trading.app.history.HistoryProvider;
import trading.app.neural.NeuralContext;
import trading.app.neural.history.Level1DataManager;
import trading.app.neural.history.NeuralDataManager;
import trading.data.model.Instrument;
import trading.data.model.Level1;

/**
 * @author dima
 * 
 */
public class Level1DataManagerTest extends AbstractTest {
	/**
	 * Mock items for test
	 */
	private static List<Level1> mockLevel1s = null;

	/**
	 * Mock instrument
	 */
	private static Instrument mockInstrument = null;

	/**
	 * Context mock
	 */
	private static NeuralContext mockNeuralContext = null;

	/**
	 * History provider mock. Methods will be overwritten using Mockito
	 * framework
	 */
	private static HistoryProvider mockHistoryProvider = null;

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#addLevel1Data}.
	 */
	@Test
	public void addLevel1DataTest() {
		// Prepare method wide params
		NeuralDataManager loader = new Level1DataManager(null, null);
		Integer singleLevel1Size = (Integer) ReflectionTestUtils.getField(
				loader, "LEVEL1_DATA_SIZE");
		MLData mlData = new BasicMLData(singleLevel1Size + 2);
		for (double value : mlData.getData()) {
			assertEquals(0, value, Constants.DOUBLE_COMPARISON_PRECISION);
		}

		// Prepare 2 level1
		final Calendar cal = new GregorianCalendar();
		final Calendar prevCal = new GregorianCalendar();
		prevCal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		prevCal.add(Calendar.MILLISECOND, -1);

		Level1 level1 = new Level1(cal.getTime(), new BigDecimal(2), 2,
				new BigDecimal(2), 2, new BigDecimal(2), 2);
		Level1 prevLevel1 = new Level1(cal.getTime(), new BigDecimal(1), 1,
				new BigDecimal(1), 1, new BigDecimal(1), 1);

		// Call and check the result
		int pos = 0;
		Integer resultPos = ReflectionTestUtils.invokeMethod(loader,
				"addLevel1Data", mlData, pos, level1, prevLevel1);
		assertEquals(singleLevel1Size, resultPos);
		assertMLDataContainsLevel1(loader, mlData, pos, level1, prevLevel1);
		assertEquals(0.0, mlData.getData(singleLevel1Size),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(0.0, mlData.getData(singleLevel1Size + 1),
				Constants.DOUBLE_COMPARISON_PRECISION);

		mlData = new BasicMLData(singleLevel1Size + 2);
		// Call and check the result
		pos = 1;
		resultPos = ReflectionTestUtils.invokeMethod(loader, "addLevel1Data",
				mlData, pos, level1, prevLevel1);
		Integer idealPos = pos + singleLevel1Size;
		assertEquals(idealPos, resultPos);
		assertEquals(0, mlData.getData(0),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertMLDataContainsLevel1(loader, mlData, pos, level1, prevLevel1);
		assertEquals(0, mlData.getData(singleLevel1Size + 1),
				Constants.DOUBLE_COMPARISON_PRECISION);

	}

	/**
	 * Check whether mldata contains level1
	 * 
	 * @param loader
	 * @param mlData
	 * @param index
	 *            level1 data index in mldata
	 * @param level1
	 * @param prevLevel1
	 */
	private void assertMLDataContainsLevel1(NeuralDataManager loader,
			MLData mlData, int index, Level1 level1, Level1 prevLevel1) {

		// Get normalized data to compare
		double nDate = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedDate", level1.getDate());
		double nPrice = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedPrice", level1.getLastPrice(),
				prevLevel1.getLastPrice());
		double nSize = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedSize", level1.getLastSize(),
				prevLevel1.getLastSize());
		double nBid = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedPrice", level1.getBid(), prevLevel1.getBid());
		double nBidSize = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedSize", level1.getBidSize(),
				prevLevel1.getBidSize());
		double nAsk = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedPrice", level1.getAsk(), prevLevel1.getAsk());
		double nAskSize = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedSize", level1.getAskSize(),
				prevLevel1.getAskSize());

		assertEquals(nDate, mlData.getData(index + 0),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nPrice, mlData.getData(index + 1),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nSize, mlData.getData(index + 2),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nBid, mlData.getData(index + 3),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nBidSize, mlData.getData(index + 4),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nAsk, mlData.getData(index + 5),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(nAskSize, mlData.getData(index + 6),
				Constants.DOUBLE_COMPARISON_PRECISION);
	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#entitiesToMLDataTest()}
	 * .
	 */
	@Test
	public void entitiesToMLDataTest() {
		NeuralDataManager loader = new Level1DataManager(null, null);
		List<Level1> entities = mockLevel1s.subList(0, 3);
		MLData mlData = ReflectionTestUtils.invokeMethod(loader,
				"entitiesToMLData", entities);

		assertMLDataContainsLevel1(loader, mlData, 0, entities.get(1),
				entities.get(0));
		Integer singleLevel1Size = (Integer) ReflectionTestUtils.getField(
				loader, "LEVEL1_DATA_SIZE");
		assertMLDataContainsLevel1(loader, mlData, singleLevel1Size,
				entities.get(2), entities.get(1));
	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#getInputData(List, int)

	 */
	@Test
	public void getInputDataTest() {

		NeuralContext context = new NeuralContext();
		context.setLevel1WindowSize(1);
		context.setPredictionSize(1);
		NeuralDataManager manager = new Level1DataManager(context, null);
		// Invoke
		MLData inputData = manager.getInputData(mockLevel1s, 1);
		// Assert input data in pair
		assertMLDataContainsLevel1(manager, inputData, 0, mockLevel1s.get(1),
				mockLevel1s.get(0));
		assertEquals(Level1DataManager.LEVEL1_DATA_SIZE, inputData.size());
	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#getMLDataPair(List, int)}
	 * .
	 */
	@Test
	public void getMLDataPairTest() {

		NeuralContext context = new NeuralContext();
		context.setLevel1WindowSize(1);
		context.setPredictionSize(1);
		NeuralDataManager manager = new Level1DataManager(context, null);
		// Invoke
		MLDataPair pair = manager.getMLDataPair(mockLevel1s, 1);
		// Assert input data in pair
		assertMLDataContainsLevel1(manager, pair.getInput(), 0,
				mockLevel1s.get(1), mockLevel1s.get(0));
		// Assert output data
		MLData output = pair.getIdeal();

		// Invoke
		MLData rightOutput = ReflectionTestUtils.invokeMethod(manager,
				"getOutputData", mockLevel1s, 1);

		assertEquals(rightOutput.getData(0), output.getData(0),
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(rightOutput.getData(1), output.getData(1),
				Constants.DOUBLE_COMPARISON_PRECISION);
	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#getNormalizedPrice}.
	 */
	@Test
	public void getNormalizedPriceTest() {

		NeuralDataManager loader = new Level1DataManager(null, null);
		double result = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedPrice", new BigDecimal(1), new BigDecimal(1));
		assertEquals(0, result, Constants.DOUBLE_COMPARISON_PRECISION);

		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedPrice",
				new BigDecimal(2), new BigDecimal(1));
		assertEquals(1, result, Constants.DOUBLE_COMPARISON_PRECISION);

		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedPrice",
				new BigDecimal(1), new BigDecimal(2));
		assertEquals(-0.5, result, Constants.DOUBLE_COMPARISON_PRECISION);

		double prevValue = 150.34;
		double curValue = 134.06;
		double expected = (curValue - prevValue) / prevValue;
		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedPrice",
				new BigDecimal(curValue), new BigDecimal(prevValue));
		assertEquals(result, expected, Constants.DOUBLE_COMPARISON_PRECISION);

	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#getNormalizedSize}.
	 */
	@Test
	public void getNormalizedSizeTest() {
		NeuralDataManager loader = new Level1DataManager(null, null);
		double result = ReflectionTestUtils.invokeMethod(loader,
				"getNormalizedSize", 1, 1);
		assertEquals(0.0, result, Constants.DOUBLE_COMPARISON_PRECISION);

		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedSize",
				2, 1);
		assertEquals(1, result, Constants.DOUBLE_COMPARISON_PRECISION);

		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedSize",
				1, 2);
		assertEquals(-0.5, result, Constants.DOUBLE_COMPARISON_PRECISION);

		int prevValue = 26;
		int curValue = 86;
		double expected = new Double(curValue - prevValue)
				/ new Double(prevValue);
		result = ReflectionTestUtils.invokeMethod(loader, "getNormalizedSize",
				curValue, prevValue);
		assertEquals(expected, result, Constants.DOUBLE_COMPARISON_PRECISION);

	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#getOutputData()}.
	 */
	@Test
	public void getOutputDataTest() {
		NeuralContext context = new NeuralContext();
		context.setLevel1WindowSize(1);
		context.setPredictionSize(2);

		NeuralDataManager loader = new Level1DataManager(context, null);
		// Prepare params
		Level1 item1 = mockLevel1s.get(0);
		Level1 item2 = mockLevel1s.get(1);
		Level1 item3 = mockLevel1s.get(2);
		double minBid = (Math.min(item3.getBid().doubleValue(), item2.getBid()
				.doubleValue()) - item1.getBid().doubleValue())
				/ item1.getBid().doubleValue();
		double maxAsk = (Math.max(item3.getAsk().doubleValue(), item2.getAsk()
				.doubleValue()) - item1.getAsk().doubleValue())
				/ item1.getAsk().doubleValue();
		// Invoke
		MLData data = ReflectionTestUtils.invokeMethod(loader, "getOutputData",
				Arrays.asList(new Level1[] { item1, item2, item3 }), 0);
		// Assert
		assertEquals(data.getData(0), minBid,
				Constants.DOUBLE_COMPARISON_PRECISION);
		assertEquals(data.getData(1), maxAsk,
				Constants.DOUBLE_COMPARISON_PRECISION);
	}

	/**
	 * Init mock instrument, context
	 */
	@SuppressWarnings("serial")
	@BeforeClass
	public static void initMockContext() {
		// Instrument
		mockInstrument = new Instrument() {
			{
				setId(0);
			}
		};
		// History provider
		mockHistoryProvider = org.mockito.Mockito.mock(HistoryProvider.class);
		// NeuralContext
		mockNeuralContext = new NeuralContext();
		// Mock insrument in instrument context
		mockNeuralContext.setInstrument(mockInstrument);
	}

	/**
	 * Test initialization Add test level1 items to test list
	 */
	@BeforeClass
	public static void initMockLevel1s() {
		// Set time values
		Calendar cal = Calendar.getInstance();
		Date time1 = cal.getTime();

		cal.add(Calendar.MILLISECOND, 1);
		Date time2 = cal.getTime();

		cal.add(Calendar.MILLISECOND, 1);
		Date time3 = cal.getTime();

		cal.add(Calendar.MILLISECOND, 1);
		Date time4 = cal.getTime();

		cal.add(Calendar.MILLISECOND, 1);
		Date time5 = cal.getTime();

		mockLevel1s = Arrays.asList(new Level1[] {
				new Level1(time1, new BigDecimal(1), 1, new BigDecimal(1), 1,
						new BigDecimal(1), 1),
				new Level1(time2, new BigDecimal(2), 2, new BigDecimal(2), 2,
						new BigDecimal(2), 2),
				new Level1(time3, new BigDecimal(3), 3, new BigDecimal(3), 3,
						new BigDecimal(3), 3),
				new Level1(time4, new BigDecimal(5), 5, new BigDecimal(5), 5,
						new BigDecimal(5), 5),
				new Level1(time5, new BigDecimal(5), 5, new BigDecimal(5), 5,
						new BigDecimal(5), 5), });

	}

	/**
	 * Test method for {@link Level1DataManager#loadTestData(Date)

	 */
	@Test
	public void loadTestDataTest() {
		// Init mock neural context
		NeuralContext context = mockNeuralContext;
		context.setLevel1WindowSize(1);
		context.setPredictionSize(1);
		// context.getTrainingContext().setTrainSamples(2);
		context.setPredictionSamples(1);
		// context.getTrainingContext().setTrainStep(1);
		// End date to pass
		Date endTime = mockLevel1s.get(3).getDate();
		// History provider mock

		when(mockHistoryProvider.findLevel1After(0, endTime, 1)).thenAnswer(
				new Answer<List<Level1>>() {
					@Override
					public List<Level1> answer(InvocationOnMock invocation)
							throws Throwable {
						return new ArrayList<Level1>(mockLevel1s.subList(4, 5));
					}
				});
		when(mockHistoryProvider.findLevel1NotAfter(0, endTime, 2)).thenAnswer(
				new Answer<List<Level1>>() {
					@Override
					public List<Level1> answer(InvocationOnMock invocation)
							throws Throwable {
						return new ArrayList<Level1>(mockLevel1s.subList(2, 4));
					}
				});
		// Run method to test
		NeuralDataManager loader = new Level1DataManager(context,
				mockHistoryProvider);
		List<Level1> level1s = loader.loadTestData(endTime);
		// prevbar + input window + prediction = 3
		assertEquals(3, level1s.size());

		assertEquals(mockLevel1s.get(2), level1s.get(0));
		assertEquals(mockLevel1s.get(3), level1s.get(1));
		assertEquals(mockLevel1s.get(4), level1s.get(2));
	}

	/**
	 * Test method for
	 * {@link trading.app.neural.history.Level1DataManager#loadTrainMLDataSet()}
	 * .
	 */
	@Test
	public void loadTrainMLDataSetTest() {
		Date endDate = mockLevel1s.get(3).getDate();
		// History provider mock

		when(mockHistoryProvider.findLevel1NotAfter(0, endDate, 4)).thenAnswer(
				new Answer<List<Level1>>() {
					@Override
					public List<Level1> answer(InvocationOnMock invocation)
							throws Throwable {
						return new ArrayList<Level1>(mockLevel1s.subList(0, 4));
					}
				});

		NeuralContext context = new NeuralContext();
		// Mock insrument in instrument context

		context.setInstrument(mockInstrument);
		// Set params in context
		context.setLevel1WindowSize(1);
		context.setPredictionSize(1);
		context.setTrainSamples(2);
		context.setPredictionSamples(0);
		context.setTrainStep(1);

		NeuralDataManager loader = new Level1DataManager(context,
				mockHistoryProvider);
		MLDataSet dataSet = loader.loadTrainMLDataSet(endDate);
		// window 2 samples of 1 item, 1 previous bar, 1 prediction
		// 4 items overall
		assertEquals(2, dataSet.size());

		assertMLDataContainsLevel1(loader, dataSet.get(0).getInput(), 0,
				mockLevel1s.get(1), mockLevel1s.get(0));
		assertMLDataContainsLevel1(loader, dataSet.get(1).getInput(), 0,
				mockLevel1s.get(2), mockLevel1s.get(1));
	}

}
