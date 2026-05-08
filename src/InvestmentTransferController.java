import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class InvestmentTransferController {

    @FXML
    private ComboBox<Account> investmentBox;

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

        for (Account account : currentClient.getAccounts()) {
            if (account instanceof InvestmentAccount) {
                investmentBox.getItems().add(account);
            }
        }
    }

    @FXML
    private void transferToChequing() {
        try {
            Account selected = investmentBox.getValue();
            double amount = Double.parseDouble(amountField.getText());

            if (selected == null) {
                messageLabel.setText("Choose an investment account.");
                return;
            }

            if (amount <= 0) {
                messageLabel.setText("Amount must be positive.");
                return;
            }

            bankService.transferInvestmentToChequing(
                    currentClient,
                    (InvestmentAccount) selected,
                    amount
            );

            jsonService.saveClients(clients);

            dashboardController.refreshFromPopup("Investment transfer successful.");

            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            messageLabel.setText("Enter a valid positive amount.");
        } catch (MissingChequingException e) {
            messageLabel.setText(e.getMessage());
        } catch (InvestmentLockException e) {
            messageLabel.setText(e.getMessage());
        } catch (InsufficientFundsException e) {
            messageLabel.setText(e.getMessage());
        }
    }
}
