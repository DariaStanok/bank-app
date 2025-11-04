package ru.practicum.cash.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.practicum.cash.model.CashOperation;
import ru.practicum.platform.contracts.enums.OperationType;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, String> {
	
	Optional<CashOperation> findByIdempotencyKeyAndTypeAndAccountId(String idempotencyKey, OperationType type, Long accountId);
}
