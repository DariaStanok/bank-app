package ru.practicum.transfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.transfer.model.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
	
    Optional<Transfer> findByOperationId(String operationId);

}
