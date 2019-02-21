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
public class CustomerControllerInputTest {

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
    public void getCustomerWithInvalidAccountId() {
        Response httpResponse = customerController.getCustomer(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void getCustomerWithNonExistentAccountId() throws Exception {
        PowerMockito.doThrow(new EntityNotFoundException("Customer couldn't be found on our repositories: id[" + fakeCustomerId + "]"))
                .when(customerService, method(CustomerService.class, "getCustomer", String.class))
                .withArguments(anyString());
        Response httpResponse = customerController.getCustomer(fakeCustomerId);
        Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createCustomerWithNullData() {
        Response httpResponse = customerController.createCustomer(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createCustomerWithNullName() {
        Response httpResponse = customerController.createCustomer(new Customer(null, null));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithNullData() {
        Response httpResponse = customerController.updateCustomer(null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithIdPresentAndNullCustomer() {
        Response httpResponse = customerController.updateCustomer(this.testCustomer1.getId(), null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithCustomerPresentAndNullId() {
        Response httpResponse = customerController.updateCustomer(null, this.testCustomer1);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithCustomerNameNull() {
        this.testCustomer1.setName(null);

        Response httpResponse = customerController.updateCustomer(null, this.testCustomer1);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void updateCustomerWithCustomerNameEmpty() {
        this.testCustomer1.setName("");

        Response httpResponse = customerController.updateCustomer(null, this.testCustomer1);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createNewAccountWithNullCustomerId() {
        Response httpResponse = customerController.createNewAccount(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void createNewAccountWithEmptyCustomerId() {
        Response httpResponse = customerController.createNewAccount("");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }


}
