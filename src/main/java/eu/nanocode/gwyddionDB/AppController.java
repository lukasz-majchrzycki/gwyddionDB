package eu.nanocode.gwyddionDB;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AppController implements Initializable {
	
	private class Panel{
		boolean state;
		double width;
		Region panel;
		Button button;
		public Panel(boolean state, double width, Region panel, Button button) {
			this.state = state;
			this.width = width;
			this.panel = panel;
			this.button = button;
		}
	}
	
	Panel leftPanelObj, rightPanelObj; 
	private long projID, imageID;
	protected boolean connState;
	
	protected GwyddionDbConn conn;
	protected Session session;

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
    private Button leftButton;
    @FXML
    private VBox leftPanel;
    
    @FXML
    private ScrollPane centerPanel;
    
    @FXML
    private Button rightButton;
    @FXML
    private ScrollPane rightPanel;
    
    @FXML
    private VBox windowPanel;
    
    @FXML
    private Label leftStatus;
    @FXML
    private Label rightStatus;
    
    private ObservableList<ProjectItem> obsProjectList;
    private ObservableList<AfmImage> obsImageList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       	colName.setCellValueFactory(new PropertyValueFactory<>("ProjectName"));
       	colModification.setCellValueFactory(new PropertyValueFactory<>("ModificationTimeString"));
       	colCreation.setCellValueFactory(new PropertyValueFactory<>("CreationTimeString"));
       	obsProjectList = FXCollections.observableArrayList();
       	projectList.setItems(obsProjectList);
       	
       	obsImageList = FXCollections.observableArrayList();
       	
       	projectList.setPlaceholder(new Label("No DB connection. Press Connect DB to start..."));
       	leftPanelObj = new Panel(true,284.0,leftPanel, leftButton);
       	rightPanelObj = new Panel(true,280.0,rightPanel, rightButton);
       	centerPanel.setFitToWidth(true);
       	leftPanel.setFillWidth(true);
       	rightPanel.setFitToWidth(true);
    }

    @FXML
    void connect(ActionEvent event) {
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
    
    private void PanelChange(Panel panel ) {
    	if( panel.state ) {
    		panel.panel.setPrefWidth(0);
    		panel.panel.setVisible(false);
    		centerPanel.setPrefWidth(centerPanel.getWidth()+panel.width);
    		panel.button.setRotate(180);
    		panel.state=false;
    	}
    	else {
    		panel.panel.setPrefWidth(panel.width);
    		panel.panel.setVisible(true);
    		centerPanel.setPrefWidth(centerPanel.getWidth()-panel.width);
    		panel.button.setRotate(0);
    		panel.state=true;
    	}
    }
    
    @FXML
    void leftPanelChange(ActionEvent event) {
    	PanelChange(leftPanelObj);
    }

    @FXML
    void rightPanelChange(ActionEvent event) {
    	PanelChange(rightPanelObj);
    }
    
    @FXML
    void openProject (ActionEvent event) {
    	try {
    		ProjectItem selectedProject = projectList.getSelectionModel().getSelectedItem();
    		obsImageList.addAll(conn.getAll(selectedProject.getProjectID())  );
    	} catch (NullPointerException e)
    	{
    		Alert alert = new Alert(AlertType.ERROR, "Select project", ButtonType.OK);
    		alert.show();
    	}  
    }
    
}
