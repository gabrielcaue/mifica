package com.mifica.repository;

import com.mifica.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe persistência CRUD para Badge.
public interface BadgeRepository extends JpaRepository<Badge, Long> {}
