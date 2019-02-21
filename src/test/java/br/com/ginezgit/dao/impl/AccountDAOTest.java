package br.com.ginezgit.dao.impl;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class AccountDAOTest {

    AccountDAO accountDAO = null;
    HashMap<String, Account> entitiesRepository = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;
    String fakeAccountId = "123456789";

    @Before
    public void initialize() {
        prepareData();
        this.accountDAO = PowerMockito.spy(AccountDAO.getInstance());

        this.entitiesRepository = new HashMap<String, Account>();
        entitiesRepository.put(this.testAccount1.getId(), this.testAccount1);
        entitiesRepository.put(this.testAccount2.getId(), this.testAccount2);

        Whitebox.setInternalState(this.accountDAO, "entitiesRepository", this.entitiesRepository);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount2 = new Account("vxcvxcvx-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer2);
    }

    @Test
    public void getAccountWithNonExistentAccountId() {
        Optional<Account> accountOptional = accountDAO.getById(fakeAccountId);
        Assert.assertFalse(accountOptional.isPresent());
    }

    @Test
    public void getAccountWithExistentAccountId() {
        Optional<Account> accountOptional = accountDAO.getById(this.testAccount1.getId());
        Assert.assertTrue(accountOptional.isPresent());
        Account accountFound = accountOptional.get();
        Assert.assertEquals(this.testAccount1, accountFound);
    }

    @Test
    public void getAllAccountWithNoAvailableAccounts() {
        this.entitiesRepository = new HashMap<String, Account>();
        Whitebox.setInternalState(this.accountDAO, "entitiesRepository", this.entitiesRepository);

        List<Account> accounts = accountDAO.getAll();
        Assert.assertEquals(0, accounts.size());
    }

    @Test
    public void getAllAccountWithAvailableAccounts() {
        List<Account> accounts = accountDAO.getAll();
        Assert.assertEquals(2, accounts.size());
        Assert.assertTrue(accounts.contains(this.testAccount1));
        Assert.assertTrue(accounts.contains(this.testAccount2));
    }

    @Test
    public void insertAccounts() {
        this.entitiesRepository = new HashMap<String, Account>();
        Whitebox.setInternalState(this.accountDAO, "entitiesRepository", this.entitiesRepository);

        Assert.assertEquals(0, entitiesRepository.keySet().size());
        accountDAO.insert(this.testAccount1.getId(), this.testAccount1);
        Assert.assertEquals(1, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testAccount1, entitiesRepository.get(this.testAccount1.getId()));

        accountDAO.insert(this.testAccount2.getId(), this.testAccount2);
        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testAccount2, entitiesRepository.get(this.testAccount2.getId()));
    }

    @Test
    public void insertAccountsWithTransactionCommit() {
        this.entitiesRepository = new HashMap<String, Account>();
        Whitebox.setInternalState(this.accountDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        accountDAO.beginTransaction();
        accountDAO.insert(this.testAccount1.getId(), this.testAccount1);
        accountDAO.insert(this.testAccount2.getId(), this.testAccount2);
        accountDAO.commitTransaction();

        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testAccount1, entitiesRepository.get(this.testAccount1.getId()));
        Assert.assertEquals(this.testAccount2, entitiesRepository.get(this.testAccount2.getId()));
    }

    @Test
    public void insertAccountsWithTransactionRollback() {
        this.entitiesRepository = new HashMap<String, Account>();
        Whitebox.setInternalState(this.accountDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        accountDAO.beginTransaction();
        accountDAO.insert(this.testAccount1.getId(), this.testAccount1);
        accountDAO.insert(this.testAccount2.getId(), this.testAccount2);
        accountDAO.rollbackTransaction();

        Assert.assertEquals(0, entitiesRepository.keySet().size());
    }

    @Test
    public void updateAccounts() {
        BigDecimal expectedAccount1NewBalance = new BigDecimal(50);
        BigDecimal expectedAccount2NewBalance = new BigDecimal(60);

        Account updatedAccount1 = new Account(this.testAccount1.getId(), expectedAccount1NewBalance, System.currentTimeMillis(), this.testCustomer1);
        accountDAO.update(updatedAccount1.getId(),updatedAccount1);
        Assert.assertNotEquals(this.testAccount1, this.entitiesRepository.get(this.testAccount1.getId()));
        Assert.assertEquals(expectedAccount1NewBalance, this.entitiesRepository.get(this.testAccount1.getId()).getBalance());

        Account updatedAccount2 = new Account(this.testAccount2.getId(), expectedAccount2NewBalance, System.currentTimeMillis(), this.testCustomer2);
        accountDAO.update(updatedAccount2.getId(),updatedAccount2);
        Assert.assertNotEquals(this.testAccount2, this.entitiesRepository.get(this.testAccount2.getId()));
        Assert.assertEquals(expectedAccount2NewBalance, this.entitiesRepository.get(this.testAccount2.getId()).getBalance());
    }

    @Test
    public void updateAccountsWithTransactionCommit() {
        BigDecimal expectedAccount1NewBalance = new BigDecimal(50);
        BigDecimal expectedAccount2NewBalance = new BigDecimal(60);

        accountDAO.beginTransaction();
        Account updatedAccount1 = new Account(this.testAccount1.getId(), expectedAccount1NewBalance, System.currentTimeMillis(), this.testCustomer1);
        accountDAO.update(updatedAccount1.getId(),updatedAccount1);
        Account updatedAccount2 = new Account(this.testAccount2.getId(), expectedAccount2NewBalance, System.currentTimeMillis(), this.testCustomer2);
        accountDAO.update(updatedAccount2.getId(),updatedAccount2);
        accountDAO.commitTransaction();

        Assert.assertNotEquals(this.testAccount1, this.entitiesRepository.get(this.testAccount1.getId()));
        Assert.assertEquals(expectedAccount1NewBalance, this.entitiesRepository.get(this.testAccount1.getId()).getBalance());

        Assert.assertNotEquals(this.testAccount2, this.entitiesRepository.get(this.testAccount2.getId()));
        Assert.assertEquals(expectedAccount2NewBalance, this.entitiesRepository.get(this.testAccount2.getId()).getBalance());
    }

    @Test
    public void updateAccountsWithTransactionRollback() {
        BigDecimal account1NewBalance = new BigDecimal(50);
        BigDecimal account2NewBalance = new BigDecimal(60);

        accountDAO.beginTransaction();
        Account updatedAccount1 = new Account(this.testAccount1.getId(), account1NewBalance, System.currentTimeMillis(), this.testCustomer1);
        accountDAO.update(updatedAccount1.getId(),updatedAccount1);
        Account updatedAccount2 = new Account(this.testAccount2.getId(), account2NewBalance, System.currentTimeMillis(), this.testCustomer2);
        accountDAO.update(updatedAccount2.getId(),updatedAccount2);
        accountDAO.rollbackTransaction();

        Assert.assertEquals(this.testAccount1, this.entitiesRepository.get(this.testAccount1.getId()));
        Assert.assertEquals(this.testAccount1.getBalance(), this.entitiesRepository.get(this.testAccount1.getId()).getBalance());

        Assert.assertEquals(this.testAccount2, this.entitiesRepository.get(this.testAccount2.getId()));
        Assert.assertEquals(this.testAccount2.getBalance(), this.entitiesRepository.get(this.testAccount2.getId()).getBalance());
    }

}
