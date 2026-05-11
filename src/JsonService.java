
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonService{
	private static final Path DATA_FOLDER = Paths.get("data");
	private static final Path CLIENTS_FILE = DATA_FOLDER.resolve("clients.json");
	private Gson gson;
	
	public JsonService() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public void saveClients(List<Client> clients) {
	    JsonArray clientArray = new JsonArray();

	    for (Client client : clients) {
	        JsonObject clientObject = clientToJson(client);
	        clientArray.add(clientObject);
	    }

	    try {
	        Files.createDirectories(DATA_FOLDER);

	        System.out.println("Saving to: " + CLIENTS_FILE.toAbsolutePath());
	        System.out.println("Number of clients being saved: " + clients.size());
	        System.out.println("JSON content being saved:");
	        System.out.println(gson.toJson(clientArray));

	        try (FileWriter writer = new FileWriter(CLIENTS_FILE.toFile())) {
	            gson.toJson(clientArray, writer);
	            writer.flush();
	            System.out.println("Clients saved successfully.");
	        }

	    } catch (IOException e) {
	        System.out.println("Error while saving clients: " + e.getMessage());
	    }
	}
	
	public List<Client> loadClients(){
		List<Client> clients = new ArrayList<>();
		
		try(FileReader reader = new FileReader(CLIENTS_FILE.toFile())){
			JsonArray clientArray = gson.fromJson(reader, JsonArray.class);
			if(clientArray == null) {
				return clients;
			}
			 for (JsonElement element : clientArray) {
	                JsonObject clientObject = element.getAsJsonObject();
	                Client client = jsonToClient(clientObject);

	                if (client != null) {
	                    clients.add(client);
	                }
	            }

	            System.out.println("Clients loaded successfully.");

	        } catch (IOException e) {
	            System.out.println("No previous JSON file found. Starting with empty client list.");
	        }

	        return clients;
	    }

	    private JsonObject clientToJson(Client client) {
	        JsonObject obj = new JsonObject();

	        obj.addProperty("type", client.getClass().getSimpleName());
	        obj.addProperty("clientID", client.getClientID());
	        obj.addProperty("name", client.getName());
	        obj.addProperty("password", client.getPassword());

	        JsonArray accountArray = new JsonArray();

	        for (Account account : client.getAccounts()) {
	            accountArray.add(accountToJson(account));
	        }

	        obj.add("accounts", accountArray);

	        return obj;
	    }

	    private JsonObject accountToJson(Account account) {
	        JsonObject obj = new JsonObject();

	        obj.addProperty("type", account.getClass().getSimpleName());
	        obj.addProperty("accountNumber", account.getAccountNumber());
	        obj.addProperty("balance", account.getBalance());
	        obj.addProperty("openingDate", account.getOpeningDate().toString());

	        return obj;
	    }

	    private Client jsonToClient(JsonObject obj) {
	        String type = obj.get("type").getAsString();
	        String clientID = obj.get("clientID").getAsString();
	        String name = obj.get("name").getAsString();
	        String password = obj.get("password").getAsString();

	        Client client;

	        switch (type) {
	            case "StudentClient":
	                client = new StudentClient(name, password);
	                break;

	            case "IndividualClient":
	                client = new IndividualClient(name, password);
	                break;

	            case "CorporateClient":
	                client = new CorporateClient(name, password);
	                break;

	            case "VIPClient":
	                client = new VIPClient(name, password);
	                break;

	            default:
	                return null;
	        }

	        client.setClientID(clientID);

	        JsonArray accounts = obj.getAsJsonArray("accounts");

	        if (accounts != null) {
	            for (JsonElement element : accounts) {
	                JsonObject accountObject = element.getAsJsonObject();
	                Account account = jsonToAccount(accountObject, client);

	                if (account != null) {
	                    client.addAccount(account);
	                }
	            }
	        }

	        return client;
	    }

	    private Account jsonToAccount(JsonObject obj, Client client) {
	        String type = obj.get("type").getAsString();
	        String accountNumber = obj.get("accountNumber").getAsString();
	        double balance = obj.get("balance").getAsDouble();
	        LocalDate openingDate = LocalDate.parse(obj.get("openingDate").getAsString());

	        Account account;

	        switch (type) {
	            case "ChequingAccount":
	                account = new ChequingAccount();
	                break;

	            case "SavingsAccount":
	                account = new SavingsAccount(client.feeWaived(), client.isVIP());
	                break;

	            case "InvestmentAccount":
	                account = new InvestmentAccount(client.isVIP());
	                break;

	            default:
	                return null;
	        }

	        account.setAccountNumber(accountNumber);
	        account.setBalance(balance);
	        account.setOpeningDate(openingDate);

	        return account;
	    }
		}
	