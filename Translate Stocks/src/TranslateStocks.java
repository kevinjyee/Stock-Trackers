          import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class TranslateStocks {
    public static String SEPARATOR = "@";
    public static final String COLON = ":";
    
 
   
    /******* Function: generatePrice *******
     * -----------------------------------------------
     * Maps the Ticker to it's respective price per stock
     * @returns the Map holding each Price per Stock
     */
    static TreeMap<String, Double> generatePrice(String[] portfolios)
    {
        TreeMap<String,Double> map = new TreeMap<>();
        
         for(String holdings:portfolios)
         {
             String[] individHoldings = holdings.split(",");
            
  
             String Key = individHoldings[0];
             Double Price = Double.parseDouble(individHoldings[3]); //position 3 holds price of each stock

             map.put(Key, Price);
         
         }
        return map;
    }
    
    
    /******* Function: generateQuantity *******
     * -----------------------------------------------
     * Maps the Ticker holding Quantity
     * @returns map
     */
    static TreeMap<String, Double> generateQuantity(String[] portfolios)
    {
        TreeMap<String,Double> map = new TreeMap<>();
        
         for(String holdings:portfolios)
         {
             String[] individHoldings = holdings.split(",");
            
  
             String Key = individHoldings[0];
             Double Quantity = Double.parseDouble(individHoldings[2]); //position 2 holds the inital quantities

             map.put(Key, Quantity);
         
         }
        return map;
    }
    
    
    /******* Function: generateValue *******
     * -----------------------------------------------
     * Maps the Ticker to it's respective Value
     * @returns the Map holding each Price per Stock
     */
    static TreeMap<String, Double> generateValue(TreeMap<String,Double> Quantity, TreeMap<String,Double> Price)
    {
        TreeMap<String,Double> map = new TreeMap<>();
        
        for(Map.Entry<String,Double> entry: Quantity.entrySet())
        {
            
        double number = Quantity.get(entry.getKey());
        double priceperstock = Price.get(entry.getKey());
        
        String Key = entry.getKey();
        Double value = number * priceperstock;
        
        map.put(Key, value);
        
        
        }
        
        return map;
       
    }
    
    /******* Function: generateNav *******
     * -----------------------------------------------
     * Maps the Ticker to it's respective NAV Values
     * 
     * @returns the Map holding each NAV
     */
    static TreeMap<String, Double> generateNav(TreeMap<String,Double> Values, double totalHoldings)
    {
        TreeMap<String,Double> map = new TreeMap<>();
        
        for(Map.Entry<String,Double> entry: Values.entrySet())
        {
            
        
        String Key = entry.getKey();
        Double value = (entry.getValue()/totalHoldings)*100;
        
        map.put(Key, value);
        
        
        }
        
        return map;
       
    }
   
    /******* Function: totalHoldings ********
     * ---------------------------------------------
     *Finds the total Holdings
     */
    static double totalHoldings(TreeMap<String, Double> Holdings){
        
            double holdingValue =0;
        
            for(Map.Entry<String,Double> entry: Holdings.entrySet())
            {
                
            holdingValue += entry.getValue();
            
            }
            
        return holdingValue;
    }
   
    
    static String generateTransactions(String inputString) {
        
        
        /*Initialize TreeMaps*/
        
        TreeMap<String, Double> MapPrice = new TreeMap<>();
        
        TreeMap<String, Double> MapQuantityPortfolio = new TreeMap<>();
        TreeMap<String, Double> MapQuantityBenchmark = new TreeMap<>();
        
        TreeMap<String, Double> MapValuePortfolio = new TreeMap<>();
        TreeMap<String, Double> MapValueBenchmark = new TreeMap<>();
        
        
        
        TreeMap<String, Double> MapNAVPortfolio = new TreeMap<>();
        TreeMap<String, Double> MapNAVBenchmark = new TreeMap<>();
        
        TreeMap<String, String[]> FinalString = new TreeMap<>();
        
        /*Parse Strings*/
        String[] portfolios = inputString.split(COLON);
        String[] Portfolios= portfolios[0].split(SEPARATOR);
        String[] Benchmark= portfolios[1].split(SEPARATOR);
        
        /*Generate Maps to Compare*/
        MapPrice = generatePrice(Benchmark);
        
        MapQuantityPortfolio = generateQuantity(Portfolios);
        MapQuantityBenchmark = generateQuantity(Benchmark);
        
       
        MapValuePortfolio = generateValue(MapQuantityPortfolio, MapPrice);
        MapValueBenchmark = generateValue(MapQuantityBenchmark, MapPrice);
        
        /*Determine totalHoldings*/
        double totalholdingsPortfolio = totalHoldings(MapValuePortfolio);
        double totalholdingsBenchmark = totalHoldings(MapValueBenchmark);
        
        /*Generate Map with NAV*/
        MapNAVPortfolio = generateNav(MapValuePortfolio,totalholdingsPortfolio);
        MapNAVBenchmark = generateNav(MapValueBenchmark, totalholdingsBenchmark);
        
        
        /*String Value to Return*/
        String returnVal = new String();
        
        /*Decimal Format we Want*/
        DecimalFormat df = new DecimalFormat("#.00");
        
        /*Generate final Map to evaluate*/
        for(String holdings:Portfolios)
        {
            String[] individHoldings = holdings.split(",");
           
            String Key = individHoldings[0];
            String[] Value = new String[2];
           
            if(MapQuantityPortfolio.get(Key) > MapQuantityBenchmark.get(Key)){
            	Value[0] = "SELL";
            }
            else if(MapQuantityPortfolio.get(Key) < MapQuantityBenchmark.get(Key)){
            	Value[0] = "BUY";
            }
            /*Formula for determining quantity*/
            double quantity = ((MapNAVBenchmark.get(Key)/100 * totalholdingsPortfolio) -
            				  MapValuePortfolio.get(Key))/MapPrice.get(Key);
           
            
            Value[1] = df.format(quantity);

            FinalString.put(Key, Value);
        
        }
        
        
       
        /*Print String*/
        

        for(Map.Entry<String, String[]> entry: FinalString.entrySet())
        {
            String[] values = entry.getValue();
            returnVal += '[' + values[0] + ", " + entry.getKey() +", "+ values[1] + ']' + ", ";
        }
        
        return returnVal.substring(0,returnVal.length()-2);
        
            
         
    }
    public static void main(String[] args) throws IOException{
        Scanner in = new Scanner(System.in);
        String res;
        String _input;
        try {
            _input = in.nextLine();
        } catch (Exception e) {
            _input = null;
        }
        res = generateTransactions(_input);
        System.out.println(res);
    }
}

/*


VOD,Vodafone,10@GOOG,Google,15@MSFT,Microsoft,12:VOD,Vodafone,16,2@GOOG,Google,10,5@MSFT,Microsoft,25,6
Expected Output Download Test Output
[SELL, GOOG, -7.80], [BUY, MSFT, 6.00], [BUY, VOD, 1.52]
*/
