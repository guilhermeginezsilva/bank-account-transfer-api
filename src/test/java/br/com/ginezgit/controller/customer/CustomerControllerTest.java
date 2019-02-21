package br.com.ginezgit.controller.customer;

import br.com.ginezgit.controller.CustomerController;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.CustomerService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
public class CustomerControllerTest {

    CustomerController customerController = null;
    CustomerService customerService = null;
    AccountService accountService = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    String fakeCustomerId = "123456789";

    @Before
    public void initialize() {
        prepareData();

        this.customerController = PowerMockito.spy(new CustomerController());
        this.customerService = PowerMockito.spy(new CustomerService());
        this.accountService = PowerMockito.spy(new AccountService());
        Whitebox.setInternalState(CustomerController.class, "customerService", customerService);
        Whitebox.setInternalState(CustomerController.class, "accountService", accountService);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
    }

    @Test
    public void getCustomerWithExistentAccountId() throws Exception {
        PowerMockito.doReturn(testCustomer1)
                .when(customerService, method(CustomerService.class, "getCustomer", String.class))
                .withArguments(anyString());
        Response httpResponse = customerController.getCustomer(testCustomer1.getId());
        Customer responseCustomer = (Customer) httpResponse.getEntity();

        Assert.assertEquals(testCustomer1.getId(), responseCustomer.getId());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void getAllAccountsWithAvailableAccounts() throws Exception {
        List<Account> expectedList = new ArrayList(Arrays.asList(testCustomer1, testCustomer2));

        PowerMockito
                .doReturn(expectedList)
                .when(customerService, method(CustomerService.class, "getAllCustomers"))
                .withNoArguments();

        Response httpResponse = customerController.getAllCustomers();
        List<Customer> responseCustomers = (List<Customer>) httpResponse.getEntity();

        Assert.assertNotNull(responseCustomers);
        Assert.assertEquals(expectedList.size(), responseCustomers.size());

        responseCustomers.removeAll(expectedList);
        Assert.assertEquals(0, responseCustomers.size());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void getAllAccountsWithNoAvailableAccounts() throws Exception {
        List<Account> expectedList = new ArrayList();

        PowerMockito
                .doReturn(expectedList)
                .when(customerService, method(CustomerService.class, "getAllCustomers"))
                .withNoArguments();

        Response httpResponse = customerController.getAllCustomers();
        List<Customer> responseCustomers = (List<Customer>) httpResponse.getEntity();

        Assert.assertNotNull(responseCustomers);
        Assert.assertEquals(expectedList.size(), responseCustomers.size());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createCustomer() throws Exception {

        String newCustomerName = "Test Name";
        Customer expectedNewCustomer = Customer.newCustomer(newCustomerName);

        PowerMockito.doReturn(expectedNewCustomer)
                .when(customerService, method(CustomerService.class, "createNewCustomer", Customer.class))
                .withArguments(ArgumentMatchers.any(Customer.class));

        Response httpResponse = customerController.createCustomer(new Customer(null, newCustomerName));
        Customer responseCustomer = (Customer) httpResponse.getEntity();

        Assert.assertNotNull(responseCustomer.getId());
        Assert.assertFalse(responseCustomer.getId().isEmpty());
        Assert.assertEquals(newCustomerName, responseCustomer.getName());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithCustomerIdNull() throws Exception {
        String newName = "New Name";
        this.testCustomer1.setName(newName);

        PowerMockito.doReturn(this.testCustomer1)
                .when(customerService, method(CustomerService.class, "updateCustomer", Customer.class))
                .withArguments(ArgumentMatchers.any(Customer.class));

        Response httpResponse = customerController.updateCustomer(this.testCustomer1.getId(), new Customer(null, newName ));
        Customer updatedCustomer = (Customer) httpResponse.getEntity();

        Assert.assertEquals(this.testCustomer1.getId(), updatedCustomer.getId());
        Assert.assertEquals(newName, updatedCustomer.getName());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithCustomerIdDifferent() throws Exception {
        String newName = "New Name";
        this.testCustomer1.setName(newName);

        PowerMockito.doReturn(this.testCustomer1)
                .when(customerService, method(CustomerService.class, "updateCustomer", Customer.class))
                .withArguments(ArgumentMatchers.any(Customer.class));

        Response httpResponse = customerController.updateCustomer(this.testCustomer1.getId(), new Customer("123124342", newName ));
        Customer updatedCustomer = (Customer) httpResponse.getEntity();

        Assert.assertEquals(this.testCustomer1.getId(), updatedCustomer.getId());
        Assert.assertEquals(newName, updatedCustomer.getName());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createNewAccountWithFilledId() throws Exception {
        PowerMockito.doReturn(this.testAccount1)
                .when(accountService, method(AccountService.class, "createNewAccount", Customer.class))
                .withArguments(ArgumentMatchers.any(Customer.class));

        Response httpResponse = customerController.createNewAccount(testCustomer1.getId());
        Account createdAccount = (Account) httpResponse.getEntity();

        Assert.assertEquals(this.testAccount1.getId(), createdAccount.getId());
        Assert.assertEquals(this.testAccount1.getBalance(), createdAccount.getBalance());
        Assert.assertEquals(this.testAccount1.getLastUpdate(), createdAccount.getLastUpdate());
        Assert.assertEquals(this.testAccount1.getCustomer(), createdAccount.getCustomer());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

}
