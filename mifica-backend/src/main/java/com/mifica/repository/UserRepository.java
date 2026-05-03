package com.mifica.repository;

import com.mifica.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe persistência CRUD para User.
public interface UserRepository extends JpaRepository<User, Long> {}
