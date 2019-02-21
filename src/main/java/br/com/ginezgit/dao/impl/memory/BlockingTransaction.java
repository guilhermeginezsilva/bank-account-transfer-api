package br.com.ginezgit.dao.impl.memory;

import org.apache.log4j.Logger;

import java.util.LinkedList;

public class BlockingTransaction {

    static Logger log = Logger.getLogger(BlockingTransaction.class.getName());

    protected LinkedList<Runnable> transactionRunnables = null;
    private BlockingTransactionStatus status = null;

    public BlockingTransaction() {
        log.debug(Thread.currentThread().getId() + " BlockingTransaction is being created");
        this.transactionRunnables = new LinkedList<Runnable>();
        this.status = BlockingTransactionStatus.CREATED;
    }

    public void start() {
        log.debug(Thread.currentThread().getId() + " BlockingTransaction starting");
        this.canTransactionRun();
        synchronized (this.status) {
            this.status = BlockingTransactionStatus.STARTED;
            log.debug(Thread.currentThread().getId() + " BlockingTransaction started. Status: " + this.status);
        }
    }

    public void execute() {
        log.debug(Thread.currentThread().getId() + " BlockingTransaction execution starting");
        this.canTransactionRun();

        synchronized (this.status) {
            this.status = BlockingTransactionStatus.EXECUTING;
            log.debug(Thread.currentThread().getId() + " BlockingTransaction executing. Status: " + this.status);
        }
        synchronized (this.transactionRunnables) {
            transactionRunnables.forEach(runnable -> runnable.run());
        }
        synchronized (this.status) {
            this.status = BlockingTransactionStatus.ENDED;
            log.debug(Thread.currentThread().getId() + " BlockingTransaction executed. Status: " + this.status);
        }
    }

    public void clear() {
        log.debug(Thread.currentThread().getId() + " BlockingTransaction clear starting");
        this.canTransactionRun();

        synchronized (this.status) {
            this.status = BlockingTransactionStatus.ENDED;
            log.debug(Thread.currentThread().getId() + " BlockingTransaction cleared. Status: " + this.status);
        }
        synchronized (this.transactionRunnables) {
            this.transactionRunnables.clear();
        }
    }

    private void canTransactionRun() {
        synchronized (this.status) {
//            System.out.println(Thread.currentThread().getId() + " - canrun - status: " + this.status + " - id: " + this.toString());
            if (this.status == BlockingTransactionStatus.ENDED) {
                throw new IllegalStateException("This transactions has already ended");
            }
        }
    }

    public void registerTask(Runnable runnable) {
        synchronized (this.transactionRunnables) {
            this.transactionRunnables.addLast(runnable);
        }
    }

    public boolean isRegistering() {
        return this.hasStarted() && !this.isRunning();
    }

    public boolean hasStarted() {
        synchronized (this.status) {
            return this.status != BlockingTransactionStatus.CREATED &&
                    this.status != BlockingTransactionStatus.ENDED;
        }
    }

    public boolean isRunning() {
        synchronized (this.status) {
            return this.status == BlockingTransactionStatus.EXECUTING;
        }
    }

    public BlockingTransactionStatus getStatus() {
        synchronized (this.status) {
            return status;
        }
    }

    public enum BlockingTransactionStatus {
        CREATED, STARTED, EXECUTING, ENDED;
    }

}
