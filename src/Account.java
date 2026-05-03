

	import java.time.*;
	public abstract class Account{
	    protected String accountNumber;
	    protected double balance;
	    protected LocalDate openingDate;

	    public Account(){
	        this.accountNumber = IDGenerator.generateAccountID();
	        this.balance = 0.0;
	        this.openingDate = LocalDate.now();
	    }
	    public String getAccountNumber() {
	    	return accountNumber;
	    }
	    public double getBalance(){
	        return balance;
	    }
	    public LocalDate getOpeningDate() {
	    	return openingDate;
	    }

	    public void deposit(double amount){
	        balance+=amount;
	    }
	    public void deposit(double amount, String note){
	        balance+=amount;
	        System.out.println(note);
	    }

	    public void withdraw(double amount) throws InsufficientFundsException{
	        if(amount > balance){
	            throw new InsufficientFundsException("Insufficient funds.");
	        }
	        balance-=amount;
	    }
	    @Override
	    public String toString() {
	    	return accountNumber + " | Balance: $ " + String.format("%.2f",  getBalance());
	    }
	    public void setAccountNumber(String accountNumber) {
	    	this.accountNumber = accountNumber;
	    }
	    public void setBalance(double balance) {
	    	this.balance = balance;
	    }
	    public void setOpeningDate(LocalDate openingDate) {
	    	this.openingDate = openingDate;
	    }
	    
	}

