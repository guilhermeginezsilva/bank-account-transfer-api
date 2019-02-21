package br.com.ginezgit.model.transaction;

import br.com.ginezgit.model.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DebitTransaction extends Transaction {

    private final Account account;
    private final BigDecimal amount;

    public DebitTransaction(Long timestamp, TransactionOrigin origin, Account account, BigDecimal amount) {
        super(timestamp, TransactionType.DEBIT, origin);
        this.account = account;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public DebitTransaction(Long rrn, Long timestamp, TransactionOrigin origin, Account account, BigDecimal amount) {
        super(rrn, timestamp, TransactionType.DEBIT, origin);
        this.account = account;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static DebitTransaction newTransaction(TransactionOrigin origin, Account account, BigDecimal amount) {
        return new DebitTransaction(
                System.currentTimeMillis(),
                origin,
                account,
                amount
        );
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
