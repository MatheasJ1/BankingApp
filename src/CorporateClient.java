public class CorporateClient extends PremiumClient{
    public CorporateClient(String name, String password){
        super(name, password);
    }
    public boolean isVIP(){
        return false;
    }
    public boolean feeWaived(){
        return false;
    }
    @Override
    public String toString() {
    	return "Corporate Client: " + getName() + " | ID: " + getClientID();
    }
}
