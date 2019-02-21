package br.com.ginezgit.dao.impl;

import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.model.Customer;
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

@RunWith(PowerMockRunner.class)
public class CustomerDAOTest {

    CustomerDAO customerDAO = null;
    HashMap<String, Customer> entitiesRepository = null;

    Customer testCustomer1 = null;
    Customer testCustomer2 = null;
    String fakeCustomerId = "123456789";

    @Before
    public void initialize() {
        prepareData();
        this.customerDAO = PowerMockito.spy(CustomerDAO.getInstance());

        this.entitiesRepository = new HashMap<String, Customer>();
        entitiesRepository.put(this.testCustomer1.getId(), this.testCustomer1);
        entitiesRepository.put(this.testCustomer2.getId(), this.testCustomer2);

        Whitebox.setInternalState(this.customerDAO, "entitiesRepository", this.entitiesRepository);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
    }

    @Test
    public void getCustomerWithNonExistentCustomerId() {
        Optional<Customer> customerOptional = customerDAO.getById(fakeCustomerId);
        Assert.assertFalse(customerOptional.isPresent());
    }

    @Test
    public void getCustomerWithExistentCustomerId() {
        Optional<Customer> customerOptional = customerDAO.getById(this.testCustomer1.getId());
        Assert.assertTrue(customerOptional.isPresent());
        Customer customerFound = customerOptional.get();
        Assert.assertEquals(this.testCustomer1, customerFound);
    }

    @Test
    public void getAllCustomerWithNoAvailableCustomers() {
        this.entitiesRepository = new HashMap<String, Customer>();
        Whitebox.setInternalState(this.customerDAO, "entitiesRepository", this.entitiesRepository);

        List<Customer> customers = customerDAO.getAll();
        Assert.assertEquals(0, customers.size());
    }

    @Test
    public void getAllCustomerWithAvailableCustomers() {
        List<Customer> customers = customerDAO.getAll();
        Assert.assertEquals(2, customers.size());
        Assert.assertTrue(customers.contains(this.testCustomer1));
        Assert.assertTrue(customers.contains(this.testCustomer2));
    }

    @Test
    public void insertCustomers() {
        this.entitiesRepository = new HashMap<String, Customer>();
        Whitebox.setInternalState(this.customerDAO, "entitiesRepository", this.entitiesRepository);

        Assert.assertEquals(0, entitiesRepository.keySet().size());
        customerDAO.insert(this.testCustomer1.getId(), this.testCustomer1);
        Assert.assertEquals(1, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testCustomer1, entitiesRepository.get(this.testCustomer1.getId()));

        customerDAO.insert(this.testCustomer2.getId(), this.testCustomer2);
        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testCustomer2, entitiesRepository.get(this.testCustomer2.getId()));
    }

    @Test
    public void insertCustomersWithTransactionCommit() {
        this.entitiesRepository = new HashMap<String, Customer>();
        Whitebox.setInternalState(this.customerDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        customerDAO.beginTransaction();
        customerDAO.insert(this.testCustomer1.getId(), this.testCustomer1);
        customerDAO.insert(this.testCustomer2.getId(), this.testCustomer2);
        customerDAO.commitTransaction();

        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testCustomer1, entitiesRepository.get(this.testCustomer1.getId()));
        Assert.assertEquals(this.testCustomer2, entitiesRepository.get(this.testCustomer2.getId()));
    }

    @Test
    public void insertCustomersWithTransactionRollback() {
        this.entitiesRepository = new HashMap<String, Customer>();
        Whitebox.setInternalState(this.customerDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        customerDAO.beginTransaction();
        customerDAO.insert(this.testCustomer1.getId(), this.testCustomer1);
        customerDAO.insert(this.testCustomer2.getId(), this.testCustomer2);
        customerDAO.rollbackTransaction();

        Assert.assertEquals(0, entitiesRepository.keySet().size());
    }

    @Test
    public void updateCustomers() {
        String expectedCustomer1NewName = "New name 1";
        String expectedCustomer2NewName = "New name 2";

        Customer updatedCustomer1 = new Customer(this.testCustomer1.getId(), expectedCustomer1NewName);
        customerDAO.update(updatedCustomer1.getId(),updatedCustomer1);
        Assert.assertNotEquals(this.testCustomer1, this.entitiesRepository.get(this.testCustomer1.getId()));
        Assert.assertEquals(expectedCustomer1NewName, this.entitiesRepository.get(this.testCustomer1.getId()).getName());

        Customer updatedCustomer2 = new Customer(this.testCustomer2.getId(), expectedCustomer2NewName);
        customerDAO.update(updatedCustomer2.getId(),updatedCustomer2);
        Assert.assertNotEquals(this.testCustomer2, this.entitiesRepository.get(this.testCustomer2.getId()));
        Assert.assertEquals(expectedCustomer2NewName, this.entitiesRepository.get(this.testCustomer2.getId()).getName());
    }

    @Test
    public void updateCustomersWithTransactionCommit() {
        String expectedCustomer1NewName = "New name 1";
        String expectedCustomer2NewName = "New name 2";

        customerDAO.beginTransaction();
        Customer updatedCustomer1 = new Customer(this.testCustomer1.getId(), expectedCustomer1NewName);
        customerDAO.update(updatedCustomer1.getId(),updatedCustomer1);
        Customer updatedCustomer2 = new Customer(this.testCustomer2.getId(), expectedCustomer2NewName);
        customerDAO.update(updatedCustomer2.getId(),updatedCustomer2);
        customerDAO.commitTransaction();

        Assert.assertNotEquals(this.testCustomer1, this.entitiesRepository.get(this.testCustomer1.getId()));
        Assert.assertEquals(expectedCustomer1NewName, this.entitiesRepository.get(this.testCustomer1.getId()).getName());

        Assert.assertNotEquals(this.testCustomer2, this.entitiesRepository.get(this.testCustomer2.getId()));
        Assert.assertEquals(expectedCustomer2NewName, this.entitiesRepository.get(this.testCustomer2.getId()).getName());
    }

    @Test
    public void updateCustomersWithTransactionRollback() {
        String expectedCustomer1NewName = "New name 1";
        String expectedCustomer2NewName = "New name 2";

        customerDAO.beginTransaction();
        Customer updatedCustomer1 = new Customer(this.testCustomer1.getId(), expectedCustomer1NewName);
        customerDAO.update(updatedCustomer1.getId(),updatedCustomer1);
        Customer updatedCustomer2 = new Customer(this.testCustomer2.getId(), expectedCustomer2NewName);
        customerDAO.update(updatedCustomer2.getId(),updatedCustomer2);
        customerDAO.rollbackTransaction();

        Assert.assertEquals(this.testCustomer1, this.entitiesRepository.get(this.testCustomer1.getId()));
        Assert.assertEquals(this.testCustomer1.getName(), this.entitiesRepository.get(this.testCustomer1.getId()).getName());

        Assert.assertEquals(this.testCustomer2, this.entitiesRepository.get(this.testCustomer2.getId()));
        Assert.assertEquals(this.testCustomer2.getName(), this.entitiesRepository.get(this.testCustomer2.getId()).getName());
    }

}
