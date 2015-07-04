
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
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
import java.util.Random;

public class Main extends Application{

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
        stage.setOnCloseRequest(e -> closeProgram());

        // Create scene from layout
        layout_main = new BorderPane();
        layout_sub = new VBox(4);
        layout_sub.setStyle("-fx-padding:4px");
        layout_main.setRight(layout_sub);
        scene = new Scene(layout_main,640,480);

        // Create and add MenuBar to Group
        MenuBar menuBar = createMenuBar();
        layout_main.setTop(menuBar);

        //Create minimap with size and pixel square size
        createLabels();
        layout_sub.getChildren().addAll(l_coords, l_hex, l_rgb, t_hex, t_rgb);
        createMinimap(11, 16);

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

                imageView.setOnMouseClicked(e -> updateTextAreas((int) e.getX(), (int) e.getY()));
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
        menuOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        menuOpen.setOnAction(e -> selectFile());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        menuExit.setOnAction(e -> closeProgram());
        menuFile.getItems().addAll(menuOpen,new SeparatorMenuItem(),menuExit);

        // Menu - View
        Menu menuView = new Menu("View");
        MenuItem menuMiniSmall = new MenuItem("Minimap - Small");
        menuMiniSmall.setOnAction(e -> createMinimap(9,16));
        MenuItem menuMiniNormal = new MenuItem("Minimap - Normal");
        menuMiniNormal.setOnAction(e -> createMinimap(11,16));
        MenuItem menuMiniLarge = new MenuItem("Minimap - Large");
        menuMiniLarge.setOnAction(e -> createMinimap(13,16));
        menuView.getItems().addAll(menuMiniLarge,menuMiniNormal,menuMiniSmall);

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
        layout_sub.getChildren().remove(minimap);
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
        mapGrid[n / 2][n/2].setStroke(Color.YELLOW);
        mapGrid[n/2][n/2].toFront();
        layout_sub.getChildren().add(minimap);
        layout_sub.setPrefWidth(minimap.getBoundsInLocal().getWidth());
    }

    // Update the minimap with the pixels surrounding the mouse
    public void updateMinimap(int x, int y){
        for(int i=0;i<mapGrid.length;i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                int adjx = x + j - mapGrid.length / 2;
                int adjy = y + i - mapGrid.length / 2;
                // Color out-of-bounds pixels in yellow
                if (adjx >= 0 && adjy >= 0 && adjx < image.getWidth() && adjy < image.getHeight()) {
                    java.awt.Color color = new java.awt.Color(image.getRGB(adjx, adjy),true);
                    mapGrid[i][j].setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue(),((double)color.getAlpha()/255)));
                } else mapGrid[i][j].setFill(Color.TRANSPARENT);
            }
        }
    }

    //Create the labels that display attributes
    public void createLabels(){
        String font = "-fx-font-family: monospace; -fx-font-size: 9pt";

        l_coords = new Label("x,y:  (0,0)");
        l_hex = new Label("Hex:  #000000");
        l_rgb = new Label("RGBA: (0,0,0,0)");
        l_coords.setStyle(font);
        l_hex.setStyle(font);
        l_rgb.setStyle(font);

        // Hexadecimal TextArea
        t_hex = new TextArea("#000000");
        t_hex.setPrefRowCount(1);
        t_hex.setPrefColumnCount(7);
        t_hex.setEditable(false);
        t_hex.setStyle(font);
        t_hex.prefWidthProperty().bind(layout_sub.widthProperty());

        //RGB TextArea
        t_rgb = new TextArea("(0,0,0,0)");
        t_rgb.setPrefRowCount(1);
        t_hex.setPrefColumnCount(13);
        t_rgb.setEditable(false);
        t_rgb.setStyle(font);
        t_rgb.prefWidthProperty().bind(layout_sub.widthProperty());
    }

    // Update the labels with the relevant information
    public void updateLabels(int x, int y){
        java.awt.Color color = new java.awt.Color(image.getRGB(x,y),true);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        l_coords.setText(String.format("(x,y):  (%d,%d)",x,y));
        l_hex.setText(String.format("Hex: #%02X%02X%02X", red, green, blue));
        l_rgb.setText(String.format("RGBA: (%d,%d,%d,%d)",red,green,blue,alpha));
    }

    public void updateTextAreas(int x, int y){
        java.awt.Color color = new java.awt.Color(image.getRGB(x,y),true);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        t_hex.setText(String.format("#%02X%02X%02X",red,green,blue));
        t_rgb.setText(String.format("(%d,%d,%d,%d)",red,green,blue,alpha));
    }
}