package br.com.ginezgit.dao.impl;

import br.com.ginezgit.dao.impl.TransactionDAO;
import br.com.ginezgit.model.transaction.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RunWith(PowerMockRunner.class)
public class TransactionDAOTest {

    TransactionDAO transactionDAO = null;
    HashMap<Long, Transaction> entitiesRepository = null;

    Transaction testTransaction1 = null;
    Transaction testTransaction2 = null;

    Long fakeTransactionRrn = 123456789l;

    @Before
    public void initialize() {
        prepareData();
        this.transactionDAO = PowerMockito.spy(TransactionDAO.getInstance());

        this.entitiesRepository = new HashMap<Long, Transaction>();
        entitiesRepository.put(this.testTransaction1.getRrn(), this.testTransaction1);
        entitiesRepository.put(this.testTransaction2.getRrn(), this.testTransaction2);

        Whitebox.setInternalState(this.transactionDAO, "entitiesRepository", this.entitiesRepository);
    }

    private void prepareData() {
        this.testTransaction1 = Transaction.newTransaction(TransactionType.CREDIT, TransactionOrigin.EXTERNAL);
        this.testTransaction2 = Transaction.newTransaction(TransactionType.TRANSFER, TransactionOrigin.EXTERNAL);
    }

    @Test
    public void getTransactionWithNonExistentTransactionRrn() {
        Optional<Transaction> customerOptional = transactionDAO.getById(fakeTransactionRrn);
        Assert.assertFalse(customerOptional.isPresent());
    }

    @Test
    public void getTransactionWithExistentTransactionRrn() {
        Optional<Transaction> customerOptional = transactionDAO.getById(this.testTransaction1.getRrn());
        Assert.assertTrue(customerOptional.isPresent());
        Transaction customerFound = customerOptional.get();
        Assert.assertEquals(this.testTransaction1, customerFound);
    }

    @Test
    public void getAllTransactionWithNoAvailableCustomers() {
        this.entitiesRepository = new HashMap<Long, Transaction>();
        Whitebox.setInternalState(this.transactionDAO, "entitiesRepository", this.entitiesRepository);

        List<Transaction> customers = transactionDAO.getAll();
        Assert.assertEquals(0, customers.size());
    }

    @Test
    public void getAllTransactionsWithAvailableCustomers() {
        List<Transaction> customers = transactionDAO.getAll();
        Assert.assertEquals(2, customers.size());
        Assert.assertTrue(customers.contains(this.testTransaction1));
        Assert.assertTrue(customers.contains(this.testTransaction2));
    }

