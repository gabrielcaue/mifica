package com.mifica.repository;

import com.mifica.entity.Badge;
import java.util.*;

/**
 * FakeBadgeRepository - Implementação em memória para testes
 */
public class FakeBadgeRepository implements BadgeRepository {
    
    private Map<Long, Badge> database = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public <S extends Badge> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(idCounter++);
        }
        database.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Badge> Iterable<S> saveAll(Iterable<S> entities) {
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
    public Iterable<Badge> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Iterable<Badge> findAllById(Iterable<Long> ids) {
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
}
