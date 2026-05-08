import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application{
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader =new FXMLLoader(getClass().getResource("sample.fxml"));
			
			Scene scene = new Scene(loader.load(), 900, 600);
			stage.setTitle("E-Banking Application");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not load fxml file.");
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
