package ru.practicum.accounts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.practicum.accounts.model.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
	
    Optional<UserAccount> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
