
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        primary.setOnCloseRequest(e -> closeProgram());
        button = new Button("rgrab");
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
