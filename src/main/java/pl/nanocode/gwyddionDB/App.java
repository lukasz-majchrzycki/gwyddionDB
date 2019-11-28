package pl.nanocode.gwyddionDB;

import java.util.List;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App 
{
	static Map<Character, Integer> dataTypes = new HashMap<>();
	static Map<Pattern, ArrayList<Double>> imageDoubleData = new HashMap<>();
	static Map<Pattern, ArrayList<Integer>> imageIntData = new HashMap<>();
	static Map<Pattern, ArrayList<String>> imageStringData = new HashMap<>();
	static Map<Pattern, ArrayList<ArrayList<Double>> > imageDataArray = new HashMap<>();
	
	public static void main( String[] args ) throws IOException
    {
    	App main=new App();
       	File file = main.getFileFromResources("test-log.gwy");
       	List<AfmImage> afmImageList = readAfmFile(file);  	
    	
    }
    
    
    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
    
    private static String readStringFromFile (DataInputStream fileStream) throws IOException {
		StringBuilder str = new StringBuilder();
		char c;
		do {
			c=(char) fileStream.readByte();
			str.append(c);
		}while(c!='\u0000'); 
		str.deleteCharAt(str.length()-1);
		
		return str.toString();
    }
    
    private static int readContainer(DataInputStream fileStream, int index, int size) throws IOException {
    	String str, str2;
    	char c;
    	int bitNo, objectSize, slashPos, posCount=0, dataCount, arraySize;
    	ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    	byte[] bArray = {};
    	boolean upperCase=false;
    	
		
		str = App.readStringFromFile(fileStream); 
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

		bitNo=-10;
		for(Map.Entry<Character, Integer> entry : dataTypes.entrySet()) {
			if(entry.getKey()==c) 
				bitNo=entry.getValue();
		}
		if(bitNo==-10) {
			if(Character.isUpperCase(c) )
			{
				c=Character.toLowerCase(c);
				upperCase=true;
				for(Map.Entry<Character, Integer> entry : dataTypes.entrySet()) {
					if(entry.getKey()==c) 
						bitNo=entry.getValue();
				}
				
			}
		}
		if(bitNo==-10) throw new IllegalArgumentException("Unknown data type");

System.out.printf(str+"\t"+c+"\t"+bitNo+"\n");			

		byteBuffer.rewind();
		
		for(Map.Entry<Pattern, ArrayList<Double>> entry: App.imageDoubleData.entrySet()) {
			while(index>=entry.getValue().size()) {
				entry.getValue().add(null);
			}
		}
		
		for(Map.Entry<Pattern, ArrayList<Integer>> entry: App.imageIntData.entrySet()) {
			while(index>=entry.getValue().size()) {
				entry.getValue().add(null);
			}
		}
		
		for(Map.Entry<Pattern, ArrayList<String>> entry: App.imageStringData.entrySet()) {
			while(index>=entry.getValue().size()) {
				entry.getValue().add(null);
			}
		}
		
		for(Map.Entry<Pattern, ArrayList<ArrayList<Double>>> entry: App.imageDataArray.entrySet()) {
		/*	for(ArrayList<Double> list : entry.getValue())
			{
				while(index>=list.size())
					list.add(null);
			}*/
			while(index>=entry.getValue().size()) {
				entry.getValue().add(new ArrayList<>());
			}
		}

		if(upperCase) {
			byteBuffer.put(fileStream.readNBytes(4));
			posCount+=4;
			byteBuffer.rewind();
			arraySize=byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
		} else arraySize=1;
		
					
		for(int i=0;i<arraySize;i++)
		{	
			byteBuffer.rewind();
		if(bitNo>0) {
			bArray=fileStream.readNBytes(bitNo);
			posCount+=bitNo;
			if(c=='i') {
				for(Map.Entry<Pattern, ArrayList<Integer>> entry: imageIntData.entrySet() ) {
					Matcher matcher = entry.getKey().matcher(str.toString());
					if(matcher.matches()) {
						entry.getValue().set(index,byteBuffer.put(bArray)
						.order(ByteOrder.LITTLE_ENDIAN).rewind().getInt() );
	 					}
					}					
			}
			else if(c=='d' && !upperCase) {
				for(Map.Entry<Pattern, ArrayList<Double>> entry: imageDoubleData.entrySet() ) {
					Matcher matcher = entry.getKey().matcher(str.toString());
					if(matcher.matches()) {
						entry.getValue().set(index,byteBuffer.put(bArray)
						.order(ByteOrder.LITTLE_ENDIAN).rewind().getDouble() );
	 					}
					}				
			}
			else if(c=='d' && upperCase) {
				for(Map.Entry<Pattern, ArrayList<ArrayList<Double>> > entry: imageDataArray.entrySet() ) {
					Matcher matcher = entry.getKey().matcher(str.toString());
					if(matcher.matches()) {
						entry.getValue().get(index).
						add(byteBuffer.put(bArray)
						.order(ByteOrder.LITTLE_ENDIAN).rewind().getDouble() );
	 					}
					}				
			}
			
		}
		else if(bitNo==0) {
			str2 = App.readStringFromFile(fileStream);
			posCount+=str2.length()+1;	
			for(Map.Entry<Pattern, ArrayList<String>> entry: imageStringData.entrySet() ) {
				Matcher matcher = entry.getKey().matcher(str.toString());
				if(matcher.matches()) {
					entry.getValue().set(index,str2);
 					}
				}
		}
		else if(bitNo==-1) {
			do {
				c=(char) fileStream.readByte();
				posCount++;
				
			}while(c!='\u0000'); 
			byteBuffer.put(fileStream.readNBytes(4));
			posCount+=4;
			byteBuffer.rewind();
			objectSize=byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
			
			//fileStream.readNBytes(objectSize);
			
    		dataCount=0;
    		do {
    			dataCount+= App.readContainer(fileStream, index,objectSize);
    		} while(dataCount<objectSize);
			
			posCount+=objectSize;
System.out.println("Object size: " + objectSize);

			
		}	
    	}
		return posCount;
	}
    
    
    
    public static List<AfmImage> readAfmFile (File file) throws IOException {
    	char[] header = {'G','W','Y','P',
    			'G', 'w', 'y', 'C', 'o', 'n', 't', 'a', 'i', 'n', 'e', 'r', '\u0000'};
    	
    	dataTypes.put('b', 1);
    	dataTypes.put('c', 1);
    	dataTypes.put('i', 4);
    	dataTypes.put('q', 8);
    	dataTypes.put('d', 8);
    	dataTypes.put('s', 0);
    	dataTypes.put('o', -1);
    	
    		
    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/min"), new ArrayList<>()  );
    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/max"), new ArrayList<>()  );
    	   	
    	imageIntData.put(Pattern.compile("/[0-9]*/base/range-type"), new ArrayList<>()  );
    	    	
    	imageStringData.put(Pattern.compile("/[0-9]*/data/title"), new ArrayList<>()  );
    	    	
    	imageDataArray.put(Pattern.compile("/[0-9]*/data"), new ArrayList<>()  );
    	imageDataArray.put(Pattern.compile("data"), new ArrayList<>()  );
    	
    	ArrayList<AfmImage> afmImages = new ArrayList<AfmImage>();
    	DataInputStream fileStream =null;
    	char c;
    	int i, dataSize, dataCount;
    	ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    	
    	
    	try {
    		fileStream = new DataInputStream(new FileInputStream(file));
    		for(i=0;i<17;i++) {
    			c=(char) fileStream.readByte();
    			if(c!=header[i]) throw new IllegalArgumentException("Not a Gwyddion file!");
    		}  		
    		
    		byteBuffer.put(fileStream.readNBytes(4));
    		byteBuffer.rewind();
    		dataSize=byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    		
    		
System.out.println("Data size: "+dataSize);

    		dataCount=0;
    		do {
    			dataCount+= App.readContainer(fileStream, -1,dataSize);
    		} while(dataCount<dataSize);
    		
    	}
    	finally
    	{
    		if(fileStream!=null)
    			fileStream.close();
    	}
    	
    	
    	return afmImages;
    }
}
