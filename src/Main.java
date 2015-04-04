
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
import javafx.scene.shape.StrokeType;
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
    Label l_coords,l_rgb,l_hex;

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

        //Create minimap with size and pixel square size
        createMinimap(13,12);
        layout_sub.getChildren().add(minimap);
        createLabels();
        layout_sub.getChildren().addAll(l_coords,l_hex,l_rgb);

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
        if(file != null) {
            openFile(file);
        }
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
                    updateLabels((int) e.getX(), (int) e.getY());
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

    // Create the minimap
    public void createMinimap(int n, int s){
        minimap = new Group();
        mapGrid = new Rectangle[n][n];
        Random rand = new Random();
        for(int i=0;i<mapGrid.length;i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                mapGrid[i][j] = new Rectangle(j * s, i * s, s, s);
                mapGrid[i][j].setFill(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                mapGrid[i][j].setStroke(Color.GRAY);
                minimap.getChildren().add(mapGrid[i][j]);
            }
        }
        mapGrid[n/2][n/2].setStroke(Color.YELLOW);
        mapGrid[n/2][n/2].toFront();
    }

    // Update the minimap with the pixels surrounding the mouse
    public void updateMinimap(int x, int y){
        for(int i=0;i<mapGrid.length;i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                int adjx = x + j - mapGrid.length / 2;
                int adjy = y + i - mapGrid.length / 2;
                // Color out-of-bounds pixels in yellow
                if (adjx >= 0 && adjy >= 0 && adjx < image.getWidth() && adjy < image.getHeight()) {
                    java.awt.Color c = new java.awt.Color(image.getRGB(adjx, adjy));
                    mapGrid[i][j].setFill(Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
                } else mapGrid[i][j].setFill(Color.YELLOW);
            }
        }
    }

    //Create the labels that display attributes
    public void createLabels(){
        l_coords = new Label("Location:\t(0,0)");
        l_hex = new Label("Hex:\t\t#000000");
        l_rgb = new Label("RGB:\t\trgb(0,0,0)");
    }

    // Update the labels with the relevant information
    public void updateLabels(int x, int y){
        java.awt.Color c = new java.awt.Color(image.getRGB(x,y));
        l_coords.setText(String.format("Location:\t(%d,%d)",x,y));
        l_hex.setText(String.format("Hex:\t\t#%02X%02X%02X",c.getRed(),c.getGreen(),c.getBlue()));
        l_rgb.setText(String.format("RGB:\t\trgb(%d,%d,%d)",c.getRed(),c.getGreen(),c.getBlue()));
    }
}