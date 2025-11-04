package ru.practicum.transfer.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.TransferStatus;

@Entity
@Table(name = "transfers", uniqueConstraints = {
		@UniqueConstraint(name = "uk_transfers_operation_id", columnNames = { "operationId" }) 
		})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String operationId;

	@Column(nullable = false)
	private Long fromAccountId;

	@Column(nullable = false)
	private Long toAccountId;

	@Column(nullable = false)
	private BigDecimal amount; 

	@Enumerated(EnumType.STRING)
	@Column
	private Currency currency; 

	@Column
	private BigDecimal rate; 

	@Column
	private BigDecimal debitAmount; 

	@Column
	private BigDecimal creditAmount; 

	@Enumerated(EnumType.STRING)
	@Column
	private TransferStatus status;
}
