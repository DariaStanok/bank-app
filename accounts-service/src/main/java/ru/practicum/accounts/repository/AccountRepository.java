package ru.practicum.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import ru.practicum.accounts.model.Account;
import ru.practicum.platform.contracts.enums.Currency;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findAllByUserId(Long userId);

	Optional<Account> findByIdAndUserId(Long id, Long userId);

	Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Account> findById(Long id);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.id = :id")
	Optional<Account> findByIdForUpdate(@Param("id") Long id);
}
