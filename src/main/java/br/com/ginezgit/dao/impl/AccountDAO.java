package br.com.ginezgit.dao.impl;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.memory.InMemoryDAO;
import br.com.ginezgit.model.Account;
import org.apache.log4j.Logger;

public class AccountDAO extends InMemoryDAO<String, Account> {

    private static AccountDAO instance = null;

    private AccountDAO() {
        super();
    }

    public static AccountDAO getInstance() {
        if(instance == null) {
            instance = new AccountDAO();
        }
        return instance;
    }

}
