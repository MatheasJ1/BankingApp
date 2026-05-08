import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class RegisterController {
@FXML
private  TextField nameField;
@FXML
private  PasswordField passwordField;
@FXML
private  ComboBox<String> clientTypeBox;
@FXML
private  Label messageLabel;

private JsonService jsonService = new JsonService();
private List<Client> clients;

public void setClients(List<Client> clients) {
	this.clients = clients;
}
	@FXML
	public void initialize() {
		clientTypeBox.getItems().addAll("StudentClient", "IndividualClient", "CorporateClient", "VIPClient");
	}
	@FXML
	private void createAccount() {
		String name = nameField.getText().trim();
		String password = passwordField.getText();
		String type = clientTypeBox.getValue();
		if(name.isEmpty() || password.isEmpty() || type == null) {
			messageLabel.setText("Please complete all fields.");
			return;
		}
		Client newClient = createClient(type, name, password);
		if(newClient==null) {
			messageLabel.setText("Could not create client.");
			return;
					
		}
		clients.add(newClient);
		jsonService.saveClients(clients);
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Client created.");
		alert.setHeaderText("Account created successfully.");
		alert.setContentText("You client ID is: " + newClient.getClientID());
		alert.showAndWait();
		backToLogin();
		
		
	}
	@FXML
	private void backToLogin() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
			Scene scene = new Scene(loader.load(), 900, 600);
			Stage stage = (Stage) nameField.getScene().getWindow();
			stage.setScene(scene);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private Client createClient(String type, String name, String password) {
		switch(type) {
		case "StudentClient":
			return new StudentClient(name, password);
		case "IndividualClient":
			return new IndividualClient(name,password);
		case "CorporateClient":
			return new CorporateClient(name,password);
		case "VIPClient":
			return new VIPClient(name,password);
		default:
			return null;
		}
	}
}
