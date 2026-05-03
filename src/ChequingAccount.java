public class ChequingAccount extends Account implements Maintainable{

    private boolean feeWaived;

    public ChequingAccount() {
        
    }
    @Override
    public void applyMonthlyFee(Client client) throws InsufficientFundsException{
        if(client.feeWaived()){
        return;
    }
        double fee = 10.0;
        
        if (fee > balance) {
        	throw new InsufficientFundsException("Insufficient funds.");
        }
        balance -= fee;
}
    @Override
    public void withdraw(double amount) throws InsufficientFundsException{
    	if(amount > balance) {
    		throw new InsufficientFundsException("Insufficient funds.");
    	}
    	balance -= amount;
    }
    @Override
    public String toString() {
    	return "Chequing Account: " + getAccountNumber() + " | Balance: $" + getBalance();
    }
}
