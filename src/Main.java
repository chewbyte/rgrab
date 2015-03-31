
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class Main extends Application{

    Button button;
    Stage primary;
    Scene scene1, scene2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primary = primaryStage;
        primary.setTitle("rgrab");
        primary.setResizable(false);
        primary.setOnCloseRequest(e -> closeProgram());
        button = new Button("Add an X to the title");
        button.setOnAction(e -> {
            primary.setTitle(primary.getTitle()+" X");
        });

        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        scene1 = new Scene(layout,640,480);
        primary.setScene(scene1);
        primary.show();
    }

    public void closeProgram(){
        System.out.println("Shutting down.");
        primary.close();
    }
}
