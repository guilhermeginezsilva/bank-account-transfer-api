package br.com.ginezgit.service.account.operation;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.TransactionDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.model.transaction.*;
import br.com.ginezgit.service.AccountOperationService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import br.com.ginezgit.service.exception.InsufficientAccountBalanceException;
import br.com.ginezgit.service.exception.InvalidParameterException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class AccountOperationServiceTest {

    AccountOperationService accountsOperationService = null;
    private CrudDAO<String, Account> accountDAO = null;
    private CrudDAO<Long, Transaction> transactionDAO = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;
    TransferTransaction transferTransaction = null;
    CreditTransaction creditTransaction = null;
    DebitTransaction debitTransaction = null;

    String fakeAccount = "123456789";

    @Before
    public void initialize() {
        prepareData();

        this.accountsOperationService = PowerMockito.spy(new AccountOperationService());
        this.accountDAO = PowerMockito.spy(AccountDAO.getInstance());
        this.transactionDAO = PowerMockito.spy(TransactionDAO.getInstance());

        this.transferTransaction = TransferTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, testAccount2, BigDecimal.TEN);
        this.creditTransaction = CreditTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, new BigDecimal(1000));
        this.debitTransaction = DebitTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, new BigDecimal(1000));

        Whitebox.setInternalState(accountsOperationService, "accountDAO", this.accountDAO);
        Whitebox.setInternalState(accountsOperationService, "transactionDAO", this.transactionDAO);

    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount2 = new Account("vxcvxcvx-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer2);
    }

    @Test(expected = InvalidParameterException.class)
    public void transferWithNullOrigin() throws Exception {
        accountsOperationService.transfer(null, testAccount1.getId(), testAccount2.getId(), BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void transferWithNonExistentFromAccount() throws Exception {
        PowerMockito
                .doReturn(Optional.empty())
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(fakeAccount);
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());

        accountsOperationService.transfer(TransactionOrigin.EXTERNAL, fakeAccount, testAccount2.getId(), BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void transferWithNonExistentToAccount() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.empty())
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(fakeAccount);

        accountsOperationService.transfer(TransactionOrigin.EXTERNAL, testAccount1.getId(), fakeAccount, BigDecimal.TEN);
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void transferWithInsufficientAccountBalanceZero() throws Exception {
        this.testAccount1.setBalance(BigDecimal.ZERO);
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());

        accountsOperationService.transfer(TransactionOrigin.EXTERNAL, testAccount1.getId(), testAccount2.getId(), BigDecimal.TEN);
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void transferWithInsufficientAccountBalanceWithSomeValue() throws Exception {
        this.testAccount1.setBalance(BigDecimal.ONE);
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());

        accountsOperationService.transfer(TransactionOrigin.EXTERNAL, testAccount1.getId(), testAccount2.getId(), BigDecimal.TEN);
    }

    @Test
    public void transferWithAccountBalanceWithSameValue() throws Exception {
        this.testAccount1.setBalance(BigDecimal.TEN);

        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "update", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));
        PowerMockito
                .doNothing()
                .when(transactionDAO, method(TransactionDAO.class, "insert", Long.class, Transaction.class))
                .withArguments(anyLong(), any(Transaction.class));

        TransferTransaction transferTransaction = accountsOperationService.transfer(TransactionOrigin.EXTERNAL, testAccount1.getId(), testAccount2.getId(), BigDecimal.TEN);

        Assert.assertEquals(TransactionStatus.SUCCESSFUL, transferTransaction.getStatus());
        Assert.assertNotNull(transferTransaction.getRrn());
        Assert.assertTrue(transferTransaction.getRrn() >= 0l);
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(transferTransaction.getAmount()));
        Assert.assertEquals(testAccount1, transferTransaction.getFrom());
        Assert.assertEquals(testAccount2, transferTransaction.getTo());
    }

    @Test
    public void transfer() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "update", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));
        PowerMockito
                .doNothing()
                .when(transactionDAO, method(TransactionDAO.class, "insert", Long.class, Transaction.class))
                .withArguments(anyLong(), any(Transaction.class));

        TransferTransaction transferTransaction = accountsOperationService.transfer(TransactionOrigin.EXTERNAL, testAccount1.getId(), testAccount2.getId(), BigDecimal.TEN);

        Assert.assertEquals(TransactionStatus.SUCCESSFUL, transferTransaction.getStatus());
        Assert.assertNotNull(transferTransaction.getRrn());
        Assert.assertTrue(transferTransaction.getRrn() >= 0l);
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(transferTransaction.getAmount()));
        Assert.assertEquals(testAccount1, transferTransaction.getFrom());
        Assert.assertEquals(testAccount2, transferTransaction.getTo());
    }

    @Test(expected = InvalidParameterException.class)
    public void creditWithNullOrigin() throws Exception {
        accountsOperationService.credit(null, testAccount1.getId(), BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void creditWithNonExistentFromAccount() throws Exception {
        PowerMockito
                .doReturn(Optional.empty())
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(fakeAccount);

        accountsOperationService.credit(TransactionOrigin.EXTERNAL, fakeAccount, BigDecimal.TEN);
    }

    @Test
    public void credit() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "update", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));
        PowerMockito
                .doNothing()
                .when(transactionDAO, method(TransactionDAO.class, "insert", Long.class, Transaction.class))
                .withArguments(anyLong(), any(Transaction.class));

        CreditTransaction creditTransaction = accountsOperationService.credit(TransactionOrigin.EXTERNAL, testAccount1.getId(), BigDecimal.TEN);

        Assert.assertEquals(TransactionStatus.SUCCESSFUL, creditTransaction.getStatus());
        Assert.assertNotNull(creditTransaction.getRrn());
        Assert.assertTrue(creditTransaction.getRrn() >= 0l);
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(creditTransaction.getAmount()));
        Assert.assertEquals(testAccount1, creditTransaction.getAccount());
    }

    @Test(expected = InvalidParameterException.class)
    public void debitWithNullOrigin() throws Exception {
        accountsOperationService.debit(null, testAccount1.getId(), BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void debitWithNonExistentFromAccount() throws Exception {
        PowerMockito
                .doReturn(Optional.empty())
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(fakeAccount);

        accountsOperationService.debit(TransactionOrigin.EXTERNAL, fakeAccount, BigDecimal.TEN);
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void debitWithInsufficientAccountBalanceZero() throws Exception {
        this.testAccount1.setBalance(BigDecimal.ZERO);
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());

        accountsOperationService.debit(TransactionOrigin.EXTERNAL, testAccount1.getId(), BigDecimal.TEN);
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void debitWithInsufficientAccountBalanceWithSomeValue() throws Exception {
        this.testAccount1.setBalance(BigDecimal.ONE);
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());

        accountsOperationService.debit(TransactionOrigin.EXTERNAL, testAccount1.getId(), BigDecimal.TEN);
    }

    @Test
    public void debitWithAccountBalanceWithSameValue() throws Exception {
        this.testAccount1.setBalance(BigDecimal.TEN);

        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "update", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));
        PowerMockito
                .doNothing()
                .when(transactionDAO, method(TransactionDAO.class, "insert", Long.class, Transaction.class))
                .withArguments(anyLong(), any(Transaction.class));

        DebitTransaction debitTransaction = accountsOperationService.debit(TransactionOrigin.EXTERNAL, testAccount1.getId(), BigDecimal.TEN);

        Assert.assertEquals(TransactionStatus.SUCCESSFUL, debitTransaction.getStatus());
        Assert.assertNotNull(debitTransaction.getRrn());
        Assert.assertTrue(debitTransaction.getRrn() >= 0l);
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(debitTransaction.getAmount()));
        Assert.assertEquals(testAccount1, debitTransaction.getAccount());
    }

    @Test
    public void debit() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount1.getId());
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount2))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(testAccount2.getId());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "update", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));
        PowerMockito
                .doNothing()
                .when(transactionDAO, method(TransactionDAO.class, "insert", Long.class, Transaction.class))
                .withArguments(anyLong(), any(Transaction.class));

        DebitTransaction debitTransaction = accountsOperationService.debit(TransactionOrigin.EXTERNAL, testAccount1.getId(), BigDecimal.TEN);

        Assert.assertEquals(TransactionStatus.SUCCESSFUL, debitTransaction.getStatus());
        Assert.assertNotNull(debitTransaction.getRrn());
        Assert.assertTrue(debitTransaction.getRrn() >= 0l);
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(debitTransaction.getAmount()));
        Assert.assertEquals(testAccount1, debitTransaction.getAccount());
    }

}
