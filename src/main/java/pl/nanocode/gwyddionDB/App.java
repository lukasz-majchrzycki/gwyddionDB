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
	static Map<Pattern, Double> imageDoubleData = new HashMap<>();
	static Map<Pattern, Integer> imageIntData = new HashMap<>();
	static Map<Pattern, String> imageStringData = new HashMap<>();
	static Map<Pattern, ArrayList<Double>> imageDataArray = new HashMap<>();
	
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
    
    private static void readContainer(DataInputStream fileStream) throws IOException {
    	String str, str2;
    	char c;
    	int bitNo, objectSize;
    	ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    	byte[] bArray = {};
    	
		
		str = App.readStringFromFile(fileStream);  
		c=(char) fileStream.readByte();

		bitNo=-10;
		for(Map.Entry<Character, Integer> entry : dataTypes.entrySet()) {
			if(entry.getKey()==c) 
				bitNo=entry.getValue();
		}
		if(bitNo==-10) throw new IllegalArgumentException("Unknown data type");

System.out.printf(str+"\t"+c+"\t"+bitNo+"\n");			

		byteBuffer.rewind();
		
		
		if(bitNo>0) {
			bArray=fileStream.readNBytes(bitNo);
			if(c=='i') {
				for(Map.Entry<Pattern, Integer> entry: imageIntData.entrySet() ) {
					Matcher matcher = entry.getKey().matcher(str.toString());
					if(matcher.matches()) {
						entry.setValue(byteBuffer.put(bArray)
						.order(ByteOrder.LITTLE_ENDIAN).rewind().getInt() );
	 					}
					}					
			}
			else if(c=='d') {
				for(Map.Entry<Pattern, Double> entry: imageDoubleData.entrySet() ) {
					Matcher matcher = entry.getKey().matcher(str.toString());
					if(matcher.matches()) {
						entry.setValue(byteBuffer.put(bArray)
						.order(ByteOrder.LITTLE_ENDIAN).rewind().getDouble() );
	 					}
					}				
			}
		}
		else if(bitNo==0) {
			str2 = App.readStringFromFile(fileStream);
			for(Map.Entry<Pattern, String> entry: imageStringData.entrySet() ) {
				Matcher matcher = entry.getKey().matcher(str.toString());
				if(matcher.matches()) {
					entry.setValue(str2);
 					}
				}
		}
		else if(bitNo==-1) {
			do {
				c=(char) fileStream.readByte();
				
			}while(c!='\u0000'); 
			byteBuffer.put(fileStream.readNBytes(4));
			byteBuffer.rewind();
			objectSize=byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
			
			fileStream.readNBytes(objectSize);
System.out.println("Object size: " + objectSize);
			
		}	
	

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
    	
    		
    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/min"), null  );
    	imageDoubleData.put(Pattern.compile("/[0-9]*/base/max"), null  );
    	   	
    	imageIntData.put(Pattern.compile("/[0-9]*/base/range-type"), null  );
    	    	
    	imageStringData.put(Pattern.compile("/[0-9]*/data/title"), null  );
    	    	
    	imageDataArray.put(Pattern.compile("/[0-9]*/data"), null  );
    	
    	ArrayList<AfmImage> afmImages = new ArrayList<AfmImage>();
    	DataInputStream fileStream =null;
    	char c;
    	int i, dataSize;
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

    		
    		for(i=0;i<5;i++) {
    			App.readContainer(fileStream);
    		}
    		
    	}
    	finally
    	{
    		if(fileStream!=null)
    			fileStream.close();
    	}
    	
    	
    	return afmImages;
    }
}
