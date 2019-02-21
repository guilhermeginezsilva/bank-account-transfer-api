package br.com.ginezgit.controller.account;

import br.com.ginezgit.controller.AccountController;
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

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class AccountControllerTest {

    AccountController accountController = null;
    AccountService accountService = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;
    String fakeAccountId = "123456789";

    @Before
    public void initialize() {
        prepareData();

        this.accountController = PowerMockito.spy(new AccountController());
        this.accountService = PowerMockito.spy(new AccountService());
        Whitebox.setInternalState(AccountController.class, "accountService", accountService);
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
                .doReturn(testAccount1)
                .when(accountService, method(AccountService.class, "getAccount", String.class))
                .withArguments(anyString());
        Response httpResponse = accountController.getAccount(testAccount1.getId());
        Account responseAccount = (Account) httpResponse.getEntity();

        Assert.assertEquals(testAccount1.getId(), responseAccount.getId());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void getAllAccountsWithAvailableAccounts() throws Exception {
        List<Account> expectedList = new ArrayList(Arrays.asList(testAccount1, testAccount2));

        PowerMockito
                .doReturn(expectedList)
                .when(accountService, method(AccountService.class, "getAllAccounts"))
                .withNoArguments();

        Response httpResponse = accountController.getAllAccounts();
        List<Account> responseAccounts = (List<Account>) httpResponse.getEntity();

        Assert.assertNotNull(responseAccounts);
        Assert.assertEquals(expectedList.size(), responseAccounts.size());

        responseAccounts.removeAll(expectedList);
        Assert.assertEquals(0, responseAccounts.size());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void getAllAccountsWithNoAvailableAccounts() throws Exception {
        List<Account> expectedList = new ArrayList();

        PowerMockito
                .doReturn(expectedList)
                .when(accountService, method(AccountService.class, "getAllAccounts"))
                .withNoArguments();

        Response httpResponse = accountController.getAllAccounts();
        List<Account> responseAccounts = (List<Account>) httpResponse.getEntity();

        Assert.assertNotNull(responseAccounts);
        Assert.assertEquals(expectedList.size(), responseAccounts.size());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

}
