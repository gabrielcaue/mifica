package com.mifica.repository;

import com.mifica.entity.Badge;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.*;
import java.util.function.Function;

/**
 * FakeBadgeRepository - Implementação em memória para testes
 */
public class FakeBadgeRepository implements BadgeRepository {
    
    private Map<Long, Badge> database = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public <S extends Badge> S save(S entity) {
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
    public <S extends Badge> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public Optional<Badge> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }

    @Override
    public List<Badge> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<Badge> findAll(Sort sort) {
        return new ArrayList<>(database.values());
    }

    @Override
    public <S extends Badge> List<S> findAll(Example<S> example) {
        return new ArrayList<>();
    }

    @Override
    public <S extends Badge> List<S> findAll(Example<S> example, Sort sort) {
        // Simple implementation returning empty list
        // In real scenarios, would filter based on example
        return new ArrayList<>();
    }

    @Override
    public Badge getReferenceById(Long id) {
        return database.get(id);
    }

    @Override
    public Badge getById(Long id) {
        return database.get(id);
    }

    @Override
    public List<Badge> findAllById(Iterable<Long> ids) {
        List<Badge> result = new ArrayList<>();
        for (Long id : ids) {
            Badge badge = database.get(id);
            if (badge != null) {
                result.add(badge);
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
    public void delete(Badge entity) {
        database.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (Long id : ids) {
            database.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Badge> entities) {
        for (Badge entity : entities) {
            database.remove(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        database.clear();
    }

    /**
     * Métodos customizados para testes
     */
    public List<Badge> findByUserId(Long userId) {
        List<Badge> result = new ArrayList<>();
        for (Badge badge : database.values()) {
            if (badge.getUserId().equals(userId)) {
                result.add(badge);
            }
        }
        return result;
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
    public <S extends Badge> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
    }

    @Override
    public <S extends Badge> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("Unimplemented method 'saveAllAndFlush'");
    }

    @Override
    public void deleteAllInBatch(Iterable<Badge> entities) {
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
    public Badge getOne(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'getOne'");
    }

    @Override
    public Page<Badge> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends Badge> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public <S extends Badge> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends Badge> long count(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'count'");
    }

    @Override
    public <S extends Badge> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public <S extends Badge, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Unimplemented method 'findBy'");
    }
}
