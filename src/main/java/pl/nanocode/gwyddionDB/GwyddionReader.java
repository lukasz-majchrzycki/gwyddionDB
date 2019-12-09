package pl.nanocode.gwyddionDB;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GwyddionReader {
	
	Map<Character, Integer> dataTypes = new HashMap<>();
	Map<Pattern, ArrayList<Double>> imageDoubleData = new HashMap<>();
	Map<Pattern, ArrayList<Integer>> imageIntData = new HashMap<>();
	Map<Pattern, ArrayList<String>> imageStringData = new HashMap<>();
	Map<Pattern, ArrayList<String>> imageStringData2 = new HashMap<>();
	Map<Pattern, ArrayList<ArrayList<Double>> > imageDataArray = new HashMap<>();
	
	ByteBuffer byteBuffer;
	
	   public GwyddionReader() {
		   
		    dataTypes.put('b', 1);
	    	dataTypes.put('c', 1);
	    	dataTypes.put('i', 4);
	    	dataTypes.put('q', 8);
	    	dataTypes.put('d', 8);
	    	dataTypes.put('s', 0);
	    	dataTypes.put('o', -1);
	    	
	    		
	    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/min"), new ArrayList<>()  );
	    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/max"), new ArrayList<>()  );
	    	imageDoubleData.put(Pattern.compile("xreal"), new ArrayList<>()  );
	    	imageDoubleData.put(Pattern.compile("yreal"), new ArrayList<>()  );
	    	   	
	    	imageIntData.put(Pattern.compile("/[0-9]*/base/range-type"), new ArrayList<>()  );
	    	imageIntData.put(Pattern.compile("xres"), new ArrayList<>()  );
	    	imageIntData.put(Pattern.compile("yres"), new ArrayList<>()  );
	    	    	
	    	imageStringData.put(Pattern.compile("/[0-9]*/data/title"), new ArrayList<>()  );
	    	imageStringData2.put(Pattern.compile("si_unit_xy"), new ArrayList<>()  );
	    	imageStringData2.put(Pattern.compile("si_unit_z"), new ArrayList<>()  );
	    	    	
	    	imageDataArray.put(Pattern.compile("/[0-9]*/data"), new ArrayList<>()  );
	    	imageDataArray.put(Pattern.compile("data"), new ArrayList<>()  );
	    	
	    	byteBuffer = ByteBuffer.allocate(8);
	}

	private String readStringFromFile (DataInputStream fileStream) throws IOException {
			StringBuilder str = new StringBuilder();
			char c;
			do {
				c=(char) fileStream.readByte();
				str.append(c);
			}while(c!='\u0000'); 
			str.deleteCharAt(str.length()-1);
			
			return str.toString();
	    }
	   
	   private int readIntFromFile (DataInputStream fileStream) throws IOException{
			byteBuffer.rewind();
			byteBuffer.put(fileStream.readNBytes(4));
			byteBuffer.rewind();
			return byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
	   }
	   
	   private int getBitNo (char c) {
			for(Map.Entry<Character, Integer> entry : dataTypes.entrySet()) {
				if(entry.getKey()==c) 
					return entry.getValue();
			}
			return -10;
	   }
	   
	   @SuppressWarnings("unchecked")
	   private <E >void extendMapList (Map<Pattern, ArrayList<E>> map, int index, boolean newArrayList) {
			for(Map.Entry<Pattern, ArrayList<E>> entry:  map.entrySet()) {
				while(index>=entry.getValue().size()) {
						entry.getValue().add((E) ( newArrayList ? (new ArrayList<>() ) : null)  );
				}
			}
	   }
	   
	   private <E> void addToMap (Map<Pattern, ArrayList<E>> map, E value,int index, String fieldName){
			for(Map.Entry<Pattern, ArrayList<E>> entry: map.entrySet() ) {
				Matcher matcher = entry.getKey().matcher(fieldName);
				if(matcher.matches()) {
					entry.getValue().set(index,value );
					
 				}
			}
	   }
	   
	   private <E> void addArrayFieldToMap (Map<Pattern, ArrayList<ArrayList<E>> > map, E value,int index, String fieldName){
			for(Map.Entry<Pattern, ArrayList<ArrayList<E>> > entry: map.entrySet() ) {
				Matcher matcher = entry.getKey().matcher(fieldName);
				if(matcher.matches()) {
					entry.getValue().get(index).add(value);
					
				}
			}
	   }
	   
	   private int getInt(byte[] bArray) {
		   byteBuffer.rewind();
		   return byteBuffer.put(bArray)
			.order(ByteOrder.LITTLE_ENDIAN).rewind().getInt() ;
	   }
	   
	   private double getDouble(byte[] bArray) {
		   byteBuffer.rewind();
		   return byteBuffer.put(bArray)
			.order(ByteOrder.LITTLE_ENDIAN).rewind().getDouble() ;
	   }
	       
	    private int readContainer(DataInputStream fileStream, int index, int size) throws IOException {
	    	String str, str2;
	    	char c;
	    	int bitNo, objectSize, slashPos, posCount=0, dataCount, arraySize;
	    	ByteBuffer byteBuffer = ByteBuffer.allocate(8);
	    	byte[] bArray = {};
	    	boolean upperCase=false;
			
			str = readStringFromFile(fileStream); 
			posCount+=str.length()+1;
			
			if(index==-1)
			{
				slashPos=str.indexOf("/", 1);
				if(slashPos>1) {
					try {
						index=Integer.parseInt(str.substring(1, slashPos));
					} catch(NumberFormatException e) {
						index=-1;
					}
					
				}
			}
			
			c=(char) fileStream.readByte();
			posCount++;

			bitNo=getBitNo(c);
			
			if(bitNo==-10 && Character.isUpperCase(c)) {
					c=Character.toLowerCase(c);
					upperCase=true;
					bitNo=getBitNo(c);					
			}
			if(bitNo==-10) throw new IllegalArgumentException("Unknown data type");

	System.out.printf(str+"\t"+c+"\t"+bitNo+"\n");			
	
			this.extendMapList(imageDoubleData, index, false);
			this.extendMapList(imageIntData, index, false);
			this.extendMapList(imageStringData, index, false);
			this.extendMapList(imageStringData2, index, false);	
			this.extendMapList(imageDataArray, index, true);
						
			if(upperCase) {
				arraySize=this.readIntFromFile(fileStream);
				posCount+=4;
				
			} else arraySize=1;
			
						
			for(int i=0;i<arraySize;i++)
			{	
			byteBuffer.rewind();
			if(bitNo>0) {
				bArray=fileStream.readNBytes(bitNo);
				posCount+=bitNo;
				if(c=='i') {
					this.addToMap(imageIntData, this.getInt(bArray), index, str);				
				}
				else if(c=='d' && !upperCase) {
					this.addToMap(imageDoubleData, this.getDouble(bArray), index, str);	
				}
				else if(c=='d' && upperCase) {
					this.addArrayFieldToMap(imageDataArray, this.getDouble(bArray), index, str);
				}
				
			}
			else if(bitNo==0) {
				str2 = readStringFromFile(fileStream);
				posCount+=str2.length()+1;
				this.addToMap(imageStringData, str2, index, str);
			}
			else if(bitNo==-1) {
				str2=readStringFromFile(fileStream);
				posCount+=str2.length()+1;
				
				objectSize=this.readIntFromFile(fileStream);
				posCount+=4;
				
				if(str2.equals("GwySIUnit") ) {
					posCount+=readStringFromFile(fileStream).length()+1;
					
					c=(char) fileStream.readByte();
					posCount++;
					
					str2=readStringFromFile(fileStream);
					posCount+=str2.length()+1;
					
					this.addToMap(imageStringData2, str2, index, str);
				}else {			
		    		dataCount=0;
		    		do {
		    			dataCount+= readContainer(fileStream, index,objectSize);
		    		} while(dataCount<objectSize);
					
					posCount+=objectSize;
		System.out.println("Object size: " + objectSize);

					
				}
				}
		
	    	}
			return posCount;
		}
	    
	    
	    
	    public List<AfmImage> readAfmFile (File file) throws IOException {
	    	char[] header = {'G','W','Y','P',
	    			'G', 'w', 'y', 'C', 'o', 'n', 't', 'a', 'i', 'n', 'e', 'r', '\u0000'};
	    	
	    	ArrayList<AfmImage> afmImages = new ArrayList<AfmImage>();
	    	DataInputStream fileStream =null;
	    	char c;
	    	int i, dataSize, dataCount;   	
	    	
	    	try {
	    		fileStream = new DataInputStream(new FileInputStream(file));
	    		for(i=0;i<17;i++) {
	    			c=(char) fileStream.readByte();
	    			if(c!=header[i]) throw new IllegalArgumentException("Not a Gwyddion file!");
	    		}  		
	    		
	    		dataSize=this.readIntFromFile(fileStream);
	    		
	    		
	System.out.println("Data size: "+dataSize);

	    		dataCount=0;
	    		do {
	    			dataCount+= readContainer(fileStream, -1,dataSize);
	    		} while(dataCount<dataSize);
	    		
	    	}
	    	finally
	    	{
	    		if(fileStream!=null)
	    			fileStream.close();
	    	}
	    	
	System.out.println("\nResults in double:");    	
	     	for(Map.Entry<Pattern, ArrayList<Double>> entry : imageDoubleData.entrySet()) {    		
	    		//if(entry.getValue()==null) continue;
	    				System.out.printf(entry.getKey().toString() );
	    		for(Double x: entry.getValue() )
	    				System.out.printf("\t"+x);
	    		System.out.println();
	    	}
	System.out.println("\nResults in Int:");    	
	     	for(Map.Entry<Pattern, ArrayList<Integer>> entry : imageIntData.entrySet()) {    		
	    		//if(entry.getValue()==null) continue;
	    				System.out.printf(entry.getKey().toString() );
	    		for(Integer x: entry.getValue()) 
	    				System.out.printf("\t"+x);
	    		System.out.println();
	    	}  
	System.out.println("\nResults in String:");    	
	     	for(Map.Entry<Pattern, ArrayList<String>> entry : imageStringData.entrySet()) {    		
	    		//if(entry.getValue()==null) continue;
	    		System.out.printf(entry.getKey().toString() );
	    		for(String x: entry.getValue() )
	    			System.out.printf("\t" + x);
	    		System.out.println();

	    	} 
	     	for(Map.Entry<Pattern, ArrayList<String>> entry : imageStringData2.entrySet()) {    		
	    		//if(entry.getValue()==null) continue;
	    		System.out.printf(entry.getKey().toString() );
	    		for(String x: entry.getValue() )
	    			System.out.printf("\t" + x);
	    		System.out.println();

	    	} 
	     	
	System.out.println("\nResults in Array:");    	
	     	for(Map.Entry<Pattern, ArrayList<ArrayList<Double>> > entry : imageDataArray.entrySet()) {    		
	    		//if(entry.getValue()==null) continue;
	    		System.out.printf(entry.getKey().toString() );
	    		for(ArrayList<Double> x: entry.getValue() ) {
	    			System.out.printf("\n item No." + x.size()+ "\n");
	    			for(Double y: x) {
	    				System.out.printf(y+ "\t");
	    			}
	    		}
	    			
	    		System.out.println();

	    	} 
	    	
	    	return afmImages;
	    }	

}
