package br.com.ginezgit.model.transaction;

import br.com.ginezgit.service.RrnService;

import java.io.Serializable;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long rrn;
    private final Long timestamp;
    private final TransactionType type;
    private final TransactionOrigin origin;
    private TransactionStatus status;

    public Transaction(Long timestamp, TransactionType type, TransactionOrigin origin) {
        this.rrn = RrnService.getNewRrn();
        this.timestamp = timestamp;
        this.type = type;
        this.origin = origin;
        this.status = TransactionStatus.IN_PROGRESS;
    }

    public Transaction(Long rrn, Long timestamp, TransactionType type, TransactionOrigin origin) {
        this.rrn = rrn;
        this.timestamp = timestamp;
        this.type = type;
        this.origin = origin;
        this.status = TransactionStatus.IN_PROGRESS;
    }

    public static Transaction newTransaction(TransactionType type, TransactionOrigin origin) {
        return new Transaction(
                System.currentTimeMillis(),
                type,
                origin
        );
    }

    public Long getRrn() {
        return rrn;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionOrigin getOrigin() {
        return origin;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
