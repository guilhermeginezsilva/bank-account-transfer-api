package br.com.ginezgit.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private BigDecimal balance;
    private Long lastUpdate;
    private Customer customer;

    public Account() {
    }

    public Account(String id) {
        this.id = id;
    }

    public Account(String id, BigDecimal balance, Long lastUpdate, Customer customer) {
        this.id = id;
        this.balance = balance;
        this.lastUpdate = lastUpdate;
        this.customer = customer;
    }

    public static Account newAccount(Customer customer) {
        return new Account(
                UUID.randomUUID().toString(),
                new BigDecimal(0),
                System.currentTimeMillis(),
                customer
        );
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
