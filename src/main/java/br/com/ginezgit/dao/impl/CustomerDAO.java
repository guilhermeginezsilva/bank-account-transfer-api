package br.com.ginezgit.dao.impl;


import br.com.ginezgit.dao.impl.memory.InMemoryDAO;
import br.com.ginezgit.model.Customer;
import org.apache.log4j.Logger;

public class CustomerDAO extends InMemoryDAO<String, Customer> {

    private static CustomerDAO instance = null;

    private CustomerDAO() {
        super();
    }

    public static CustomerDAO getInstance() {
        if(instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }

}
