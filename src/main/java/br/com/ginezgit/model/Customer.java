package br.com.ginezgit.model;

import java.io.Serializable;
import java.util.UUID;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    public Customer() {
    }

    public Customer(String id) {
        this.id = id;
    }

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Customer newCustomer(String name) {
        return new Customer(
                UUID.randomUUID().toString(),
                name
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
