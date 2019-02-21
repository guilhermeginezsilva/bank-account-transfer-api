package br.com.ginezgit.service.account;

import br.com.ginezgit.controller.AccountController;
import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.swing.text.html.Option;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class AccountServiceTest {

    AccountService accountService = null;
    private CrudDAO<String, Account> accountDAO = null;
    private CrudDAO<String, Customer> customerDAO = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;

    @Before
    public void initialize() {
        prepareData();

        this.accountService = PowerMockito.spy(new AccountService());
        this.accountDAO = PowerMockito.spy(AccountDAO.getInstance());
        this.customerDAO = PowerMockito.spy(CustomerDAO.getInstance());

        Whitebox.setInternalState(accountService, "accountDAO", accountDAO);
        Whitebox.setInternalState(accountService, "customerDAO", customerDAO);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount2 = new Account("vxcvxcvx-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer2);
    }

    @Test
    public void getAccountWithExistentAccountId() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testAccount1))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(anyString());
        Account account = accountService.getAccount(testAccount1.getId());

        Assert.assertEquals(testAccount1, account);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getAccountWithNonExistentAccountId() throws Exception {
        PowerMockito
                .doThrow(new EntityNotFoundException("Account couldn't be found on our repositories: id[" + testAccount1.getId() + "]"))
                .when(accountDAO, method(AccountDAO.class, "getById", String.class))
                .withArguments(anyString());
        accountService.getAccount(testAccount1.getId());
    }

    @Test
    public void getAllAccountWithAvailableAccounts() throws Exception {
        List<Account> expectedAccounts = new ArrayList(Arrays.asList(this.testAccount1, this.testAccount2));
        PowerMockito
                .doReturn(expectedAccounts)
                .when(accountDAO, method(AccountDAO.class, "getAll"))
                .withNoArguments();
        List<Account> accounts = accountService.getAllAccounts();

        Assert.assertEquals(expectedAccounts.size(), accounts.size());

        accounts.removeAll(expectedAccounts);
        Assert.assertEquals(0, accounts.size());
    }

    @Test
    public void getAllAccountWithNoAvailableAccounts() throws Exception {
        List<Account> expectedAccounts = new ArrayList();
        PowerMockito
                .doReturn(expectedAccounts)
                .when(accountDAO, method(AccountDAO.class, "getAll"))
                .withNoArguments();
        List<Account> accounts = accountService.getAllAccounts();

        Assert.assertEquals(expectedAccounts.size(), accounts.size());
    }

    @Test
    public void createNewAccountWithStringId() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(this.testCustomer1))
                .when(customerDAO, method(CustomerDAO.class, "getById", String.class))
                .withArguments(anyString());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "insert", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));

        Account account = accountService.createNewAccount(this.testCustomer1.getId());

        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getId());
        Assert.assertNotNull(account.getLastUpdate());
        Assert.assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO) );
        Assert.assertEquals(this.testCustomer1, account.getCustomer());
    }

    @Test
    public void createNewAccountWithCustomer() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(this.testCustomer1))
                .when(customerDAO, method(CustomerDAO.class, "getById", String.class))
                .withArguments(anyString());
        PowerMockito
                .doNothing()
                .when(accountDAO, method(AccountDAO.class, "insert", String.class, Account.class))
                .withArguments(anyString(), any(Account.class));

        Account account = accountService.createNewAccount(this.testCustomer1);

        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getId());
        Assert.assertNotNull(account.getLastUpdate());
        Assert.assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO) );
        Assert.assertEquals(this.testCustomer1, account.getCustomer());
    }


}
