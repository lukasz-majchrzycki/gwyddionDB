package eu.nanocode.gwyddionDB;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.ResourceBundle;

import javax.persistence.TransactionRequiredException;

import org.hibernate.Session;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
	
	protected Stage stage;
	protected final FileChooser fileChooser = new FileChooser();
	
	Panel leftPanelObj, rightPanelObj; 
	private long projID;
	private Long imageID;
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
    private Button addProjectButton;

    @FXML
    private Button removeProjectButton;

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
    private TilePane imgPanel;
  
    @FXML
    private GridPane buttonPanel;
    
    @FXML
    private VBox windowPanel;
    
    @FXML
    private Label leftStatus;
    @FXML
    private Label rightStatus;
       
    private class ImageData{
    	AfmImage afmImage;
    	WritableImage writableImage;
    	ImageView imageView;
		public ImageData(AfmImage afmImage, WritableImage writableImage, ImageView imageView) {
			this.afmImage = afmImage;
			this.writableImage = writableImage;
			this.imageView = imageView;
		}
    	
    }
    private ObservableList<ProjectItem> obsProjectList;
    private ObservableMap<Long, ImageData> obsImages;
    
    private String newProjectName;
    private Label emptyProjectInfo;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       	colName.setCellValueFactory(new PropertyValueFactory<>("ProjectName"));
       	colModification.setCellValueFactory(new PropertyValueFactory<>("ModificationTimeString"));
       	colCreation.setCellValueFactory(new PropertyValueFactory<>("CreationTimeString"));
       	obsProjectList = FXCollections.observableArrayList();
       	projectList.setItems(obsProjectList);
       	imgPanel.setHgap(10);
       	imgPanel.setVgap(10);
       	emptyProjectInfo = new Label("No images in project");
       	
       	obsImages = FXCollections.observableMap(new HashMap<>());
       	obsImages.addListener( new MapChangeListener<Long, ImageData>() {

			@Override
			public void onChanged(Change<? extends Long, ? extends ImageData> change) {
				if(obsImages.isEmpty()) {
					imgPanel.getChildren().add(emptyProjectInfo);
				}
				else {
					imgPanel.getChildren().remove(emptyProjectInfo);
				}
				
				if (change.wasAdded()) {
					imgPanel.getChildren().add( change.getValueAdded().imageView );
				}else if (change.wasRemoved()) {
					imgPanel.getChildren().remove(change.getValueRemoved().imageView);
				}
				imgPanel.setPrefWidth(centerPanel.getWidth()-20);
			}
       		
       	}
       			);
       	     	
       	projectList.setPlaceholder(new Label("No DB connection. Press Connect DB to start..."));
       	leftPanelObj = new Panel(true,284.0,leftPanel, leftButton);
       	rightPanelObj = new Panel(true,280.0,rightPanel, rightButton);
       	centerPanel.setFitToWidth(true);
       	leftPanel.setFillWidth(true);
       	rightPanel.setFitToWidth(true);
       	
       	fileChooser.setTitle("Open AFM data File");
       	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("gwyddion files", "*.gwy") );
       	     	
       	openButton.setDisable(!connState);
       	addButton.setDisable(!connState);
       	removeButton.setDisable(!connState);
       	addProjectButton.setDisable(!connState);
       	removeProjectButton.setDisable(!connState);
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
           	projectList.setPlaceholder(new Label("No project in DB. Add project to start..."));

    	} else {
           	session.close();    	      	
          	HibernateUtil.shutdown(); 
          	
           	connState=false;
           	connectButton.setText("Connect DB");
           	obsProjectList.clear();
           	projectList.setPlaceholder(new Label("DB connection stopped. Connect DB to start..."));
    	}
       	openButton.setDisable(!connState);
       	addButton.setDisable(!connState);
       	removeButton.setDisable(!connState);
       	addProjectButton.setDisable(!connState);
       	removeProjectButton.setDisable(!connState);
    	
    }
    
    @FXML
    public void removeImage(ActionEvent event) {
    	try{
    		conn.removeImage(imageID);
    		obsImages.remove(imageID);
    	}catch (Exception e) {
    		Alert alert = new Alert(AlertType.ERROR, "Select image from project", ButtonType.OK);
    		alert.show();
    	}
    	
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
    	buttonPanel.setPrefWidth(centerPanel.getWidth()-10);
    	
    }
    
    @FXML
    void leftPanelChange(ActionEvent event) {
    	PanelChange(leftPanelObj);
    }

    @FXML
    void rightPanelChange(ActionEvent event) {
    	PanelChange(rightPanelObj);
    }
    
    private byte palette(double x, double min, double max) {
		return (byte)( (x -min ) / (max - min ) *Byte.MAX_VALUE );
    }
    
    private void addObsImages(List<AfmImage> newImgList) {
		for(AfmImage x : newImgList)
		{
			PixelWriter pixelWriter;
			ByteBuffer buffer = ByteBuffer.allocateDirect(3* x.getXres() *  x.getYres());
			WritableImage wi = new WritableImage(x.getXres(),  x.getYres());
			ImageView i = new ImageView();
			obsImages.put(x.getImageID(), new ImageData(x,wi,i));
			
    		for(Double y: x.afmMap) {
    			byte b = palette(y, x.getMinZ(), x.getMaxZ());
    			buffer.put(b); buffer.put(b); buffer.put(b);
    		}
    		buffer.rewind();
    		byte[] b = new byte[buffer.remaining()];
    		buffer.get(b);
    		
    		pixelWriter=wi.getPixelWriter();
    		pixelWriter.setPixels(0, 0, x.getXres(), x.getYres(),
    				WritablePixelFormat.getByteRgbInstance(), b, 0, x.getXres()*3);
    		
			i.setImage(wi) ;
			i.setFitWidth(256);
			i.setPreserveRatio(true);
			i.setOnMouseClicked((e) -> {
				imageID=x.getImageID();
			});
		}
    }
    
    @FXML
    void openProject (ActionEvent event) {
    	try {
    		ProjectItem selectedProject = projectList.getSelectionModel().getSelectedItem();
    		projID=selectedProject.getProjectID();
     		List<AfmImage> newImgList = new ArrayList<>();
    		newImgList.addAll(conn.getAll(projID));
    		obsImages.clear();		
    		
    		imgPanel.getChildren().clear();
    		if(obsImages.isEmpty()) {
    			imgPanel.getChildren().add(emptyProjectInfo);
    		addObsImages(newImgList);	
    		imageID = null;
    		
    		}
	} catch (NullPointerException e)
	{
		Alert alert = new Alert(AlertType.ERROR, "Select project", ButtonType.OK);
		alert.show();
	} 

    }
    
    private String TextFieldDialog(String s) {
    	Stage dialogStage = new Stage();
    	dialogStage.initModality(Modality.WINDOW_MODAL);
    	newProjectName = null;
    	
    	TextField textField = new TextField();
    	
    	HBox hbox = new HBox(new Text(s), textField);
    	hbox.setAlignment(Pos.CENTER);
    	hbox.setSpacing(10);
    	
    	Button OkButton = new Button("OK");
    	OkButton.setOnAction( new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				newProjectName = textField.getText();
				dialogStage.close();
			}
    		
    	});
    	
    	Button CancelButton = new Button("Cancel");
    	CancelButton.setOnAction( new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				dialogStage.close();
			}
    		
    	});
    	
    	HBox hbox2 = new HBox(OkButton, CancelButton);
    	hbox2.setAlignment(Pos.CENTER);
    	hbox2.setSpacing(10);
    	VBox vbox = new VBox(hbox, hbox2);
    	vbox.setAlignment(Pos.CENTER);
    	vbox.setPadding( new Insets(10,10,10,10) );
    	vbox.setSpacing(10);

    	dialogStage.setScene(new Scene(vbox));
    	dialogStage.showAndWait();
    	
    	return newProjectName;
    }
    
    @FXML
    void addProject(ActionEvent event) { 
    	try {
        	String projectName = TextFieldDialog("Project name: ");
        	obsProjectList.add(conn.addProject(projectName) );
    	} catch (NullPointerException e) {
    	}
    }
    
    @FXML
    void removeProject(ActionEvent event) {
    	try {
    		ProjectItem selectedProject = projectList.getSelectionModel().getSelectedItem();
    		obsProjectList.remove(conn.removeProject(selectedProject.getProjectID()) );
    	}
    	catch (NullPointerException e)
    	{
    		Alert alert = new Alert(AlertType.ERROR, "Select project", ButtonType.OK);
    		alert.show();
    	} 
    	catch (TransactionRequiredException e) {
    		Alert alert = new Alert(AlertType.ERROR, "Error during remove project. Reconnect database and try again", ButtonType.OK);
    		alert.show();
    	}
    }
    
    @FXML
    void addImage(ActionEvent event) throws IOException {
    	List<File> filelist = fileChooser.showOpenMultipleDialog(stage);
        if (filelist != null) {
            for(File file: filelist) {
                GwyddionReader reader = new GwyddionReader();
            	List<AfmImage> afmImages = reader.readAfmFile(file);
                addObsImages(conn.sendAll(projID, afmImages) );
            }
        }
    }
    
}
