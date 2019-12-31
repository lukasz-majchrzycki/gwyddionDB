package eu.nanocode.gwyddionDB;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.hibernate.Session;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application
{
		
	public static void main( String[] args ) throws IOException
    {
		launch();	
    }
    
    
    private URL getFileFromResources(String fileName) {
        URL resource = App.class.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return resource;
        }

    }


	@Override
	public void start(Stage primaryStage) throws Exception, IOException {
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
       	GwyddionDbConn conn = new GwyddionDbConn(session);
       		
        Parent root=new FXMLLoader(this.getFileFromResources("gwyddionDB.fxml")).load();        
        
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("GwyddionDB utility");
        primaryStage.show();    

       	session.close();    	      	
      	HibernateUtil.shutdown(); 
		
	}
    
 
}
