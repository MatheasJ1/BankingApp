import java.util.*;
import com.google.gson.*;
public abstract class Client{
    protected String clientID;
    protected String name;
    protected String password;
    protected List<Account> accounts;

    public Client(String name, String password){
        this.clientID = IDGenerator.generateClientID();
        this.name = name;
        this.password = password;
        this.accounts = new ArrayList<>();
    }
    public boolean hasChequingAccount() {
    	for(Account account : accounts) {
    		if (account instanceof ChequingAccount) {
    			return true;
    		}
    	}
    	return false;
    }
    public ChequingAccount getChequingAccount() {
    	for (Account account: accounts) {
    		if(account instanceof ChequingAccount) {
    			return (ChequingAccount) account;
    		}
    	}
    	return null;
    }
    public void addAccount(Account account){
        accounts.add(account);
    }
    public List<Account> getAccounts(){
        return accounts;
    }
    public String getClientID(){
        return clientID;
    }
    public String getName() {
    	return name;
    }
    public boolean login(String id, String password){
        return this.clientID.equals(id) && this.password.equals(password);
    }
    public abstract String getClientType();

    @Override
    public String toString(){
        return clientID + " - " + name + " (" + getClientType() + ")";
    }
    public void setClientID(String clientID) {
    	this.clientID = clientID;
    }
    public String getPassword() {
    	return password;
    }
    public abstract boolean isVIP();
    public abstract boolean feeWaived();
}