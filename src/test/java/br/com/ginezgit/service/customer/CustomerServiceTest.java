package br.com.ginezgit.service.customer;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.CustomerService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.glassfish.jersey.internal.inject.Custom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class CustomerServiceTest {

    CustomerService customerService = null;
    private CrudDAO<String, Customer> customerDAO = null;

    Customer testCustomer1 = null;
    Customer testCustomer2 = null;

    @Before
    public void initialize() {
        prepareData();

        this.customerService = PowerMockito.spy(new CustomerService());
        this.customerDAO = PowerMockito.spy(CustomerDAO.getInstance());

        Whitebox.setInternalState(customerService, "customerDAO", customerDAO);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
    }

    @Test
    public void getCustomerWithExistentAccountId() throws Exception {
        PowerMockito
                .doReturn(Optional.ofNullable(testCustomer1))
                .when(customerDAO, method(CustomerDAO.class, "getById", String.class))
                .withArguments(anyString());
        Customer customer = customerService.getCustomer(testCustomer1.getId());

        Assert.assertEquals(testCustomer1, customer);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getCustomerWithNonExistentAccountId() throws Exception {
        PowerMockito
                .doThrow(new EntityNotFoundException("Customer couldn't be found on our repositories: id[" + testCustomer1.getId() + "]"))
                .when(customerDAO, method(CustomerDAO.class, "getById", String.class))
                .withArguments(anyString());
        customerService.getCustomer(testCustomer1.getId());
    }

    @Test
    public void getAllCustomersWithAvailableCustomers() throws Exception {
        List<Customer> expectedCustomers = new ArrayList(Arrays.asList(this.testCustomer1, this.testCustomer2));
        PowerMockito
                .doReturn(expectedCustomers)
                .when(customerDAO, method(CustomerDAO.class, "getAll"))
                .withNoArguments();
        List<Customer> accounts = customerService.getAllCustomers();

        Assert.assertEquals(expectedCustomers.size(), accounts.size());

        accounts.removeAll(expectedCustomers);
        Assert.assertEquals(0, accounts.size());
    }

    @Test
    public void getAllAccountWithNoAvailableAccounts() throws Exception {
        List<Customer> expectedCustomers = new ArrayList();
        PowerMockito
                .doReturn(expectedCustomers)
                .when(customerDAO, method(CustomerDAO.class, "getAll"))
                .withNoArguments();
        List<Customer> customers = customerService.getAllCustomers();

        Assert.assertEquals(expectedCustomers.size(), customers.size());
    }

    @Test
    public void createNewCustomer() throws Exception {
        PowerMockito
                .doNothing()
                .when(customerDAO, method(CustomerDAO.class, "insert", String.class, Customer.class))
                .withArguments(anyString(), any(Customer.class));

        Customer customer = customerService.createNewCustomer(this.testCustomer1);

        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getId());
        Assert.assertEquals(this.testCustomer1.getName(), customer.getName());
        Assert.assertNotEquals(this.testCustomer1.getId(), customer.getId());
    }

}
