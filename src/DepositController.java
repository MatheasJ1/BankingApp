import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class DepositController {

    @FXML
    private ComboBox<Account> accountBox;

    @FXML
    private TextField amountField;

    @FXML
    private Label messageLabel;

    private Client currentClient;
    private List<Client> clients;
    private DashboardController dashboardController;

    private BankService bankService = new BankService();
    private JsonService jsonService = new JsonService();

    public void setData(Client currentClient, List<Client> clients, DashboardController dashboardController) {
        this.currentClient = currentClient;
        this.clients = clients;
        this.dashboardController = dashboardController;

        accountBox.getItems().addAll(currentClient.getAccounts());
    }

    @FXML
    private void deposit() {
        try {
            Account account = accountBox.getValue();
            double amount = Double.parseDouble(amountField.getText());

            if (account == null) {
                messageLabel.setText("Choose an account.");
                return;
            }

            if (amount <= 0) {
                messageLabel.setText("Amount must be positive.");
                return;
            }

            bankService.deposit(account, amount);
            jsonService.saveClients(clients);

            dashboardController.refreshFromPopup("Deposit successful.");

            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            messageLabel.setText("Enter a valid positive amount.");
        }
    }
}
