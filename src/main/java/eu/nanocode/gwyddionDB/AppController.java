package eu.nanocode.gwyddionDB;

import org.hibernate.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AppController {
	
	private boolean connState, leftPanState, rightPanState;
	private long projID, imageID;
	
	private GwyddionDbConn conn;
	private Session session;

    @FXML
    private Color x21;

    @FXML
    private Font x11;
    
    @FXML
    private Font x3;

    @FXML
    private Color x4;
   
    @FXML
    private Button connectButton;

    @FXML
    private Button openButton;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private ImageView imgBox;

    @FXML
    private TableView<?> detailsTable;

    @FXML
    private TableView<?> projectList;

    @FXML
    private Label leftStatus;
    
    @FXML
    private Label rightStatus;


    public void connect() {
    	if(!connState) {
        	session = HibernateUtil.getSessionFactory().openSession();
        	session.beginTransaction();
           	conn = new GwyddionDbConn(session);
           	
           	connState=true;
           	connectButton.setText("Disconnect...");
    	} else {
           	session.close();    	      	
          	HibernateUtil.shutdown(); 
          	
           	connState=false;
           	connectButton.setText("Connect DB");
    	}
    	
    }
    
    public void addImage() {
    	
    }

    public void removeImage() {
    	
    }
    
    
}
