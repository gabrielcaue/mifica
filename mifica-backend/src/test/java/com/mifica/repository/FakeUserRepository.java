package com.mifica.repository;

import com.mifica.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import java.util.*;

/**
 * FakeUserRepository - Implementação em memória do UserRepository
 * Útil para testes de integração sem banco de dados real
 */
public class FakeUserRepository implements UserRepository {
    
    private Map<Long, User> database = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public <S extends User> S save(S entity) {
        // Note: In this fake implementation, we simulate ID assignment
        // In real JPA, @GeneratedValue(IDENTITY) is handled by the database
        if (entity.getId() == null) {
            try {
                entity.getClass().getDeclaredMethod("setId", Long.class).invoke(entity, idCounter++);
            } catch (Exception e) {
                // If setId is not available, entity comes with ID already set
            }
        }
        database.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<User> findAll(Sort sort) {
        return new ArrayList<>(database.values());
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return new ArrayList<>();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        // Simple implementation returning empty list
        // In real scenarios, would filter based on example
        return new ArrayList<>();
    }

    @Override
    public User getReferenceById(Long id) {
        return database.get(id);
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        List<User> result = new ArrayList<>();
        for (Long id : ids) {
            User user = database.get(id);
            if (user != null) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public long count() {
        return database.size();
    }

    @Override
    public void deleteById(Long id) {
        database.remove(id);
    }

    @Override
    public void delete(User entity) {
        database.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (Long id : ids) {
            database.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        for (User entity : entities) {
            database.remove(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        database.clear();
    }

    public void reset() {
        database.clear();
        idCounter = 1L;
    }
}
