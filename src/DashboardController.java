import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class DashboardController {

    @FXML
    private Label clientInfoLabel;

    @FXML
    private Label totalBalanceLabel;

    @FXML
    private TextArea accountArea;

    @FXML
    private Label messageLabel;

    private Client currentClient;
    private List<Client> clients;

    private JsonService jsonService = new JsonService();
    private BankService bankService = new BankService();

    public void setData(Client currentClient, List<Client> clients) {
        this.currentClient = currentClient;
        this.clients = clients;

        clientInfoLabel.setText(
                currentClient.getName()
                        + " | ID: " + currentClient.getClientID()
                        + " | Type: " + currentClient.getClientType()
        );

        refresh();
    }

    @FXML
    private void openChequing() {
        bankService.openChequingAccount(currentClient);
        saveAndRefresh("Chequing account opened.");
    }

    @FXML
    private void openSavings() {
        try {
            bankService.openSavingsAccount(currentClient);
            saveAndRefresh("Savings account opened.");
        } catch (MissingChequingException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void openInvestment() {
        try {
            bankService.openInvestmentAccount(currentClient);
            saveAndRefresh("Investment account opened.");
        } catch (MissingChequingException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void deposit() {
        openPopup("Deposit.fxml", "Deposit Money");
    }

    @FXML
    private void withdraw() {
        openPopup("Withdraw.fxml", "Withdraw Money");
    }

    @FXML
    private void transfer() {
        openPopup("Transfer.fxml", "Transfer Money");
    }

    @FXML
    private void investmentTransfer() {
        openPopup("InvestmentTransfer.fxml", "Investment Transfer");
    }

    @FXML
    private void applyInterest() {
        bankService.applyInterestToAllAccount(currentClient);
        saveAndRefresh("Interest applied.");
    }

    @FXML
    private void applyMonthlyFees() {
        try {
            bankService.applyMonthlyFeeToAllAccounts(currentClient);
            saveAndRefresh("Monthly fees applied.");
        } catch (InsufficientFundsException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void refresh() {
        if (currentClient == null) {
            return;
        }

        String text = "";

        for (Account account : currentClient.getAccounts()) {
            text += account.toString() + "\n";
        }

        if (text.isEmpty()) {
            text = "No accounts yet. Open a chequing account first.";
        }

        accountArea.setText(text);

        double total = bankService.getTotalBalance(currentClient);
        totalBalanceLabel.setText("Total Balance: $" + String.format("%.2f", total));
    }

    @FXML
    private void logout() {
        try {
            jsonService.saveClients(clients);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            Stage stage = (Stage) accountArea.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPopup(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Object controller = loader.getController();

            if (controller instanceof DepositController) {
                ((DepositController) controller).setData(currentClient, clients, this);
            }

            if (controller instanceof WithdrawController) {
                ((WithdrawController) controller).setData(currentClient, clients, this);
            }

            if (controller instanceof TransferController) {
                ((TransferController) controller).setData(currentClient, clients, this);
            }

            if (controller instanceof InvestmentTransferController) {
                ((InvestmentTransferController) controller).setData(currentClient, clients, this);
            }

            Stage popup = new Stage();
            popup.setTitle(title);
            popup.setScene(scene);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(accountArea.getScene().getWindow());
            popup.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAndRefresh(String message) {
        jsonService.saveClients(clients);
        refresh();
        messageLabel.setText(message);
    }

    public void refreshFromPopup(String message) {
        saveAndRefresh(message);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.showAndWait();
    }
}
