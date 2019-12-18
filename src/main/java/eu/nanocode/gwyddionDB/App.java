package eu.nanocode.gwyddionDB;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class App 
{
		
	public static void main( String[] args ) throws IOException
    {
    	App main=new App();
       	File file = main.getFileFromResources("8px.gwy");
       	List<AfmImage> afmImageList = new GwyddionReader().readAfmFile(file);  	
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
    
 
}
