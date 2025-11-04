package ru.practicum.cash.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.platform.contracts.enums.CashOpStatus;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.OperationType;

@Entity
@Table  (name = "cash_operation",
		indexes = @Index(
		    name = "idx_cash_op_account_created",
		    columnList = "account_id,created_at"   
		),
		uniqueConstraints = @UniqueConstraint(
		    name = "uq_cash_op_idem_acc_type",
		    columnNames = {"idempotency_key","account_id","type"}
		))
@Getter
@Setter
public class CashOperation {
	@Id
    @Column(name="id", length=64, nullable=false)
    private String id;                          

    @Enumerated(EnumType.STRING)
    @Column(name="type", length=16, nullable=false)
    private OperationType type;                   

    @Column(name="account_id", nullable=false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name="currency", length=8, nullable=false)
    private Currency currency;

    @Column(name="amount", precision=19, scale=4, nullable=false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private CashOpStatus status;                

    @Column(name="idempotency_key", nullable=false)
    private String idempotencyKey;             

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @Column(name="completed_at")
    private Instant completedAt;

    @Column(name="new_balance")
    private BigDecimal newBalance;                         
}
