import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class TransferController {

    @FXML
    private ComboBox<Account> fromBox;

    @FXML
    private ComboBox<Account> toBox;

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

        fromBox.getItems().addAll(currentClient.getAccounts());
        toBox.getItems().addAll(currentClient.getAccounts());
    }

    @FXML
    private void transfer() {
        try {
            Account from = fromBox.getValue();
            Account to = toBox.getValue();
            double amount = Double.parseDouble(amountField.getText());

            if (from == null || to == null) {
                messageLabel.setText("Choose both accounts.");
                return;
            }

            if (from == to) {
                messageLabel.setText("Cannot transfer to the same account.");
                return;
            }

            if (from instanceof InvestmentAccount) {
                messageLabel.setText("Use Investment → Chequing for investment transfers.");
                return;
            }

            if (amount <= 0) {
                messageLabel.setText("Amount must be positive.");
                return;
            }

            bankService.transfer(from, to, amount);
            jsonService.saveClients(clients);

            dashboardController.refreshFromPopup("Transfer successful.");

            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            messageLabel.setText("Enter a valid positive amount.");
        } catch (InsufficientFundsException e) {
            messageLabel.setText(e.getMessage());
        } catch (RuntimeException e) {
            messageLabel.setText(e.getMessage());
        }
    }
}