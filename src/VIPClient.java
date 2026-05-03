public class VIPClient extends PremiumClient{
    public VIPClient(String name, String password){
        super(name, password);
    }
    @Override
    public boolean isVIP(){
        return true;
    }
    @Override
    public boolean feeWaived(){
        return true;
    }
    @Override
    public String toString() {
    	return "VIP Client: " + getName() + " | ID: " + getClientID();
    }
}