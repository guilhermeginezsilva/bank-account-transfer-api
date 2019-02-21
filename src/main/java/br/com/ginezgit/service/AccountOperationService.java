package br.com.ginezgit.service;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.TransactionDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.transaction.*;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import br.com.ginezgit.service.exception.InsufficientAccountBalanceException;
import br.com.ginezgit.service.exception.InvalidParameterException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountOperationService {

    private CrudDAO accountDAO = AccountDAO.getInstance();
    private CrudDAO transactionDAO = TransactionDAO.getInstance();

    public TransferTransaction transfer(TransactionOrigin origin, String fromAccountId, String toAccountId, BigDecimal amount) {

        validateTransferTransactionInput(origin, fromAccountId, toAccountId, amount);
        TransferTransaction transaction = null;

        try {
            this.accountDAO.beginTransaction();

            Account from = getIfAccountExistsElseThrowsException(fromAccountId);
            validateFromAccountBalanceGreaterThan(from, amount);

            Account to = getIfAccountExistsElseThrowsException(toAccountId);

            transaction = TransferTransaction.newTransaction(origin, from, to, amount);

            from.setBalance(from.getBalance().subtract(transaction.getAmount()));
            this.accountDAO.update(from.getId(), from);

            to.setBalance(to.getBalance().add(transaction.getAmount()));
            this.accountDAO.update(to.getId(), to);


            this.accountDAO.commitTransaction();

            transaction.setStatus(TransactionStatus.SUCCESSFUL);
            this.transactionDAO.insert(transaction.getRrn(), transaction);
        } catch (InsufficientAccountBalanceException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (EntityNotFoundException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (Throwable e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw new RuntimeException("Transfer couldn't be completed", e);
        }
        return transaction;

    }

    public CreditTransaction credit(TransactionOrigin origin, String accountId, BigDecimal amount) {

        validateTransactionInput(origin, accountId, amount);
        CreditTransaction transaction = null;

        try {
            this.accountDAO.beginTransaction();

            Account account = getIfAccountExistsElseThrowsException(accountId);

            transaction = CreditTransaction.newTransaction(origin, account, amount);

            account.setBalance(account.getBalance().add(transaction.getAmount()));
            this.accountDAO.update(account.getId(), account);

            this.accountDAO.commitTransaction();

            transaction.setStatus(TransactionStatus.SUCCESSFUL);
            this.transactionDAO.insert(transaction.getRrn(), transaction);
        } catch (InsufficientAccountBalanceException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (EntityNotFoundException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (Throwable e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw new RuntimeException("Credit couldn't be completed", e);
        }
        return transaction;
    }

    public DebitTransaction debit(TransactionOrigin origin, String accountId, BigDecimal amount) {

        validateTransactionInput(origin, accountId, amount);
        DebitTransaction transaction = null;

        try {
            this.accountDAO.beginTransaction();

            Account account = getIfAccountExistsElseThrowsException(accountId);
            validateFromAccountBalanceGreaterThan(account, amount);

            transaction = DebitTransaction.newTransaction(origin, account, amount);

            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            this.accountDAO.update(account.getId(), account);

            this.accountDAO.commitTransaction();

            transaction.setStatus(TransactionStatus.SUCCESSFUL);
            this.transactionDAO.insert(transaction.getRrn(), transaction);
        } catch (InsufficientAccountBalanceException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (EntityNotFoundException e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw e;
        } catch (Throwable e) {
            this.accountDAO.rollbackTransaction();

            if (transaction != null) {
                transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
                this.transactionDAO.insert(transaction.getRrn(), transaction);
            }

            throw new RuntimeException("Debit couldn't be completed", e);
        }
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        return this.transactionDAO.getAll();
    }

    private void validateFromAccountBalanceGreaterThan(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientAccountBalanceException("Account balance is less than the transaction amount");
        }
    }

    private void validateTransferTransactionInput(TransactionOrigin origin, String fromAccountId, String toAccountId, BigDecimal amount) throws IllegalArgumentException {
        if (origin == null) {
            throw new InvalidParameterException("Transaction origin must be set");
        }
    }

    private void validateTransactionInput(TransactionOrigin origin, String accountId, BigDecimal amount) throws IllegalArgumentException {
        if (origin == null) {
            throw new InvalidParameterException("Transaction origin must be set");
        }
    }

    private Account getIfAccountExistsElseThrowsException(String accountId) {
        Optional<Account> accountOptional = this.accountDAO.getById(accountId);
        accountOptional.orElseThrow(() -> new EntityNotFoundException("Account couldn't be found on our repositories: id[" + accountId + "]"));
        return accountOptional.get();
    }

}
