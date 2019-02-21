package br.com.ginezgit.model.transaction;

import br.com.ginezgit.model.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransferTransaction extends Transaction {

    private final Account from;
    private final Account to;
    private final BigDecimal amount;

    public TransferTransaction(Long timestamp, TransactionOrigin origin, Account from, Account to, BigDecimal amount) {
        super(timestamp, TransactionType.TRANSFER, origin);
        this.from = from;
        this.to = to;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public TransferTransaction(Long rrn, Long timestamp, TransactionOrigin origin, Account from, Account to, BigDecimal amount) {
        super(rrn, timestamp, TransactionType.TRANSFER, origin);
        this.from = from;
        this.to = to;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static TransferTransaction newTransaction(TransactionOrigin origin, Account from, Account to, BigDecimal amount) {
        return new TransferTransaction(
                System.currentTimeMillis(),
                origin,
                from,
                to,
                amount
        );
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
