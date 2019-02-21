package br.com.ginezgit.service;

import java.util.concurrent.atomic.AtomicLong;

public class RrnService {

    private static final AtomicLong transactionIdAutoGen = new AtomicLong(1);

    public static long getNewRrn() {
        return transactionIdAutoGen.getAndIncrement();
    }

}
