public class StudentClient extends StandardClient{
    public StudentClient(String name, String password){
        super(name, password);
    }
    @Override
    public boolean isVIP(){
        return false;
    }
    @Override
    public boolean feeWaived(){
        return true;
    }
    @Override
    public String toString() {
    	return "Student Client: " + getName() + " | ID: " + getClientID();
    }
}
