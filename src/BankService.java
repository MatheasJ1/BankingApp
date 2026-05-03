import java.util.*;
public class BankService {
	public void openChequingAccount(Client client) {
		if(client.hasChequingAccount()) {
			System.out.println("Client already has a chequing account.");
			return;
		}
		ChequingAccount chequing = new ChequingAccount();
		client.addAccount(chequing);
	}
	public void openSavingsAccount(Client client) throws MissingChequingException{
		if(!client.hasChequingAccount()) {
			throw new MissingChequingException("You must open a chequing account before a savings account.");
		}
		SavingsAccount savings = new SavingsAccount(client.feeWaived(), client.isVIP());
		client.addAccount(savings);
	}
	public void openInvestmentAccount(Client client) throws MissingChequingException{
		if(!client.hasChequingAccount()) {
			throw new MissingChequingException("You must open a chequing account before an investment account.");
		}
		InvestmentAccount investment = new InvestmentAccount(client.isVIP());
		client.addAccount(investment);
	}
	public void deposit(Account account, double amount) {
		if (amount <=0) {
			System.out.println("Deposit must be greater than 0.");
			return;
		}
		account.deposit(amount, "Deposit successful.");
	}
	public void withdraw(Account account, double amount) throws InsufficientFundsException{
		if(amount<=0) {
			System.out.println("Withdrawal amount must be greater than 0.");
			return;
		}
		account.withdraw(amount);
	}
	public void transfer(Account fromAccount, Account toAccount, double amount) throws InsufficientFundsException{
		if (amount<=0) {
			throw new InsufficientFundsException("Transfer amount must be greater than 0.");
			
		}
		fromAccount.withdraw(amount);
		toAccount.deposit(amount, "Transfer received.");
	}
	public void transferInvestmentToChequing(Client client, InvestmentAccount investment, double amount) throws MissingChequingException, InvestmentLockException, InsufficientFundsException {
		if(!client.hasChequingAccount()) {
			throw new MissingChequingException("Client does not have a chequing account to receive the investment transfer.");
		}
		if(amount<=0) {
			System.out.println("Transfer amount must be greater than 0.");
			return;
		}
		ChequingAccount chequing = client.getChequingAccount();
		investment.transferToChequeing(chequing, amount);
	}
	public void applyInterestToAllAccount(Client client) {
		for(Account account : client.getAccounts()) {
			if(account instanceof InterestBearing) {
				InterestBearing interestAccount = (InterestBearing) account;
				interestAccount.applyInterest();
			}
		}
	}
	public void applyMonthlyFeeToAllAccounts(Client client) throws InsufficientFundsException{
		for(Account account: client.getAccounts()) {
			if(account instanceof Maintainable) {
				Maintainable maintainableAccount = (Maintainable) account;
				maintainableAccount.applyMonthlyFee(client);
			}
		}
	}
	public double getTotalBalance(Client client) {
		double total = 0.0;
		for(Account account: client.getAccounts()) {
			total += account.getBalance();
		}
		return total;
	}
	public Account findAccountByNumber(Client client, String accountNumber) {
		for(Account account : client.getAccounts()) {
			if (account.getAccountNumber().equals(accountNumber)) {
				return account;
			}
		}
		return null;
	}
	public void displayAccounts(Client client) {
		List<Account> accounts = client.getAccounts();
		if(accounts.isEmpty()) {
			System.out.println("No accounts to display.");
			return;
		}
		for (Account account: accounts) {
			System.out.println(account);
		}
	}
}
