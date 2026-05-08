import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class LoginController {
	@FXML
	private  TextField clientField;
	@FXML
	private  PasswordField passwordField;
	@FXML 
	private  Label errorLabel;
	
	
	private JsonService jsonService = new JsonService();
	private List<Client> clients;
	@FXML
	public void initialize() {
		clients = jsonService.loadClients();
	}
	@FXML
	private void login() {
		String id = clientField.getText().trim();
		String password = passwordField.getText();
		Client foundClient = findClientByLogin(id,password);
		if(foundClient == null) {
			errorLabel.setText("Invalid Client Id or Password.");
			return;
		}
		openDashboard(foundClient);
	}
	@FXML
	private void goToRegister() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
			Scene scene = new Scene(loader.load(), 900, 600);
			RegisterController controller = loader.getController();
			controller.setClients(clients);
			Stage stage = (Stage) clientField.getScene().getWindow();
			stage.setScene(scene);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		
	}
	private Client findClientByLogin(String id, String password) {
		for(Client client : clients) {
			if(client.login(id, password)) {
				return client;
			}
		}
		return null;
	}
	private void openDashboard(Client client) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.FXML"));
			Scene scene = new Scene(loader.load(), 900, 600);
			DashboardController controller = loader.getController();
			controller.setData(client, clients);
			Stage stage = (Stage) clientField.getScene().getWindow();
			stage.setScene(scene);
	} catch(Exception e) {
		e.printStackTrace();
	}
}
}
