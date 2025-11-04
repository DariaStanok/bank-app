package ru.practicum.accounts.config;

import java.math.BigDecimal;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.practicum.accounts.model.Account;
import ru.practicum.accounts.model.UserAccount;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.UserDto;

@Configuration
public class ModelMapperConfig {
	
	@Bean
	ModelMapper modelMapper() {
	        ModelMapper modelMapper = new ModelMapper();
	        modelMapper.getConfiguration()
	                .setFieldMatchingEnabled(true)
	                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
	                .setMatchingStrategy(MatchingStrategies.STRICT);

	        Converter<Account, AccountView> accToView = ctx -> {
	            Account a = ctx.getSource();
	            String masked = mask(a.getExternalAccountId());
	            BigDecimal balance = a.getBalance() == null ? BigDecimal.ZERO : a.getBalance();
	            return new AccountView(
	                    a.getId(),
	                    masked,
	                    a.getCurrency(),
	                    balance
	            );
	        };
	        modelMapper.createTypeMap(Account.class, AccountView.class).setConverter(accToView);
	        modelMapper.createTypeMap(UserAccount.class, UserDto.class);
	        return modelMapper;
		}

		private static String mask(String extId) {
			if (extId == null || extId.length() < 4)
				return "****";
			return "****" + extId.substring(extId.length() - 4);
		}

}
