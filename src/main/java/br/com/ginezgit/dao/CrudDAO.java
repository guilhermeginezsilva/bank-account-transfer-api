package br.com.ginezgit.dao;

import br.com.ginezgit.dao.impl.memory.BlockingTransaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<ID, T> {

    public Optional<T> getById(ID id);

    public List<T> getAll();

    public void insert(ID id, T entity);

    public void update(ID id, T entity);

    public void beginTransaction();

    public void commitTransaction();

    public void rollbackTransaction();
}
