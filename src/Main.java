
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class Main extends Application {

    Button button;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("rgrab");
        button = new Button("rgrab");

        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        Scene scene = new Scene(layout,640,480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
