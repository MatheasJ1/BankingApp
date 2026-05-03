public class PremiumClient extends Client{
    public PremiumClient(String name, String password){
        super(name, password);
    }

    @Override
    public String getClientType(){
        return "Premium";
    }

	@Override
	public boolean isVIP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean feeWaived() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String toString() {
		return "Premium Client : " + getName() + " | ID: " + getClientID();
	}
}
