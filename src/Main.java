
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
import javafx.scene.text.Font;
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
    TextArea t_hex,t_rgb;

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
        layout_sub = new VBox(4);
        layout_sub.setStyle("-fx-background: #FC0;");
        layout_main.setRight(layout_sub);
        scene = new Scene(layout_main,640,480);

        // Create and add MenuBar to Group
        MenuBar menuBar = createMenuBar();
        layout_main.setTop(menuBar);

        //Create minimap with size and pixel square size
        createMinimap(11, 16);
        layout_sub.getChildren().add(minimap);
        layout_sub.setPrefWidth(minimap.getBoundsInLocal().getWidth());
        createLabels();
        layout_sub.getChildren().addAll(l_coords,l_hex,l_rgb,t_hex,t_rgb);

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

                imageView.setOnMouseClicked(e -> {
                    updateTextAreas((int) e.getX(), (int) e.getY());
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
        String font = "-fx-font-family: monospace; -fx-font-size: 12pt";

        l_coords = new Label("x,y: (0,0)");
        l_hex = new Label("Hex: #000000");
        l_rgb = new Label("RGB: (0,0,0)");
        l_coords.setStyle(font);
        l_hex.setStyle(font);
        l_rgb.setStyle(font);

        // Hexadecimal TextArea
        t_hex = new TextArea("#000000");
        t_hex.setPrefRowCount(0);
        t_hex.setPrefColumnCount(7);
        t_hex.setEditable(false);
        t_hex.setStyle(font);
        t_hex.prefWidthProperty().bind(layout_sub.widthProperty());

        //RGB TextArea
        t_rgb = new TextArea("(0,0,0)");
        t_rgb.setPrefRowCount(0);
        t_hex.setPrefColumnCount(13);
        t_rgb.setEditable(false);
        t_rgb.setStyle(font);
        t_rgb.prefWidthProperty().bind(layout_sub.widthProperty());
    }

    // Update the labels with the relevant information
    public void updateLabels(int x, int y){
        java.awt.Color c = new java.awt.Color(image.getRGB(x,y));
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        l_coords.setText(String.format("x,y: (%d,%d)",x,y));

        String temp_hex = String.format("#%02X%02X%02X",red,green,blue);
        l_hex.setText(String.format("Hex: %s",temp_hex));

        String temp_rgb = String.format("(%d,%d,%d)",red,green,blue);
        l_rgb.setText(String.format("RGB: %s",temp_rgb));
    }

    public void updateTextAreas(int x, int y){
        java.awt.Color c = new java.awt.Color(image.getRGB(x,y));
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        t_hex.setText(String.format("#%02X%02X%02X",red,green,blue));
        t_rgb.setText(String.format("(%d,%d,%d)",red,green,blue));
    }
}