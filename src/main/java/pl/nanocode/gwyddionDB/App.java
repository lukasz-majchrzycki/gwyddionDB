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
    
    
    public static List<AfmImage> readAfmFile (File file) throws IOException {
    	char[] header = {'G','W','Y','P',
    			'G', 'w', 'y', 'C', 'o', 'n', 't', 'a', 'i', 'n', 'e', 'r', '\u0000'};
    	Map<Character, Integer> dataTypes = new HashMap<>();
    	dataTypes.put('b', 1);
    	dataTypes.put('c', 1);
    	dataTypes.put('i', 4);
    	dataTypes.put('q', 8);
    	dataTypes.put('d', 8);
    	dataTypes.put('s', 0);
    	dataTypes.put('o', -1);
    	
    	Map<Pattern, byte[]> imageData = new HashMap<>();
    	imageData.put(Pattern.compile("/[0-9]*/data/title"), null  );
    	imageData.put(Pattern.compile("/[0-9]*/base/range-type"), null  );
    	imageData.put(Pattern.compile("/[0-9]*/base/min"), null  );
    	imageData.put(Pattern.compile("/[0-9]*/base/max"), null  );
    	//imageData.put(Pattern.compile("/[0-9]*/data"), null  );
    	
    	ArrayList<AfmImage> afmImages = new ArrayList<AfmImage>();
    	DataInputStream fileStream =null;
    	char c;
    	double d;
    	int i, dataSize, bitNo, objectSize;
    	StringBuilder str, str2;
    	byte[] bArray = {};
    	ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    	ByteBuffer byteBuffer2 = ByteBuffer.allocate(8);
    	
    	
    	try {
    		fileStream = new DataInputStream(new FileInputStream(file));
    		for(i=0;i<17;i++) {
    			c=(char) fileStream.readByte();
    			if(c!=header[i]) throw new IllegalArgumentException("Not a Gwyddion file!");
    		}  		
    		
    		byteBuffer.put(fileStream.readNBytes(4));
    		byteBuffer.rewind();
    		dataSize=byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    		
    		

    		
    		for(i=0;i<5;i++) {
    			
    			str = new StringBuilder();
    			do {
        			c=(char) fileStream.readByte();
        			str.append(c);
        		}while(c!='\u0000'); 
    			str.deleteCharAt(str.length()-1);
    			
    			

			c=(char) fileStream.readByte();

		
			bitNo=-10;
			for(Map.Entry<Character, Integer> entry : dataTypes.entrySet()) {
				if(entry.getKey()==c) 
					bitNo=entry.getValue();
			}
			if(bitNo==-10) throw new IllegalArgumentException("Unknown data type");



			if(bitNo>0) {
				bArray=fileStream.readNBytes(bitNo);
			}
			else if(bitNo==0) {
				str2 = new StringBuilder();
				do {
					c=(char) fileStream.readByte();
					str2.append(c);
				}while(c!='\u0000'); 
				str2.deleteCharAt(str2.length()-1);
				bArray=str2.toString().getBytes("US-ASCII");
			}
			else if(bitNo==-1) {
				do {
					c=(char) fileStream.readByte();
					
				}while(c!='\u0000'); 
				byteBuffer2.put(fileStream.readNBytes(4));
				byteBuffer2.rewind();
				objectSize=byteBuffer2.order(ByteOrder.LITTLE_ENDIAN).getInt();
				
				fileStream.readNBytes(objectSize);
				
			}	

			for(Map.Entry<Pattern, byte[]> entry: imageData.entrySet() ) {
				Matcher matcher = entry.getKey().matcher(str.toString());
				if(matcher.matches()) {
					entry.setValue(bArray);
 					}
				}
			

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
