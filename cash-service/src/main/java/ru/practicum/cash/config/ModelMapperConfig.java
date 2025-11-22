package ru.practicum.cash.config;

import java.time.Instant;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.practicum.cash.model.CashOperation;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration()
          .setFieldMatchingEnabled(true)
          .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
          .setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<CashOperation, CashOperationViewDto> opToView = ctx -> {
            CashOperation src = ctx.getSource();
            Instant at = src.getCompletedAt() != null ? src.getCompletedAt() : src.getCreatedAt();
            return CashOperationViewDto.builder()
                    .operationId(src.getId())
                    .accountId(src.getAccountId())
                    .currency(src.getCurrency())
                    .amount(src.getAmount())
                    .newBalance(src.getNewBalance())
                    .status(src.getStatus())
                    .at(at)
                    .build();
        };

        mm.createTypeMap(CashOperation.class, CashOperationViewDto.class)
          .setConverter(opToView);

        return mm;
    }
}
