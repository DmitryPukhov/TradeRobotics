package trading.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import trading.common.NeuralContext;
import trading.data.model.Bar;

/**
 * Load bars array from input files
 *
 * @author pdg
 */
public class BarFileLoader {

    private final static String CSV_DELIMITER = ",";

//    /**
//     * Load small, medium large bars with train data
//     * @param smallBars
//     * @param mediumBars
//     * @param largeBars
//     * @throws FileNotFoundException
//     * @throws IOException 
//     */
//    public static void loadTrainBars(List<Bar> smallBars, List<Bar> mediumBars, List<Bar> largeBars) throws FileNotFoundException, IOException{
//        smallBars.addAll(load(NeuralContext.Files.getSmallBarsTrainFilePath()));
//        mediumBars.addAll(load(NeuralContext.Files.getMediumBarsTrainFilePath())); 
//        largeBars.addAll(load(NeuralContext.Files.getLargeBarsTestFilePath()));
//    }
//     /**
//     * Load small, medium large bars with test data
//     * @param smallBars
//     * @param mediumBars
//     * @param largeBars
//     * @throws FileNotFoundException
//     * @throws IOException 
//     */
//    public static void loadTestBars(List<Bar> smallBars, List<Bar> mediumBars, List<Bar> largeBars) throws FileNotFoundException, IOException{
//        smallBars.addAll(load(NeuralContext.Files.getSmallBarsTestFilePath()));
//        mediumBars.addAll(load(NeuralContext.Files.getMediumBarsTestFilePath())); 
//        largeBars.addAll(load(NeuralContext.Files.getLargeBarsTestFilePath()));
//    }
    /**
     * Read bars from *.csv file to bar data array
     *
     * @param barData Array to fill
     * @param csvPath path to *.csv file
     * @throws FileNotFoundException
     */
    public static List<Bar> load(String csvPath) throws FileNotFoundException, IOException {
        List<Bar> bars = new ArrayList<>();
        
         String currentDir = new java.io.File( "." ).getCanonicalPath();
         csvPath = currentDir + "/" + csvPath;
        // Read file
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
    
            // Init from CSV header
            String barString = br.readLine();
            BarColumnPosition positions = getBarColumnPositions(barString);
            barString = br.readLine();
            // Process string in csv file and add it to array            

            while (barString != null) {

                Bar bar = readSingleBarData(barString, positions);
                // Add to resulting array
                if (bar != null) {
                    bars.add(bar);
                }
                // Next line
                barString = br.readLine();
            }
        }
        return bars;
    }
    
    

    //<editor-fold desc="Utils">
    /**
     * Indexes of date, ohlc, volume elements in CSV string. To be filled from
     * CSV file header
     */
    private static class BarColumnPosition {
        // Date time

        public int date = -1;
        public static final String dateHeader = "<DATE>";
        public int time = -1;
        public static final String timeHeader = "<TIME>";
        //public static final int TIME = 0;    
        public int open = -1;
        public static final String openHeader = "<OPEN>";
        //public static final int OPEN = 1;
        public int high = -1;
        public static final String highHeader = "<HIGH>";
        //public static final int HIGH = 2;
        public int low = -1;
        public static final String lowHeader = "<LOW>";
        //public static final int LOW = 3;
        public int close = -1;
        public static final String closeHeader = "<CLOSE>";
        //public static final int CLOSE = 4;
        public int volume = -1;
        public static final String volumeHeader = "<VOL>";
        //public static final int VOLUME = 5;
    }

    /**
     * Set BarElementPosition static class variables to ohlc, vol, time
     * positions
     *
     * @param headerString
     */
    private static BarColumnPosition getBarColumnPositions(String headerString) {
        BarColumnPosition positions = new BarColumnPosition();
        if (headerString == null) {
            return positions;
        }
        String[] headers = headerString.split(CSV_DELIMITER);
        for (int i = 0; i < headers.length; i++) {
            switch (headers[i]) {
                case BarColumnPosition.dateHeader:
                    positions.date = i;
                    break;
                case BarColumnPosition.timeHeader:
                    positions.time = i;
                    break;
                case BarColumnPosition.openHeader:
                    positions.open = i;
                    break;
                case BarColumnPosition.highHeader:
                    positions.high = i;
                    break;
                case BarColumnPosition.lowHeader:
                    positions.low = i;
                    break;
                case BarColumnPosition.closeHeader:
                    positions.close = i;
                    break;
                case BarColumnPosition.volumeHeader:
                    positions.volume = i;
                    break;
            }
        }
        return positions;
    }

    /**
     * Read data from csv string
     * <TICKER>	<PER>	<DATE>	<TIME>	<OPEN>	<HIGH>	<LOW>	<CLOSE>
     *
     * @param elements
     * @return
     */
    private static Bar readSingleBarData(String barString, BarColumnPosition positions) {
        Bar bar = null;
        try {
            String[] elements = barString.split(CSV_DELIMITER);
            // Get ohlc values
            Calendar cal = getTimeValue(elements, positions);
            double open = (positions.open != -1) ? Double.valueOf(elements[positions.open]) : 0;
            double high = (positions.high != -1) ? Double.valueOf(elements[positions.high]) : 0;
            double low = (positions.low != -1) ? Double.valueOf(elements[positions.low]) : 0;
            double close = (positions.close != -1) ? Double.valueOf(elements[positions.close]) : 0;
            double vol = (positions.volume != -1) ? Double.valueOf(elements[positions.volume]) : 0;
            // Init bar array according to BarColumnPositon positions (time, ohlc, vol)
            /*singleBarData = new double[]{cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.DAY_OF_MONTH), 
             cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
             open, high, low, close, vol}*/
            bar = new Bar(cal, open, high, low, close, vol);

        } catch (Exception e) {
            // Log exception and continue
            Logger.getLogger(BarFileLoader.class.toString()).log(Level.SEVERE, e.toString());
        }
        return bar;
    }

    /**
     * Get time from date and time columns
     *
     * @param elements
     * @return
     */
    private static Calendar getTimeValue(String[] elements, BarColumnPosition positions) {
        // Date column
        String dateString = elements[positions.date];

        int dateInt = Integer.parseInt(dateString);
        int year = dateInt / 10000;
        int month = (dateInt % 10000) / 100;
        int day = dateInt % 100;
        // Time column
        String timeString = elements[positions.time];
        int timeInt = Integer.parseInt(timeString);
        int hour = timeInt / 10000;
        int min = (timeInt % 10000) / 100;
        int sec = timeInt % 100;
        // Convert to double
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(year, month, day, hour, min, sec);
        return cal;
    }
    //</editor-fold>
}
