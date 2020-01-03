package eu.nanocode.gwyddionDB;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AppController implements Initializable {
	
	private boolean connState, leftPanState, rightPanState;
	private long projID, imageID;
	
	private GwyddionDbConn conn;
	private Session session;
	List<ProjectItem> projectItemList;

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
    private TableView<ProjectItem> projectList;
    
    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colModification;

    @FXML
    private TableColumn<?, ?> colCreation;

    @FXML
    private Label leftStatus;
    
    @FXML
    private Label rightStatus;
    
    private ObservableList<ProjectItem> obsProjectList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       	colName.setCellValueFactory(new PropertyValueFactory<>("ProjectName"));
       	colModification.setCellValueFactory(new PropertyValueFactory<>("ModificationTimeString"));
       	colCreation.setCellValueFactory(new PropertyValueFactory<>("CreationTimeString"));
       	obsProjectList = FXCollections.observableArrayList();
       	projectList.setItems(obsProjectList);
       	
       	projectList.setPlaceholder(new Label("No DB connection. Press Connect DB to start..."));
    }

    public void connect() {
    	if(!connState) {
        	session = HibernateUtil.getSessionFactory().openSession();
        	session.beginTransaction();
           	conn = new GwyddionDbConn(session);
           	
           	connState=true;
           	connectButton.setText("Disconnect...");
           	
           	obsProjectList.addAll(conn.getProjectList());

    	} else {
           	session.close();    	      	
          	HibernateUtil.shutdown(); 
          	
           	connState=false;
           	connectButton.setText("Connect DB");
           	obsProjectList.clear();
           	projectList.setPlaceholder(new Label("DB connection stopped. Press Connect DB to start..."));
    	}
    	
    }
    
    public void addImage() {
    	
    }

    public void removeImage() {
    	
    }
    
    
}
