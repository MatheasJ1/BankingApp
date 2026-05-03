import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private JsonService jsonService = new JsonService();
    private BankService bankService = new BankService();

    private List<Client> clients;
    private Client currentClient;

    private Stage mainStage;

    private TextArea accountArea;
    private Label messageLabel;
    private Label totalBalanceLabel;

    private final String BUTTON_STYLE = "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 14;";
    private final String GREEN_BUTTON = "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 14;";
    private final String RED_BUTTON = "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 14;";
    private final String INPUT_STYLE = "-fx-padding: 8; -fx-background-radius: 8; -fx-border-radius: 8;";

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        clients = jsonService.loadClients();

        mainStage.setTitle("E-Banking Application");

        mainStage.setOnCloseRequest(e -> {
            jsonService.saveClients(clients);
        });

        showLoginScreen();
        mainStage.show();
    }

    private void showLoginScreen() {
        Label title = new Label("E-Banking Login");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        TextField clientIdField = new TextField();
        clientIdField.setPromptText("Client ID");
        clientIdField.setStyle(INPUT_STYLE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(INPUT_STYLE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setStyle(BUTTON_STYLE);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Register New Client");
        registerButton.setStyle(GREEN_BUTTON);
        registerButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> {
            String id = clientIdField.getText().trim();
            String password = passwordField.getText();

            Client foundClient = findClientByLogin(id, password);

            if (foundClient == null) {
                errorLabel.setText("Invalid Client ID or password.");
                return;
            }

            currentClient = foundClient;
            showDashboard();
        });

        registerButton.setOnAction(e -> showRegisterScreen());

        VBox box = new VBox(12);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(400);

        box.getChildren().addAll(
                title,
                clientIdField,
                passwordField,
                loginButton,
                registerButton,
                errorLabel
        );

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: #f1f5f9;");

        Scene scene = new Scene(root, 800, 500);
        mainStage.setScene(scene);
    }

    private void showRegisterScreen() {
        Label title = new Label("Register New Client");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle(INPUT_STYLE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(INPUT_STYLE);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("StudentClient", "IndividualClient", "CorporateClient", "VIPClient");
        typeBox.setPromptText("Client Type");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label();

        Button createButton = new Button("Create Client");
        createButton.setStyle(GREEN_BUTTON);
        createButton.setMaxWidth(Double.MAX_VALUE);

        Button backButton = new Button("Back");
        backButton.setStyle(BUTTON_STYLE);
        backButton.setMaxWidth(Double.MAX_VALUE);

        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText();
            String type = typeBox.getValue();

            if (name.isEmpty() || password.isEmpty() || type == null) {
                message.setText("Please complete all fields.");
                return;
            }

            Client newClient = createClient(type, name, password);

            if (newClient == null) {
                message.setText("Could not create client.");
                return;
            }

            clients.add(newClient);
            jsonService.saveClients(clients);

            showInfo("Client Created", "Your Client ID is: " + newClient.getClientID());
            showLoginScreen();
        });

        backButton.setOnAction(e -> showLoginScreen());

        VBox box = new VBox(12);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(420);

        box.getChildren().addAll(
                title,
                nameField,
                passwordField,
                typeBox,
                createButton,
                backButton,
                message
        );

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: #f1f5f9;");

        Scene scene = new Scene(root, 800, 500);
        mainStage.setScene(scene);
    }

    private void showDashboard() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label clientInfo = new Label(
                currentClient.getName()
                        + " | ID: " + currentClient.getClientID()
                        + " | Type: " + currentClient.getClientType()
        );

        totalBalanceLabel = new Label();
        totalBalanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        VBox topBox = new VBox(6);
        topBox.getChildren().addAll(title, clientInfo, totalBalanceLabel);

        accountArea = new TextArea();
        accountArea.setEditable(false);
        accountArea.setPrefHeight(300);
        accountArea.setStyle("-fx-font-size: 14px;");

        messageLabel = new Label("Welcome.");
        messageLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");

        Button openChequingButton = new Button("Open Chequing");
        Button openSavingsButton = new Button("Open Savings");
        Button openInvestmentButton = new Button("Open Investment");

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button transferButton = new Button("Transfer");
        Button investmentTransferButton = new Button("Investment → Chequing");

        Button interestButton = new Button("Apply Interest");
        Button feesButton = new Button("Apply Monthly Fees");
        Button refreshButton = new Button("Refresh");
        Button logoutButton = new Button("Logout");

        Button[] buttons = {
                openChequingButton, openSavingsButton, openInvestmentButton,
                depositButton, withdrawButton, transferButton, investmentTransferButton,
                interestButton, feesButton, refreshButton
        };

        for (Button b : buttons) {
            b.setStyle(BUTTON_STYLE);
        }

        logoutButton.setStyle(RED_BUTTON);

        openChequingButton.setOnAction(e -> {
            bankService.openChequingAccount(currentClient);
            saveAndRefresh("Chequing account opened.");
        });

        openSavingsButton.setOnAction(e -> {
            try {
                bankService.openSavingsAccount(currentClient);
                saveAndRefresh("Savings account opened.");
            } catch (MissingChequingException ex) {
                showError("Error", ex.getMessage());
            }
        });

        openInvestmentButton.setOnAction(e -> {
            try {
                bankService.openInvestmentAccount(currentClient);
                saveAndRefresh("Investment account opened.");
            } catch (MissingChequingException ex) {
                showError("Error", ex.getMessage());
            }
        });

        depositButton.setOnAction(e -> showDepositWindow());

        withdrawButton.setOnAction(e -> showWithdrawWindow());

        transferButton.setOnAction(e -> showTransferWindow());

        investmentTransferButton.setOnAction(e -> showInvestmentTransferWindow());

        interestButton.setOnAction(e -> {
            bankService.applyInterestToAllAccount(currentClient);
            saveAndRefresh("Interest applied.");
        });

        feesButton.setOnAction(e -> {
            try {
                bankService.applyMonthlyFeeToAllAccounts(currentClient);
                saveAndRefresh("Monthly fees applied.");
            } catch (InsufficientFundsException ex) {
                showError("Error", ex.getMessage());
            }
        });

        refreshButton.setOnAction(e -> refreshDashboard());

        logoutButton.setOnAction(e -> {
            jsonService.saveClients(clients);
            currentClient = null;
            showLoginScreen();
        });

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);

        buttonGrid.add(openChequingButton, 0, 0);
        buttonGrid.add(openSavingsButton, 1, 0);
        buttonGrid.add(openInvestmentButton, 2, 0);

        buttonGrid.add(depositButton, 0, 1);
        buttonGrid.add(withdrawButton, 1, 1);
        buttonGrid.add(transferButton, 2, 1);

        buttonGrid.add(investmentTransferButton, 0, 2);
        buttonGrid.add(interestButton, 1, 2);
        buttonGrid.add(feesButton, 2, 2);

        buttonGrid.add(refreshButton, 0, 3);
        buttonGrid.add(logoutButton, 1, 3);

        VBox centerBox = new VBox(15);
        centerBox.getChildren().addAll(accountArea, buttonGrid, messageLabel);

        root.setTop(topBox);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 600);
        mainStage.setScene(scene);

        refreshDashboard();
    }

    private void showDepositWindow() {
        Stage window = createPopup("Deposit");

        ComboBox<Account> accountBox = createAccountBox();
        TextField amountField = createAmountField();
        Label msg = createPopupMessage();

        Button depositButton = new Button("Deposit");
        depositButton.setStyle(GREEN_BUTTON);
        depositButton.setMaxWidth(Double.MAX_VALUE);

        depositButton.setOnAction(e -> {
            try {
                Account account = accountBox.getValue();
                double amount = parseAmount(amountField.getText());

                if (account == null) {
                    msg.setText("Choose an account.");
                    return;
                }

                bankService.deposit(account, amount);
                saveAndRefresh("Deposit successful.");
                window.close();

            } catch (NumberFormatException ex) {
                msg.setText("Enter a valid positive amount.");
            }
        });

        VBox root = popupLayout("Deposit Money", accountBox, amountField, depositButton, msg);
        window.setScene(new Scene(root, 380, 270));
        window.show();
    }

    private void showWithdrawWindow() {
        Stage window = createPopup("Withdraw");

        ComboBox<Account> accountBox = createAccountBox();
        TextField amountField = createAmountField();
        Label msg = createPopupMessage();

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setStyle(BUTTON_STYLE);
        withdrawButton.setMaxWidth(Double.MAX_VALUE);

        withdrawButton.setOnAction(e -> {
            try {
                Account account = accountBox.getValue();
                double amount = parseAmount(amountField.getText());

                if (account == null) {
                    msg.setText("Choose an account.");
                    return;
                }

                bankService.withdraw(account, amount);
                saveAndRefresh("Withdrawal successful.");
                window.close();

            } catch (NumberFormatException ex) {
                msg.setText("Enter a valid positive amount.");
            } catch (InsufficientFundsException ex) {
                msg.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                msg.setText(ex.getMessage());
            }
        });

        VBox root = popupLayout("Withdraw Money", accountBox, amountField, withdrawButton, msg);
        window.setScene(new Scene(root, 380, 270));
        window.show();
    }

    private void showTransferWindow() {
        Stage window = createPopup("Transfer");

        ComboBox<Account> fromBox = createAccountBox();
        ComboBox<Account> toBox = createAccountBox();
        TextField amountField = createAmountField();
        Label msg = createPopupMessage();

        Button transferButton = new Button("Transfer");
        transferButton.setStyle(BUTTON_STYLE);
        transferButton.setMaxWidth(Double.MAX_VALUE);

        transferButton.setOnAction(e -> {
            try {
                Account from = fromBox.getValue();
                Account to = toBox.getValue();
                double amount = parseAmount(amountField.getText());

                if (from == null || to == null) {
                    msg.setText("Choose both accounts.");
                    return;
                }

                if (from == to) {
                    msg.setText("Cannot transfer to the same account.");
                    return;
                }

                if (from instanceof InvestmentAccount) {
                    msg.setText("Use Investment → Chequing for investment transfers.");
                    return;
                }

                bankService.transfer(from, to, amount);
                saveAndRefresh("Transfer successful.");
                window.close();

            } catch (NumberFormatException ex) {
                msg.setText("Enter a valid positive amount.");
            } catch (InsufficientFundsException ex) {
                msg.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                msg.setText(ex.getMessage());
            }
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label title = new Label("Transfer Money");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        root.getChildren().addAll(
                title,
                new Label("From Account"),
                fromBox,
                new Label("To Account"),
                toBox,
                amountField,
                transferButton,
                msg
        );

        window.setScene(new Scene(root, 400, 390));
        window.show();
    }

    private void showInvestmentTransferWindow() {
        Stage window = createPopup("Investment to Chequing");

        ComboBox<Account> investmentBox = new ComboBox<>();

        for (Account account : currentClient.getAccounts()) {
            if (account instanceof InvestmentAccount) {
                investmentBox.getItems().add(account);
            }
        }

        investmentBox.setPromptText("Choose investment account");
        investmentBox.setMaxWidth(Double.MAX_VALUE);

        TextField amountField = createAmountField();
        Label msg = createPopupMessage();

        Button transferButton = new Button("Transfer to Chequing");
        transferButton.setStyle(BUTTON_STYLE);
        transferButton.setMaxWidth(Double.MAX_VALUE);

        transferButton.setOnAction(e -> {
            try {
                Account selected = investmentBox.getValue();
                double amount = parseAmount(amountField.getText());

                if (selected == null) {
                    msg.setText("Choose an investment account.");
                    return;
                }

                bankService.transferInvestmentToChequing(
                        currentClient,
                        (InvestmentAccount) selected,
                        amount
                );

                saveAndRefresh("Investment transfer successful.");
                window.close();

            } catch (NumberFormatException ex) {
                msg.setText("Enter a valid positive amount.");
            } catch (MissingChequingException | InvestmentLockException | InsufficientFundsException ex) {
                msg.setText(ex.getMessage());
            }
        });

        VBox root = popupLayout("Investment Transfer", investmentBox, amountField, transferButton, msg);
        window.setScene(new Scene(root, 400, 280));
        window.show();
    }

    
    private ComboBox<Account> createAccountBox() {
        ComboBox<Account> box = new ComboBox<>();
        box.getItems().addAll(currentClient.getAccounts());
        box.setPromptText("Choose account");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private TextField createAmountField() {
        TextField field = new TextField();
        field.setPromptText("Amount");
        field.setStyle(INPUT_STYLE);
        return field;
    }

    private Label createPopupMessage() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red;");
        return label;
    }

    private VBox popupLayout(String titleText, ComboBox<Account> accountBox, TextField amountField, Button button, Label msg) {
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(title, accountBox, amountField, button, msg);

        return root;
    }

    private Stage createPopup(String title) {
        Stage window = new Stage();
        window.setTitle(title);
        window.initOwner(mainStage);
        window.initModality(Modality.APPLICATION_MODAL);
        return window;
    }

    private void refreshDashboard() {
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

    private void saveAndRefresh(String message) {
        jsonService.saveClients(clients);
        refreshDashboard();
        messageLabel.setText(message);
    }

    private Client findClientByLogin(String id, String password) {
        for (Client client : clients) {
            if (client.login(id, password)) {
                return client;
            }
        }

        return null;
    }

    private Client createClient(String type, String name, String password) {
        switch (type) {
            case "StudentClient":
                return new StudentClient(name, password);

            case "IndividualClient":
                return new IndividualClient(name, password);

            case "CorporateClient":
                return new CorporateClient(name, password);

            case "VIPClient":
                return new VIPClient(name, password);

            default:
                return null;
        }
    }

    private double parseAmount(String amountText) {
        double amount = Double.parseDouble(amountText);

        if (amount <= 0) {
            throw new NumberFormatException();
        }

        return amount;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}