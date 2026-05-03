public class IndividualClient extends StandardClient{
    public IndividualClient(String name, String password){
        super(name, password);
    }
    @Override
    public String toString() {
    	return "Individual Client: " + getName() + " | ID: " + getClientID();
    }
}