    @Test
    public void insertTransactions() {
        this.entitiesRepository = new HashMap<Long, Transaction>();
        Whitebox.setInternalState(this.transactionDAO, "entitiesRepository", this.entitiesRepository);

        Assert.assertEquals(0, entitiesRepository.keySet().size());
        transactionDAO.insert(this.testTransaction1.getRrn(), this.testTransaction1);
        Assert.assertEquals(1, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testTransaction1, entitiesRepository.get(this.testTransaction1.getRrn()));

        transactionDAO.insert(this.testTransaction2.getRrn(), this.testTransaction2);
        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testTransaction2, entitiesRepository.get(this.testTransaction2.getRrn()));
    }

    @Test
    public void insertTransactionsWithTransactionCommit() {
        this.entitiesRepository = new HashMap<Long, Transaction>();
        Whitebox.setInternalState(this.transactionDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        transactionDAO.beginTransaction();
        transactionDAO.insert(this.testTransaction1.getRrn(), this.testTransaction1);
        transactionDAO.insert(this.testTransaction2.getRrn(), this.testTransaction2);
        transactionDAO.commitTransaction();

        Assert.assertEquals(2, entitiesRepository.keySet().size());
        Assert.assertEquals(this.testTransaction1, entitiesRepository.get(this.testTransaction1.getRrn()));
        Assert.assertEquals(this.testTransaction2, entitiesRepository.get(this.testTransaction2.getRrn()));
    }

    @Test
    public void insertTransactionsWithTransactionRollback() {
        this.entitiesRepository = new HashMap<Long, Transaction>();
        Whitebox.setInternalState(this.transactionDAO, "entitiesRepository", this.entitiesRepository);
        Assert.assertEquals(0, entitiesRepository.keySet().size());

        transactionDAO.beginTransaction();
        transactionDAO.insert(this.testTransaction1.getRrn(), this.testTransaction1);
        transactionDAO.insert(this.testTransaction2.getRrn(), this.testTransaction2);
        transactionDAO.rollbackTransaction();

        Assert.assertEquals(0, entitiesRepository.keySet().size());
    }

    @Test
    public void updateTransactions() {
        TransactionType expectedCustomer1NewType = TransactionType.DEBIT;
        TransactionType expectedCustomer2NewType = TransactionType.CREDIT;

        Transaction updatedTransaction1 = new Transaction(this.testTransaction1.getRrn(), System.currentTimeMillis() , expectedCustomer1NewType, this.testTransaction1.getOrigin());
        transactionDAO.update(updatedTransaction1.getRrn(),updatedTransaction1);
        Assert.assertNotEquals(this.testTransaction1, this.entitiesRepository.get(this.testTransaction1.getRrn()));
        Assert.assertEquals(expectedCustomer1NewType, this.entitiesRepository.get(this.testTransaction1.getRrn()).getType());

        Transaction updatedTransaction2 = new Transaction(this.testTransaction2.getRrn(), System.currentTimeMillis() , expectedCustomer2NewType, this.testTransaction2.getOrigin());
        transactionDAO.update(updatedTransaction2.getRrn(),updatedTransaction2);
        Assert.assertNotEquals(this.testTransaction2, this.entitiesRepository.get(this.testTransaction2.getRrn()));
        Assert.assertEquals(expectedCustomer2NewType, this.entitiesRepository.get(this.testTransaction2.getRrn()).getType());
    }

    @Test
    public void updateTransactionsWithTransactionCommit() {
        TransactionType expectedCustomer1NewType = TransactionType.DEBIT;
        TransactionType expectedCustomer2NewType = TransactionType.CREDIT;

        transactionDAO.beginTransaction();
        Transaction updatedCustomer1 = new Transaction(this.testTransaction1.getRrn(), System.currentTimeMillis() ,expectedCustomer1NewType, this.testTransaction1.getOrigin());
        transactionDAO.update(updatedCustomer1.getRrn(),updatedCustomer1);
        Transaction updatedCustomer2 = new Transaction(this.testTransaction2.getRrn(), System.currentTimeMillis() , expectedCustomer2NewType, this.testTransaction2.getOrigin());
        transactionDAO.update(updatedCustomer2.getRrn(),updatedCustomer2);
        transactionDAO.commitTransaction();

        Assert.assertNotEquals(this.testTransaction1, this.entitiesRepository.get(this.testTransaction1.getRrn()));
        Assert.assertEquals(expectedCustomer1NewType, this.entitiesRepository.get(this.testTransaction1.getRrn()).getType());

        Assert.assertNotEquals(this.testTransaction2, this.entitiesRepository.get(this.testTransaction2.getRrn()));
        Assert.assertEquals(expectedCustomer2NewType, this.entitiesRepository.get(this.testTransaction2.getRrn()).getType());
    }

    @Test
    public void updateTransactionWithTransactionRollback() {
        TransactionType expectedCustomer1NewType = TransactionType.DEBIT;
        TransactionType expectedCustomer2NewType = TransactionType.CREDIT;

        transactionDAO.beginTransaction();
        Transaction updatedCustomer1 = new Transaction(this.testTransaction1.getRrn(), System.currentTimeMillis() , expectedCustomer1NewType, this.testTransaction1.getOrigin());
        transactionDAO.update(updatedCustomer1.getRrn(),updatedCustomer1);
        Transaction updatedCustomer2 = new Transaction(this.testTransaction2.getRrn(), System.currentTimeMillis() , expectedCustomer2NewType, this.testTransaction2.getOrigin());
        transactionDAO.update(updatedCustomer2.getRrn(),updatedCustomer2);
        transactionDAO.rollbackTransaction();

        Assert.assertEquals(this.testTransaction1, this.entitiesRepository.get(this.testTransaction1.getRrn()));
        Assert.assertEquals(this.testTransaction1.getType(), this.entitiesRepository.get(this.testTransaction1.getRrn()).getType());

        Assert.assertEquals(this.testTransaction2, this.entitiesRepository.get(this.testTransaction2.getRrn()));
        Assert.assertEquals(this.testTransaction2.getType(), this.entitiesRepository.get(this.testTransaction2.getRrn()).getType());
    }

}
