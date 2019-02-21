package br.com.ginezgit.model.transaction;

import br.com.ginezgit.model.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditTransaction extends Transaction {

    private final Account account;
    private final BigDecimal amount;

    public CreditTransaction(Long timestamp, TransactionOrigin origin, Account account, BigDecimal amount) {
        super(timestamp, TransactionType.CREDIT, origin);
        this.account = account;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public CreditTransaction(Long rrn, Long timestamp, TransactionOrigin origin, Account account, BigDecimal amount) {
        super(rrn, timestamp, TransactionType.CREDIT, origin);
        this.account = account;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static CreditTransaction newTransaction(TransactionOrigin origin, Account account, BigDecimal amount) {
        return new CreditTransaction(
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
