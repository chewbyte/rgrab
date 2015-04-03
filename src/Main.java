
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application{

    Button button;

    Stage stage;
    Scene scene;
    Group rootGroup;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("rgrab");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> closeProgram());

        rootGroup = new Group();
        scene = new Scene(rootGroup,640,480,Color.ALICEBLUE);

        // Create and add MenuBar to Group
        MenuBar menuBar = createMenuBar();
        rootGroup.getChildren().addAll(menuBar);

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
            // Placeholder display test
            String filepath = "file:///"+file.getAbsolutePath();
            BufferedImage image = createBufferedImage(filepath);
            ImageView imageView = new ImageView(new Image(filepath));
            imageView.setX(32);
            imageView.setY(32);
            rootGroup.getChildren().add(imageView);
            System.out.println(image.getRGB(2,2));
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

    // Creates a BufferedImage object from a file path
    public BufferedImage createBufferedImage(String path){
        BufferedImage b = null;
        try {
            b = ImageIO.read(new URL(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
}
