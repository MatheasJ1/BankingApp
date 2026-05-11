public class SavingsAccount extends Account implements InterestBearing, Maintainable{
    private boolean feeWaived;
    private boolean isVIP;

    public SavingsAccount(boolean feeWaived, boolean isVIP){
        this.feeWaived = feeWaived;
        this.isVIP = isVIP;
    }
   

    @Override
    public void applyInterest(){
        double rate = 0.02;
        if(isVIP) rate += 0.01;
        balance += balance * rate;
        balance = Math.round(balance * 100) / 100.0;
    }
    @Override
    public void applyMonthlyFee(Client client)throws InsufficientFundsException{
        if(client.feeWaived()){
        return;
    }
        double fee = 10.0;
        balance-=fee;
        if(fee> balance) {
        	throw new InsufficientFundsException("Insufficient funds.");
        }
}
    @Override
    public void withdraw(double amount) throws InsufficientFundsException{
    	if(amount > balance) {
    		throw new InsufficientFundsException("Insufficient funds.");
    	}
    	balance-=amount;
    }
    @Override
    public String toString() {
    	return "Savings Account: " + getAccountNumber() + " | Balance: $" + getBalance();
    }
}
