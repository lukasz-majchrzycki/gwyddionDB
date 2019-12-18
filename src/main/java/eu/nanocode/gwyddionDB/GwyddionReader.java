package eu.nanocode.gwyddionDB;

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
	ArrayList<Pattern> patternList = new ArrayList<>();
	
	ByteBuffer byteBuffer;
	
	   public GwyddionReader() {
		   
		    dataTypes.put('b', 1);
	    	dataTypes.put('c', 1);
	    	dataTypes.put('i', 4);
	    	dataTypes.put('q', 8);
	    	dataTypes.put('d', 8);
	    	dataTypes.put('s', 0);
	    	dataTypes.put('o', -1);

	    	patternList.add(Pattern.compile("/[0-9]*/base/min"));
	    	patternList.add(Pattern.compile("/[0-9]*/base/max"));
	    	patternList.add(Pattern.compile("xreal"));
	    	patternList.add(Pattern.compile("yreal"));
	    	patternList.add(Pattern.compile("/[0-9]*/base/range-type"));
	    	patternList.add(Pattern.compile("xres"));
	    	patternList.add(Pattern.compile("yres"));
	    	patternList.add(Pattern.compile("/[0-9]*/data/title"));
	    	patternList.add(Pattern.compile("si_unit_xy"));
	    	patternList.add(Pattern.compile("si_unit_z"));
	    	patternList.add(Pattern.compile("/[0-9]*/data"));
	    	patternList.add(Pattern.compile("data"));
	    	
	    	imageDoubleData.put(patternList.get(0), new ArrayList<>()  );
	    	imageDoubleData.put(patternList.get(1), new ArrayList<>()  );
	    	imageDoubleData.put(patternList.get(2), new ArrayList<>()  );
	    	imageDoubleData.put(patternList.get(3), new ArrayList<>()  );
	    	   	
	    	imageIntData.put(patternList.get(4), new ArrayList<>()  );
	    	imageIntData.put(patternList.get(5), new ArrayList<>()  );
	    	imageIntData.put(patternList.get(6), new ArrayList<>()  );
	    	    	
	    	imageStringData.put(patternList.get(7), new ArrayList<>()  );
	    	imageStringData2.put(patternList.get(8), new ArrayList<>()  );
	    	imageStringData2.put(patternList.get(9), new ArrayList<>()  );
	    	    	
	    	imageDataArray.put(patternList.get(10), new ArrayList<>()  );
	    	imageDataArray.put(patternList.get(11), new ArrayList<>()  );
	    	
	    	byteBuffer = ByteBuffer.allocate(8);
	    	byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
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
			return byteBuffer.getInt();
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
		   return byteBuffer.put(bArray).rewind().getInt() ;
	   }
	   
	   private double getDouble(byte[] bArray) {
		   byteBuffer.rewind();
		   return byteBuffer.put(bArray).rewind().getDouble() ;
	   }
	       
	    private int readContainer(DataInputStream fileStream, int index, int size) throws IOException {
	    	String str, str2;
	    	char c;
	    	int bitNo, objectSize, slashPos, posCount=0, dataCount, arraySize;
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
			else {
				upperCase=false;
			}
			if(bitNo==-10) throw new IllegalArgumentException("Unknown data type");		
	
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
				}else if (str2.equals("GwyDataField") )
				{			
		    		dataCount=0;
			    		do {
			    			dataCount+= readContainer(fileStream, index,objectSize);
			    		} while(dataCount<objectSize);
					posCount+=objectSize;	
					}
				else {
					fileStream.readNBytes(objectSize);
					posCount+=objectSize;
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
	    	int i, j, dataSize, dataCount;   	
	    	
	    	try {
	    		fileStream = new DataInputStream(new FileInputStream(file));
	    		for(i=0;i<17;i++) {
	    			c=(char) fileStream.readByte();
	    			if(c!=header[i]) throw new IllegalArgumentException("Not a Gwyddion file!");
	    		}  		
	    		
	    		dataSize=this.readIntFromFile(fileStream);
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
	    	
	    	for(i=0, j=0;i<this.imageDataArray.get(patternList.get(11) ).size();i++) {
	    		if(this.imageDataArray.get(patternList.get(11)).get(i).isEmpty()) {
	    			continue;
	    		}
	    		
	    		afmImages.add(new AfmImage());
	    		Double min, max;
	    		
	    		min=this.imageDoubleData.get(patternList.get(0)).get(i);
	    		if(min==null) {
	    			min=Double.POSITIVE_INFINITY;
	    			for(Double x: this.imageDataArray.get(patternList.get(11) ).get(i) ) {
	    				if(x<min)	min=x;
	    			}
	    		}
	    		afmImages.get(j).setMinZ(min);
	    		
	    		max=this.imageDoubleData.get(patternList.get(1)).get(i);
	    		if(max==null) {
	    			max=Double.NEGATIVE_INFINITY;
	    			for(Double x: this.imageDataArray.get(patternList.get(11) ).get(i) ) {
	    				if(x>max)	max=x;
	    			}
	    		}
	    		afmImages.get(j).setMaxZ(max);
	    		
	    		afmImages.get(j).setXreal(this.imageDoubleData.get(patternList.get(2)).get(i) );
	    		afmImages.get(j).setYreal(this.imageDoubleData.get(patternList.get(3)).get(i) );
	    		
	    		afmImages.get(j).setXres(this.imageIntData.get(patternList.get(5)).get(i) );
	    		afmImages.get(j).setYres(this.imageIntData.get(patternList.get(6)).get(i) );
	    		
	    		afmImages.get(j).setTitle(this.imageStringData.get(patternList.get(7)).get(i) );
	    		
	    		afmImages.get(j).setSi_unit_xy(this.imageStringData2.get(patternList.get(8)).get(i) );
	    		afmImages.get(j).setSi_unit_z(this.imageStringData2.get(patternList.get(9)).get(i) );
	    		
	    		afmImages.get(j).afmMap=this.imageDataArray.get(patternList.get(11)).get(i);
	    		j++;
	    		
	    	}
	    	return afmImages;
	    }	

}
