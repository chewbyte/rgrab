
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application{

    Button button;

    Stage stage;
    Scene scene;
    BorderPane layout_main;
    VBox layout_sub;

    BufferedImage image;
    ImageView imageView;

    Group minimap;
    Rectangle[][] mapGrid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("rgrab");
        //stage.setResizable(false);
        stage.setOnCloseRequest(e -> closeProgram());

        // Create scene from layout
        layout_main = new BorderPane();
        layout_main.setStyle("-fx-background: #FFF;");
        layout_sub = new VBox();
        layout_sub.setStyle("-fx-background: #FC0;");
        layout_main.setRight(layout_sub);
        scene = new Scene(layout_main,640,480);

        // Create and add MenuBar to Group
        MenuBar menuBar = createMenuBar();
        layout_main.setTop(menuBar);

        //Create minimap
        createMinimap();
        layout_sub.getChildren().add(minimap);

        stage.setScene(scene);
        stage.show();
    }

    // Allows user to select an image to open
    public void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");

        // Set supported image filters
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Supported Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            openFile(file);
        }
        // else display an error popup
    }

    // Attempts to open and display the image specified
    public void openFile(File file){
        try{
            String filepath = "file:///"+file.getAbsolutePath();
            // Read in image from file
            try {
                image = ImageIO.read(new URL(filepath));
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            // Initialise the ImageView if one does not already exist
            if(imageView == null){
                imageView = new ImageView(new Image(filepath));
                imageView.setPreserveRatio(true);
                layout_main.setCenter(imageView);

                //Set up mouse events
                imageView.setOnMouseMoved(e -> {
                    updateMinimap((int) e.getX(), (int) e.getY());
                });
            }
            imageView.setImage(new Image(filepath));
            stage.setTitle(String.format("%s - %dx%d - rgrab",file.getAbsolutePath(),image.getWidth(),image.getHeight()));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void closeProgram(){
        System.out.println("Shutting down.");
        stage.close();
    }

    // Creates the program MenuBar
    public MenuBar createMenuBar(){

        MenuBar menuBar = new MenuBar();

        // Menu - File
        Menu menuFile = new Menu("File");
        MenuItem menuOpen = new MenuItem("Open");
        menuOpen.setOnAction(e -> selectFile());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(e -> closeProgram());
        menuFile.getItems().addAll(menuOpen,new SeparatorMenuItem(),menuExit);

        // Menu - View
        Menu menuView = new Menu("View");
        MenuItem menuEg1 = new MenuItem("Placeholder");
        MenuItem menuEg2 = new MenuItem("Placeholder");
        menuView.getItems().addAll(menuEg1,menuEg2);

        // Menu - Help
        Menu menuHelp = new Menu("Help");
        MenuItem menuViewHelp = new MenuItem("View Help");
        MenuItem menuAbout = new MenuItem("About");
        menuHelp.getItems().addAll(menuViewHelp,menuAbout);

        // Add Menus to MenuBar
        menuBar.getMenus().addAll(menuFile, menuView, menuHelp);

        // Bind width of Menu to Stage width
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        return menuBar;
    }

    public void createMinimap(){
        minimap = new Group();
        mapGrid = new Rectangle[9][9];
        Random rand = new Random();
        for(int i=0;i<mapGrid.length;i++){
            for(int j=0;j<mapGrid[i].length;j++){
                mapGrid[i][j] = new Rectangle(j*16,i*16,16,16);
                mapGrid[i][j].setFill(Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
                minimap.getChildren().add(mapGrid[i][j]);
            }
        }
        mapGrid[4][4].setStrokeWidth(2);
        mapGrid[4][4].setStroke(Color.YELLOW);
    }

    public void updateMinimap(int x, int y){

        for(int i=0;i<mapGrid.length;i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                int adjx = x + j - 4;
                int adjy = y + i - 4;
                if (adjx>=0 && adjy>=0 && adjx<image.getWidth() && adjy<image.getHeight()) {
                    java.awt.Color c = new java.awt.Color(image.getRGB(adjx,adjy));
                    mapGrid[i][j].setFill(Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
                }else{
                    mapGrid[i][j].setFill(Color.YELLOW);
                }
            }
        }
    }
}
