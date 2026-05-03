public class StandardClient extends Client{
    public StandardClient(String name, String password){
        super(name, password);
    }

    @Override
    public String getClientType(){
        return "Standard";
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
		return "Standard Client: " + getName() + " | ID: " + getClientID();
	}
    
}
