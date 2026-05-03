import java.time.*;
import java.time.temporal.ChronoUnit;
public class InvestmentAccount extends Account implements InterestBearing, Maintainable{
    private boolean isVIP;
    public InvestmentAccount(boolean isVIP){
        this.isVIP = isVIP;
    }

    @Override
    public void applyInterest(){
        double rate = 0.05;
        if(isVIP) rate += 0.01;
        balance += balance * rate;
        balance = Math.round(balance * 100.0) / 100.0;
    }
    @Override
    public void withdraw(double amount){
        throw new UnsupportedOperationException("Withdrawals are not allowed from Investment Accounts.");
    }
    public void transferToChequeing(Account chequeing, double amount)
        throws InvestmentLockException, InsufficientFundsException{
        
        if(ChronoUnit.DAYS.between(openingDate,LocalDate.now()) < 365){
            throw new InvestmentLockException("Cannot transfer from Investment account.");
        }

        if(balance < amount){
            throw new InsufficientFundsException("Insufficient funds.");
        }

        balance -= amount;
        chequeing.deposit(amount);
}
    @Override 
    public void applyMonthlyFee(Client client) throws InsufficientFundsException{
    	if(client.feeWaived()) {
    		return;
    	}
    	double fee = 10.0;
    	if(fee > balance) {
    		throw new InsufficientFundsException("Not enough to apply monthly fee");
    		
    	}
    	balance -= fee;
    }
    @Override
    public String toString() {
    	return " Investment Account: " + accountNumber + " | Balance: $" + balance;
    }
}
