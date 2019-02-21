package br.com.ginezgit.service;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

import static br.com.ginezgit.controller.exception.InvalidInputParameterValueException.buildInvalidParameterValueMessage;

public class AccountService {

    private CrudDAO<String, Account> accountDAO = AccountDAO.getInstance();
    private CrudDAO<String, Customer> customerDAO = CustomerDAO.getInstance();

    public Account getAccount(String id) {
        return this.accountDAO.getById(id).orElseThrow(()-> new EntityNotFoundException("Account couldn't be found on our repositories: id[" + id + "]"));
    }

    public List<Account> getAllAccounts() {
        return this.accountDAO.getAll();
    }

    public Account createNewAccount(String customerId) {
        return createNewAccount(new Customer(customerId));
    }

    public Account createNewAccount(Customer customer) {
        Optional<Customer> customerOptional = customerDAO.getById(customer.getId());
        customerOptional.orElseThrow(() -> new EntityNotFoundException("Customer couldn't be found on our repositories: id[" + customer.getId() + "]"));

        Account newAccount = Account.newAccount(customerOptional.get());

        this.accountDAO.insert(newAccount.getId(), newAccount);

        return newAccount;
    }

}
