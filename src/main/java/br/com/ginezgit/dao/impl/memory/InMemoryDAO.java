package br.com.ginezgit.dao.impl.memory;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public abstract class InMemoryDAO<ID, T> implements CrudDAO<ID, T> {

    static Logger log = Logger.getLogger(InMemoryDAO.class.getName());

    protected HashMap<ID, T> entitiesRepository = new HashMap<ID, T>();
    protected BlockingTransaction transaction = new BlockingTransaction();

    final ReentrantLock lock = new ReentrantLock();

    protected InMemoryDAO() {
    }

    public void beginTransaction() {
        log.debug(Thread.currentThread().getId() + " Beginning InMemoryDAO transaction");
        this.lock.lock();
        log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction lock acquired");
        this.transaction = new BlockingTransaction();
        this.transaction.start();
    }

    public void commitTransaction() {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction committing");
            this.transaction.execute();
            log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction committed");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction lock unlocked");
            }
        }
    }

    public void rollbackTransaction() {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction roll backing");
            this.transaction.clear();
            log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction roll backed");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                log.debug(Thread.currentThread().getId() + " InMemoryDAO transaction lock unlocked");
            }
        }
    }

    @Override
    public Optional<T> getById(ID id) {
        log.debug(Thread.currentThread().getId() + " InMemoryDAO getById("+id+")");
        return Optional.ofNullable((T)
                usingExceptionHandler(() ->
                        usingResourceControlAccess(() ->
                                entitiesRepository.get(id)
                        )));
    }

    @Override
    public List<T> getAll() {
        log.debug(Thread.currentThread().getId() + " InMemoryDAO getAll()");
        List<T> entities = (List<T>)
                usingExceptionHandler(() ->
                        usingResourceControlAccess(() ->
                                new ArrayList(entitiesRepository.values())
                        ));
        return entities;
    }

    @Override
    public void insert(ID id, T entity) {
        log.debug(Thread.currentThread().getId() + " InMemoryDAO insert("+id+", "+ entity +")");
        usingExceptionHandler(() ->
                usingResourceControlAccess(() -> {
                            if (this.transaction.isRegistering()) {
                                this.transaction.registerTask(() -> insert(id, entity));
                            } else {
                                entitiesRepository.put(id, entity);
                            }
                        }
                ));
    }

    @Override
    public void update(ID id, T entity) {
        log.debug(Thread.currentThread().getId() + " InMemoryDAO update("+id+", "+ entity +")");
        usingExceptionHandler(() ->
                usingResourceControlAccess(() -> {
                            if (this.transaction.isRegistering()) {
                                this.transaction.registerTask(() -> update(id, entity));
                            } else {
                                Optional<T> foundEntityOptional = getById(id);

                                foundEntityOptional.orElseThrow(() -> new EntityNotFoundException(entity.getClass().getName() + " couldn't be found on our repositories: id[" + id + "]"));
                                entitiesRepository.put(id, entity);
                            }
                        }
                ));
    }

    private Object usingExceptionHandler(Supplier<Object> supplier) {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO using Exception Handler");
            return supplier.get();
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private void usingExceptionHandler(Runnable runnable) {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO using Exception Handler");
            runnable.run();
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }


    private Object usingResourceControlAccess(Supplier<Object> supplier) {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO using Resource Control Access");
            this.lock.lock();
            log.debug(Thread.currentThread().getId() + " InMemoryDAO lock acquired");
            return supplier.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                log.debug(Thread.currentThread().getId() + " InMemoryDAO lock unlocked");
            }
        }
    }

    private void usingResourceControlAccess(Runnable runnable) {
        try {
            log.debug(Thread.currentThread().getId() + " InMemoryDAO using Resource Control Access");
            this.lock.lock();
            log.debug(Thread.currentThread().getId() + " InMemoryDAO lock acquired");
            runnable.run();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                log.debug(Thread.currentThread().getId() + " InMemoryDAO lock unlocked");
            }
        }

    }
}
