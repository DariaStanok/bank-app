package ru.practicum.cash.model;


import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.OperationType;

@Entity
@Table		(name = "balance_change",
			indexes = {
			@Index(name="idx_change_account_applied", columnList="account_id,applied_at"),
			@Index(name="idx_change_operation", columnList="operation_id")
			})
@Getter
@Setter
public class BalanceChange {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="operation_id", nullable=false)
    private String operationId;                  

    @Column(name="account_id", nullable=false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name="currency", nullable=false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false)
    private OperationType type;                    

    @Column(name="amount", nullable=false)
    private BigDecimal amount;

    @Column(name="applied_at", nullable=false)
    private Instant appliedAt;
}
