
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CPGfinder {

    
	// DEFINE GC PERCENTAGE CPG CRITERIA
	public static double getGC (String input) {
		double gcCount = 0.0;
		int gCount = 0;
		int cCount = 0;
		// convert input string into upper case
		input = input.toUpperCase();
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i)=='G') {
				gCount++;
			}
			if (input.charAt(i)=='C') {
				cCount++;
			}
		}
		gcCount = (gCount + cCount)*100.0/(input.length());
		return gcCount;
	}
	
	//DEFINE OBSERVED/EXPECTED RATIO CPG CRITERIA
	public static double getRatio (String input) {
		double ratio = 0.0;
		int gCount = 0;
		int cCount = 0;
		input = input.toUpperCase();
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i)=='G') {
				gCount++;
			}
			if (input.charAt(i)=='C') {
				cCount++;
			}
		}
		double expectedGC = gCount * cCount;
		String findStr = "CG";
		int lastIndex = 0;
		int observedGC = 0;

		while(lastIndex != -1){

		       lastIndex = input.indexOf(findStr,lastIndex);

		       if( lastIndex != -1){
		    	     observedGC ++;
		             lastIndex+=findStr.length();
		      }
		}
		ratio = ((observedGC / expectedGC) * (input.length() * 1.0));
		return ratio;
	}
	
	// FUNCTION TO CHECK FOR CPG CRITERIA ON AN INPUT STRING
	public static boolean cpgCriteriaCheck (String input) {
		boolean check = false;
		double gcCount = getGC(input);
		double gcRatio = getRatio(input);
		if ((gcCount > 50.0) && (gcRatio > 0.6)){
			check = true;
		}
		return check;
	}
	
	// GET STARTING AND END POINTS OF CPG ISLANDS
	public static List<Integer> getInitialCPG (String input, int width) {
		List<Integer> outputList = new ArrayList<Integer> ();
        int i = 0;
        while (i < input.length()-(width-1)) {
            String subInput = null;
            if ((i + width) < input.length()) {
                subInput = input.substring(i, i+width);
            }
            else {
            	// If substring length is less than width in the main string
                subInput = input.substring(i);
            }
            // Checking if criteria is met or not
            boolean checkResult = cpgCriteriaCheck(subInput);
            if (checkResult) {
            	// Add start and end intervals
                outputList.add(i);
                outputList.add(i+width);
            }
            i++;
        }
		return outputList;
	}
	
	// PARSE THE INITIAL START AND END POINTS
	
	public static List<Integer> parseList(List<Integer> input, int difference) {
	    List<Integer> output = new ArrayList<Integer> ();
	    if (input.size() > 0) {
	        // always use first element
	        int indexToAdd = -1;
	        output.add(input.get(0));
	        // add second element if input list size is equal to 2
	        if ((input.size()==2)) {
	        	output.add(input.get(1));
	        }
	        // LOOP THROUGH THE REMAINING LIST
	        for (int i = 2; i < input.size(); i+=2) {
	        	// FOR SUCCESSIVE ELEMENTS
	            if ( (input.get(i) - input.get(i-1)) <= difference) {
	            	// CODING FOR THE LAST TWO ELEMENTS OF INPUT LIST
	                if (i >= input.size()-2) {
	                    output.add(input.get(i));
	                    output.add(input.get(i+1));
	                }
	                // SET INDEX TO i+1
	                else { 
	                    indexToAdd = i+1;
	                }
	            }
	            else {
	                if(indexToAdd != -1) {
	                    output.add(input.get(indexToAdd));
	                    indexToAdd = -1;
	                } 
	               	output.add(input.get(i));
	               	output.add(input.get(i+1));
	            }
	        }
	    }
	    return output;
	}
    // FINAL REVIEW OF CPG ISLANDS
    
    public static List<Integer> finalCPGIslands (List<Integer> iList, String iSeq, int width) {
    	List<Integer> oList = new ArrayList<Integer> ();
    	for (int i = 1; i < iList.size()-2; i+=2 ) {
    		String testSeq = null;
    		testSeq = iSeq.substring(iList.get(i-1), iList.get(i)+1);
    		boolean check = cpgCriteriaCheck(testSeq);
    		if (check) {
    			// If condition is met, add the indexes and GC Count to the final list
    			oList.add(iList.get(i-1));
    			oList.add(iList.get(i));
    		}
    		// If condition is not met, start removing one character at a time until condition is met
    		else {
    			
    			int counter = 0;
    			int currentSequenceLength = testSeq.length();
    			String newTestSeq = null;
    			while (counter<=currentSequenceLength) {
    				counter++;
    				if (testSeq.length()>2) {
    					newTestSeq = testSeq.substring(1, testSeq.length()-1);
        				testSeq = newTestSeq;
        				if (newTestSeq.length()<width) {
        					counter = currentSequenceLength+1;
        				} else {
        					boolean checkAgain = cpgCriteriaCheck(newTestSeq);
        					// If condition met, add the item to list and exit
            				if(checkAgain) {
            						oList.add(iList.get(i-1)+counter);
                					oList.add(iList.get(i)-counter);
            					counter = currentSequenceLength+1;
            				}
        				
        				} // End of Else
    				} // End of IF
    				
    			} // End of While
    		} // End of Else 
    	} // End of For
    	return oList;
    }

    
    public static void main(String[] args) {


        String content = null;
		try {
			content = new Scanner(new File("D:\\input.txt")).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String seq2 = content;

    	// Get the start intervals of CPG Islands
    	
    	List<Integer> tempOutput1 = getInitialCPG(seq2, 200);
    	
    	
    	// parse the intervals of CPG Islands
    	List<Integer> tempOutput2 = parseList(tempOutput1, 100);
    	
    	// Get the final CPG Islands
    	List<Integer> output = finalCPGIslands(tempOutput2, seq2, 200);
    	
    	System.out.println("String Length is: "+seq2.length());

        System.out.println("Size Output is: "+output.size());
        
        int count = 1;
        if (output.size()==0) {
        	System.out.println("No CPG Islands Found");
        }
        else {
        	DecimalFormat df = new DecimalFormat("#.##");
    		
        	for (int i = 1; i < output.size()-1; i+=2) {
        		double ratio = getRatio(seq2.substring(output.get(i-1), output.get(i)));
        		System.out.println("==========================================");
                System.out.println("The start interval of CPG Island number "+count+" is: "+output.get(i-1));
                System.out.println("The end interval of CPG Island number "+count+" is: "+output.get(i));
                System.out.println("The GC Count Percentage of CPG Island number "+count+" is: "+getGC(seq2.substring(output.get(i-1), output.get(i)))+"%");
                System.out.println("The GC Ratio of CPG Island number "+count+" is: "+df.format(ratio));
                System.out.println("The sequence of CPG Island number "+count+" is: "+seq2.substring(output.get(i-1), output.get(i)));
                System.out.println("==========================================");
                count++;
            }
            
        }     
    }

}

