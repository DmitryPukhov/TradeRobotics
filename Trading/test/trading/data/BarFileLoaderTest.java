package trading.data;

import trading.data.model.Bar;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author dima
 */
public class BarFileLoaderTest {
    
    /**
     * Test of load method, of class BarFileLoader.
     */
    @Test
    public void testGetBarColumnPositions() throws Exception {
        System.out.println("testgetBarColumnPositions");
        // Invoke private method using reflection
        Method method = BarFileLoader.class.getDeclaredMethod("getBarColumnPositions", String.class); 
        method.setAccessible(true);

        Object columnPositions = method.invoke(null, "<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>");
        
        assertFieldEquals(columnPositions, 0, "date");
        assertFieldEquals(columnPositions, 1, "time");
        assertFieldEquals(columnPositions, 2, "open");
        assertFieldEquals(columnPositions, 3, "high");
        assertFieldEquals(columnPositions, 4, "low");
        assertFieldEquals(columnPositions, 5, "close");
        assertFieldEquals(columnPositions, 6, "volume");

    }
    
    /**
     * Read bar from string test
     * @throws Exception 
     */
    @Test
    public void testReadSingleBarData() throws Exception{
        System.out.println("testgetBarColumnPositions");
        // Get bar column positions to pass as argument to being tested function
        Method getBarColumnPositions = BarFileLoader.class.getDeclaredMethod("getBarColumnPositions", String.class); 
        getBarColumnPositions.setAccessible(true);
        Object columnPositions = getBarColumnPositions.invoke(null, "<TICKER>,<PER>,<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>");    
        // Invoke readSingleBarData function with proper args
        Method readSingleBarData = BarFileLoader.class.getDeclaredMethod("readSingleBarData", String.class, columnPositions.getClass());
        readSingleBarData.setAccessible(true);
        Bar bar = (Bar) readSingleBarData.invoke(null, "SPFB.RTS,1,20130711,150100,126200.0000000,129500.0000000,126100.0000000,129310.0000000,42379", columnPositions);
        // Check the result
        assertNotNull(bar);
        // Date
        assertEquals("Year",2013,bar.getTime().get(Calendar.YEAR));
        assertEquals("Month",7,bar.getTime().get(Calendar.MONTH));
        assertEquals("Day",11,bar.getTime().get(Calendar.DAY_OF_MONTH));
        //Time
         assertEquals("Hour",15,bar.getTime().get(Calendar.HOUR_OF_DAY));
         assertEquals("Minute", 01,bar.getTime().get(Calendar.MINUTE));
         assertEquals("Second", 0,bar.getTime().get(Calendar.SECOND));

         //OHLC
         assertEquals("Open", 126200,bar.getOpen(),0);
         assertEquals("High", 129500,bar.getHigh(),0);
         assertEquals("Low", 126100,bar.getLow(),0);
         assertEquals("Close", 129310,bar.getClose(),0);
         assertEquals("vol", 42379,bar.getVolume(),0);
    }    
 
    /**
     * Check whether field with name equals the value
     * @param columnPositions
     * @param expected
     * @param fieldName 
     */
    protected void assertFieldEquals(Object columnPositions, int expected, String fieldName) throws Exception{
        Field field = columnPositions.getClass().getDeclaredField(fieldName);
        Object pos = field.get(columnPositions);
        assertEquals(fieldName, expected, pos);       
    }
}
