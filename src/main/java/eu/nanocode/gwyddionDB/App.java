package eu.nanocode.gwyddionDB;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.hibernate.Session;


public class App 
{
		
	public static void main( String[] args ) throws IOException
    {
		App main=new App();
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
		    	
       	File file = main.getFileFromResources("test.gwy");
       	List<AfmImage> afmImageList = new GwyddionReader().readAfmFile(file);  	
		       	
       	for(AfmImage x: afmImageList) {
       		session.save(x);
       	}
		      	
     	session.getTransaction().commit();      	
      	HibernateUtil.shutdown(); 	
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
