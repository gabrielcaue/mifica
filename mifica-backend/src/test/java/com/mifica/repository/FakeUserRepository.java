package com.mifica.repository;

import com.mifica.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.*;
import java.util.function.Function;

/**
 * FakeUserRepository - Implementação em memória do UserRepository
 * Útil para testes de integração sem banco de dados real
 */
@SuppressWarnings("null")
public class FakeUserRepository implements UserRepository {
    
    private Map<Long, User> database = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public <S extends User> S save(S entity) {
        // Note: In this fake implementation, we simulate ID assignment
        // In real JPA, @GeneratedValue(IDENTITY) is handled by the database
        
        // ✅ CRITICAL: Always assign ID if null
        if (entity.getId() == null) {
            try {
                entity.getClass().getDeclaredMethod("setId", Long.class).invoke(entity, idCounter++);
            } catch (Exception e) {
                // Fallback: se setId não existir, tenta com reflexão alternativa
                try {
                    var field = entity.getClass().getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, idCounter++);
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot assign ID to entity", ex);
                }
            }
        }
        
        // ✅ Validate ID is not null before storing
        if (entity.getId() == null) {
            throw new RuntimeException("Entity must have a non-null ID after save");
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
    public User getById(Long id) {
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

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Unimplemented method 'flush'");
    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("Unimplemented method 'saveAllAndFlush'");
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllByIdInBatch'");
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
    }

    @Override
    public User getOne(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'getOne'");
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'count'");
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Unimplemented method 'findBy'");
    }
}
