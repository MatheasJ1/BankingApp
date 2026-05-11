import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankingTest {

    private BankService bankService;

    private IndividualClient individualClient;
    private StudentClient studentClient;
    private VIPClient vipClient;
    private CorporateClient corporateClient;

    @BeforeEach
    void setUp() {
        bankService = new BankService();

        individualClient = new IndividualClient("Matheas", "1234");
        studentClient = new StudentClient("Ethan", "1111");
        vipClient = new VIPClient("Emanuel", "2222");
        corporateClient = new CorporateClient("Company", "3333");
    }

    // =========================================================
    // CLIENT TESTS
    // =========================================================

    @Test
    void newClientShouldStartWithNoAccounts() {
        assertEquals(0, individualClient.getAccounts().size());
    }

    @Test
    void clientLoginShouldWorkWithCorrectIdAndPassword() {
        assertTrue(individualClient.login(individualClient.getClientID(), "1234"));
    }

    @Test
    void clientLoginShouldFailWithWrongPassword() {
        assertFalse(individualClient.login(individualClient.getClientID(), "wrong"));
    }

    @Test
    void individualClientShouldNotBeVipAndShouldNotHaveFeesWaived() {
        assertFalse(individualClient.isVIP());
        assertFalse(individualClient.feeWaived());
    }

    @Test
    void studentClientShouldNotBeVipButShouldHaveFeesWaived() {
        assertFalse(studentClient.isVIP());
        assertTrue(studentClient.feeWaived());
    }

    @Test
    void vipClientShouldBeVipAndShouldHaveFeesWaived() {
        assertTrue(vipClient.isVIP());
        assertTrue(vipClient.feeWaived());
    }

    @Test
    void corporateClientShouldNotBeVipAndShouldNotHaveFeesWaived() {
        assertFalse(corporateClient.isVIP());
        assertFalse(corporateClient.feeWaived());
    }

    @Test
    void clientShouldDetectWhenItHasChequingAccount() {
        assertFalse(individualClient.hasChequingAccount());

        individualClient.addAccount(new ChequingAccount());

        assertTrue(individualClient.hasChequingAccount());
    }

    @Test
    void getChequingAccountShouldReturnTheChequingAccount() {
        ChequingAccount chequing = new ChequingAccount();
        individualClient.addAccount(chequing);

        assertSame(chequing, individualClient.getChequingAccount());
    }

    // =========================================================
    // ACCOUNT BASIC TESTS
    // =========================================================

    @Test
    void newAccountShouldStartWithZeroBalance() {
        ChequingAccount account = new ChequingAccount();

        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    void accountDepositShouldIncreaseBalance() {
        ChequingAccount account = new ChequingAccount();

        account.deposit(100.0);

        assertEquals(100.0, account.getBalance(), 0.001);
    }

    @Test
    void overloadedDepositShouldIncreaseBalance() {
        ChequingAccount account = new ChequingAccount();

        account.deposit(150.0, "Paycheque");

        assertEquals(150.0, account.getBalance(), 0.001);
    }

    @Test
    void accountWithdrawShouldDecreaseBalance() throws InsufficientFundsException {
        ChequingAccount account = new ChequingAccount();
        account.deposit(200.0);

        account.withdraw(50.0);

        assertEquals(150.0, account.getBalance(), 0.001);
    }

    @Test
    void accountWithdrawShouldThrowExceptionWhenBalanceTooLow() {
        ChequingAccount account = new ChequingAccount();
        account.deposit(50.0);

        assertThrows(InsufficientFundsException.class, () -> {
            account.withdraw(100.0);
        });
    }

    // =========================================================
    // OPEN ACCOUNT TESTS
    // =========================================================

    @Test
    void openChequingAccountShouldAddOneChequingAccount() {
        bankService.openChequingAccount(individualClient);

        assertEquals(1, individualClient.getAccounts().size());
        assertTrue(individualClient.getAccounts().get(0) instanceof ChequingAccount);
    }

    @Test
    void openChequingAccountShouldNotOpenSecondChequingAccount() {
        bankService.openChequingAccount(individualClient);
        bankService.openChequingAccount(individualClient);

        assertEquals(1, individualClient.getAccounts().size());
    }

    @Test
    void openingSavingsWithoutChequingShouldThrowException() {
        assertThrows(MissingChequingException.class, () -> {
            bankService.openSavingsAccount(individualClient);
        });
    }

    @Test
    void openingInvestmentWithoutChequingShouldThrowException() {
        assertThrows(MissingChequingException.class, () -> {
            bankService.openInvestmentAccount(individualClient);
        });
    }

    @Test
    void openingSavingsAfterChequingShouldWork() throws MissingChequingException {
        bankService.openChequingAccount(individualClient);
        bankService.openSavingsAccount(individualClient);

        assertEquals(2, individualClient.getAccounts().size());
        assertTrue(individualClient.getAccounts().get(1) instanceof SavingsAccount);
    }

    @Test
    void openingInvestmentAfterChequingShouldWork() throws MissingChequingException {
        bankService.openChequingAccount(individualClient);
        bankService.openInvestmentAccount(individualClient);

        assertEquals(2, individualClient.getAccounts().size());
        assertTrue(individualClient.getAccounts().get(1) instanceof InvestmentAccount);
    }

    // =========================================================
    // BANK SERVICE DEPOSIT / WITHDRAW TESTS
    // =========================================================

    @Test
    void bankServiceDepositShouldIncreaseBalance() {
        ChequingAccount account = new ChequingAccount();

        bankService.deposit(account, 100.0);

        assertEquals(100.0, account.getBalance(), 0.001);
    }

    @Test
    void bankServiceDepositWithZeroOrNegativeAmountShouldNotChangeBalance() {
        ChequingAccount account = new ChequingAccount();

        bankService.deposit(account, 0.0);
        bankService.deposit(account, -50.0);

        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    void bankServiceWithdrawShouldDecreaseBalance() throws InsufficientFundsException {
        ChequingAccount account = new ChequingAccount();
        account.deposit(200.0);

        bankService.withdraw(account, 75.0);

        assertEquals(125.0, account.getBalance(), 0.001);
    }

    @Test
    void bankServiceWithdrawWithZeroOrNegativeAmountShouldNotChangeBalance()
            throws InsufficientFundsException {

        ChequingAccount account = new ChequingAccount();
        account.deposit(200.0);

        bankService.withdraw(account, 0.0);
        bankService.withdraw(account, -50.0);

        assertEquals(200.0, account.getBalance(), 0.001);
    }

    @Test
    void bankServiceWithdrawShouldThrowExceptionWhenFundsTooLow() {
        ChequingAccount account = new ChequingAccount();
        account.deposit(50.0);

        assertThrows(InsufficientFundsException.class, () -> {
            bankService.withdraw(account, 100.0);
        });
    }

    // =========================================================
    // TRANSFER TESTS
    // =========================================================

    @Test
    void transferShouldMoveMoneyFromOneAccountToAnother()
            throws InsufficientFundsException {

        ChequingAccount from = new ChequingAccount();
        SavingsAccount to = new SavingsAccount(false, false);

        from.deposit(300.0);

        bankService.transfer(from, to, 100.0);

        assertEquals(200.0, from.getBalance(), 0.001);
        assertEquals(100.0, to.getBalance(), 0.001);
    }

    @Test
    void transferShouldThrowExceptionForNegativeAmount() {
        ChequingAccount from = new ChequingAccount();
        SavingsAccount to = new SavingsAccount(false, false);

        from.deposit(300.0);

        assertThrows(InsufficientFundsException.class, () -> {
            bankService.transfer(from, to, -50.0);
        });
    }

    @Test
    void transferShouldThrowExceptionWhenSenderHasNotEnoughMoney() {
        ChequingAccount from = new ChequingAccount();
        SavingsAccount to = new SavingsAccount(false, false);

        from.deposit(50.0);

        assertThrows(InsufficientFundsException.class, () -> {
            bankService.transfer(from, to, 100.0);
        });
    }

    // =========================================================
    // SAVINGS ACCOUNT TESTS
    // =========================================================

    @Test
    void savingsAccountShouldApplyTwoPercentInterestForNormalClient() {
        SavingsAccount savings = new SavingsAccount(false, false);
        savings.deposit(1000.0);

        savings.applyInterest();

        assertEquals(1020.0, savings.getBalance(), 0.001);
    }

    @Test
    void savingsAccountShouldApplyThreePercentInterestForVipClient() {
        SavingsAccount savings = new SavingsAccount(true, true);
        savings.deposit(1000.0);

        savings.applyInterest();

        assertEquals(1030.0, savings.getBalance(), 0.001);
    }

    @Test
    void savingsAccountWithdrawShouldDecreaseBalance() throws InsufficientFundsException {
        SavingsAccount savings = new SavingsAccount(false, false);
        savings.deposit(500.0);

        savings.withdraw(200.0);

        assertEquals(300.0, savings.getBalance(), 0.001);
    }

    @Test
    void savingsAccountWithdrawShouldThrowExceptionWhenFundsTooLow() {
        SavingsAccount savings = new SavingsAccount(false, false);
        savings.deposit(100.0);

        assertThrows(InsufficientFundsException.class, () -> {
            savings.withdraw(200.0);
        });
    }

    @Test
    void savingsAccountShouldChargeMonthlyFeeForNormalClient()
            throws InsufficientFundsException {

        SavingsAccount savings = new SavingsAccount(false, false);
        savings.deposit(100.0);

        savings.applyMonthlyFee(individualClient);

        assertEquals(90.0, savings.getBalance(), 0.001);
    }

    @Test
    void savingsAccountShouldNotChargeMonthlyFeeForStudentClient()
            throws InsufficientFundsException {

        SavingsAccount savings = new SavingsAccount(true, false);
        savings.deposit(100.0);

        savings.applyMonthlyFee(studentClient);

        assertEquals(100.0, savings.getBalance(), 0.001);
    }

    @Test
    void savingsAccountMonthlyFeeShouldThrowExceptionWhenFundsTooLow() {
        SavingsAccount savings = new SavingsAccount(false, false);
        savings.deposit(5.0);

        assertThrows(InsufficientFundsException.class, () -> {
            savings.applyMonthlyFee(individualClient);
        });
    }

    // =========================================================
    // CHEQUING ACCOUNT TESTS
    // =========================================================

    @Test
    void chequingAccountShouldChargeMonthlyFeeForNormalClient()
            throws InsufficientFundsException {

        ChequingAccount chequing = new ChequingAccount();
        chequing.deposit(100.0);

        chequing.applyMonthlyFee(individualClient);

        assertEquals(90.0, chequing.getBalance(), 0.001);
    }

    @Test
    void chequingAccountShouldNotChargeMonthlyFeeForStudentClient()
            throws InsufficientFundsException {

        ChequingAccount chequing = new ChequingAccount();
        chequing.deposit(100.0);

        chequing.applyMonthlyFee(studentClient);

        assertEquals(100.0, chequing.getBalance(), 0.001);
    }

    @Test
    void chequingAccountMonthlyFeeShouldThrowExceptionWhenFundsTooLow() {
        ChequingAccount chequing = new ChequingAccount();
        chequing.deposit(5.0);

        assertThrows(InsufficientFundsException.class, () -> {
            chequing.applyMonthlyFee(individualClient);
        });
    }

    // =========================================================
    // INVESTMENT ACCOUNT TESTS
    // =========================================================

    @Test
    void investmentAccountShouldApplyFivePercentInterestForNormalClient() {
        InvestmentAccount investment = new InvestmentAccount(false);
        investment.deposit(1000.0);

        investment.applyInterest();

        assertEquals(1050.0, investment.getBalance(), 0.001);
    }

    @Test
    void investmentAccountShouldApplySixPercentInterestForVipClient() {
        InvestmentAccount investment = new InvestmentAccount(true);
        investment.deposit(1000.0);

        investment.applyInterest();

        assertEquals(1060.0, investment.getBalance(), 0.001);
    }

    @Test
    void investmentAccountDirectWithdrawShouldNotBeAllowed() {
        InvestmentAccount investment = new InvestmentAccount(false);
        investment.deposit(500.0);

        assertThrows(UnsupportedOperationException.class, () -> {
            investment.withdraw(100.0);
        });
    }

    @Test
    void investmentAccountShouldChargeMonthlyFeeForNormalClient()
            throws InsufficientFundsException {

        InvestmentAccount investment = new InvestmentAccount(false);
        investment.deposit(100.0);

        investment.applyMonthlyFee(individualClient);

        assertEquals(90.0, investment.getBalance(), 0.001);
    }

    @Test
    void investmentAccountShouldNotChargeMonthlyFeeForVipClient()
            throws InsufficientFundsException {

        InvestmentAccount investment = new InvestmentAccount(true);
        investment.deposit(100.0);

        investment.applyMonthlyFee(vipClient);

        assertEquals(100.0, investment.getBalance(), 0.001);
    }

    @Test
    void investmentAccountTransferBeforeOneYearShouldThrowException() {
        InvestmentAccount investment = new InvestmentAccount(false);
        ChequingAccount chequing = new ChequingAccount();

        investment.deposit(500.0);

        assertThrows(InvestmentLockException.class, () -> {
            investment.transferToChequeing(chequing, 100.0);
        });
    }

    @Test
    void investmentAccountTransferAfterOneYearShouldWork()
            throws InvestmentLockException, InsufficientFundsException {

        InvestmentAccount investment = new InvestmentAccount(false);
        ChequingAccount chequing = new ChequingAccount();

        investment.deposit(500.0);
        investment.setOpeningDate(LocalDate.now().minusDays(365));

        investment.transferToChequeing(chequing, 100.0);

        assertEquals(400.0, investment.getBalance(), 0.001);
        assertEquals(100.0, chequing.getBalance(), 0.001);
    }

    @Test
    void investmentAccountTransferShouldThrowExceptionWhenFundsTooLow()
            throws InvestmentLockException {

        InvestmentAccount investment = new InvestmentAccount(false);
        ChequingAccount chequing = new ChequingAccount();

        investment.deposit(50.0);
        investment.setOpeningDate(LocalDate.now().minusDays(365));

        assertThrows(InsufficientFundsException.class, () -> {
            investment.transferToChequeing(chequing, 100.0);
        });
    }

    // =========================================================
    // BANK SERVICE MULTI-ACCOUNT TESTS
    // =========================================================

    @Test
    void applyInterestToAllAccountsShouldOnlyAffectSavingsAndInvestment()
            throws MissingChequingException {

        bankService.openChequingAccount(individualClient);
        bankService.openSavingsAccount(individualClient);
        bankService.openInvestmentAccount(individualClient);

        Account chequing = individualClient.getAccounts().get(0);
        Account savings = individualClient.getAccounts().get(1);
        Account investment = individualClient.getAccounts().get(2);

        chequing.deposit(1000.0);
        savings.deposit(1000.0);
        investment.deposit(1000.0);

        bankService.applyInterestToAllAccount(individualClient);

        assertEquals(1000.0, chequing.getBalance(), 0.001);
        assertEquals(1020.0, savings.getBalance(), 0.001);
        assertEquals(1050.0, investment.getBalance(), 0.001);
    }

    @Test
    void applyMonthlyFeeToAllAccountsShouldChargeNormalClientAccounts()
            throws MissingChequingException, InsufficientFundsException {

        bankService.openChequingAccount(individualClient);
        bankService.openSavingsAccount(individualClient);
        bankService.openInvestmentAccount(individualClient);

        for (Account account : individualClient.getAccounts()) {
            account.deposit(100.0);
        }

        bankService.applyMonthlyFeeToAllAccounts(individualClient);

        assertEquals(90.0, individualClient.getAccounts().get(0).getBalance(), 0.001);
        assertEquals(90.0, individualClient.getAccounts().get(1).getBalance(), 0.001);
        assertEquals(90.0, individualClient.getAccounts().get(2).getBalance(), 0.001);
    }

    @Test
    void getTotalBalanceShouldReturnSumOfAllAccounts()
            throws MissingChequingException {

        bankService.openChequingAccount(individualClient);
        bankService.openSavingsAccount(individualClient);
        bankService.openInvestmentAccount(individualClient);

        individualClient.getAccounts().get(0).deposit(100.0);
        individualClient.getAccounts().get(1).deposit(200.0);
        individualClient.getAccounts().get(2).deposit(300.0);

        assertEquals(600.0, bankService.getTotalBalance(individualClient), 0.001);
    }

    @Test
    void findAccountByNumberShouldReturnCorrectAccount()
            throws MissingChequingException {

        bankService.openChequingAccount(individualClient);
        bankService.openSavingsAccount(individualClient);

        Account expected = individualClient.getAccounts().get(1);
        String accountNumber = expected.getAccountNumber();

        Account found = bankService.findAccountByNumber(individualClient, accountNumber);

        assertSame(expected, found);
    }

    @Test
    void findAccountByNumberShouldReturnNullIfAccountDoesNotExist() {
        Account found = bankService.findAccountByNumber(individualClient, "A999");

        assertNull(found);
    }

    @Test
    void transferInvestmentToChequingShouldWorkAfterOneYear()
            throws MissingChequingException, InvestmentLockException, InsufficientFundsException {

        bankService.openChequingAccount(individualClient);
        bankService.openInvestmentAccount(individualClient);

        InvestmentAccount investment =
                (InvestmentAccount) individualClient.getAccounts().get(1);

        investment.deposit(500.0);
        investment.setOpeningDate(LocalDate.now().minusDays(365));

        bankService.transferInvestmentToChequing(individualClient, investment, 200.0);

        assertEquals(300.0, investment.getBalance(), 0.001);
        assertEquals(200.0, individualClient.getChequingAccount().getBalance(), 0.001);
    }

    @Test
    void transferInvestmentToChequingShouldThrowExceptionIfClientHasNoChequing() {
        InvestmentAccount investment = new InvestmentAccount(false);

        assertThrows(MissingChequingException.class, () -> {
            bankService.transferInvestmentToChequing(individualClient, investment, 100.0);
        });
    }
}
