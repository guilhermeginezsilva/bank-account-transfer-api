package br.com.ginezgit.service;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

import static br.com.ginezgit.controller.exception.InvalidInputParameterValueException.buildInvalidParameterValueMessage;

public class CustomerService {

    private CrudDAO<String, Customer> customerDAO = CustomerDAO.getInstance();

    public Customer getCustomer(String id) {
        return this.customerDAO.getById(id).orElseThrow(()-> new EntityNotFoundException("Customer couldn't be found on our repositories: id[" + id + "]"));
    }

    public List<Customer> getAllCustomers() {
        return this.customerDAO.getAll();
    }

    public Customer createNewCustomer(Customer customer) {
        Customer newCustomer = Customer.newCustomer(customer.getName());
        this.customerDAO.insert(newCustomer.getId(), newCustomer);
        return newCustomer;
    }

    public Customer updateCustomer(Customer customer) {
        if (customer.getId() == null || customer.getId().isEmpty()) {
            throw new InvalidInputParameterValueException(buildInvalidParameterValueMessage("customerId"));
        }

        Optional<Customer> customerOptional = customerDAO.getById(customer.getId());
        customerOptional.orElseThrow(() -> new EntityNotFoundException("Customer couldn't be found on our repositories: id[" + customer.getId() + "]"));

        Customer updatedCustomer = customerOptional.get();
        updatedCustomer.setName(customer.getName());

        this.customerDAO.update(customer.getId(), customer);

        return updatedCustomer;
    }
}
