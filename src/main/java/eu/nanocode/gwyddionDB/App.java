package eu.nanocode.gwyddionDB;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application
{
	private Parent root;
	private FXMLLoader loader;
		
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
        loader=new FXMLLoader(this.getFileFromResources("gwyddionDB.fxml"));        
        root=loader.load();
        ((AppController) loader.getController()).stage = primaryStage; 
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getFileFromResources("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("GwyddionDB utility");
        primaryStage.setResizable(false);
        primaryStage.show();
	}
	
	@Override
	public void stop() {
		AppController appController = ((AppController) loader.getController());
		if ( appController.connState ) {
           	appController.session.close();    	      	
          	HibernateUtil.shutdown(); 
		}
	}
    
 
}
