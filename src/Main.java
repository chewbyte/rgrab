
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;

public class Main extends Application{

    Button button;

    Stage stage;
    Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("rgrab");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> closeProgram());

        Group rootGroup = new Group();
        scene = new Scene(rootGroup,640,480,Color.ALICEBLUE);

        button = new Button("Add an X to the title");
        button.setOnAction(e -> {
            stage.setTitle(stage.getTitle()+" X");
        });

        //Set up menu bar
        MenuBar menuBar = new MenuBar();

        // Menu - File
        Menu menuFile = new Menu("File");
        MenuItem menuOpen = new MenuItem("Open");
        MenuItem menuExit = new MenuItem("Exit");
        menuFile.getItems().addAll(menuOpen,menuExit);

        // Menu - Help
        Menu menuHelp = new Menu("Help");
        MenuItem menuViewHelp = new MenuItem("View Help");
        MenuItem menuAbout = new MenuItem("About");
        menuHelp.getItems().addAll(menuViewHelp,menuAbout);

        // Add Menus to MenuBar
        menuBar.getMenus().addAll(menuFile, menuHelp);

        // Bind width of Menu to Stage width
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        // Add MenuBar to Group
        rootGroup.getChildren().addAll(menuBar);
        
        stage.setScene(scene);
        stage.show();
    }

    public void closeProgram(){
        System.out.println("Shutting down.");
        stage.close();
    }
}
