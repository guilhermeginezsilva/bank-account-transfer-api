package br.com.ginezgit.dao.impl;

import br.com.ginezgit.dao.impl.memory.InMemoryDAO;
import br.com.ginezgit.model.transaction.Transaction;
import org.apache.log4j.Logger;

public class TransactionDAO extends InMemoryDAO<Long, Transaction> {

    private static TransactionDAO instance = null;

    private TransactionDAO() {
        super();
    }

    public static TransactionDAO getInstance() {
        if(instance == null) {
            instance = new TransactionDAO();
        }
        return instance;
    }

}
