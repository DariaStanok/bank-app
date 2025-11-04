package ru.practicum.frontui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.service.FrontUiService;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.enums.Currency;

@Controller
@RequestMapping("/ui/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final FrontUiService front;

	@GetMapping
	public String list(@RequestParam("userId") Long userId, Model model) {
		model.addAttribute("userId", userId);
		model.addAttribute("accounts", front.getAccounts(userId));
		return "accounts";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam("userId") Long userId, Model model) {
		model.addAttribute("userId", userId);
		model.addAttribute("form", new UpdateExternalAccount("", Currency.RUB));
		model.addAttribute("currencies", Currency.values());
		return "account-new";
	}

	@PostMapping
	public RedirectView create(@RequestParam("userId") Long userId,
			@ModelAttribute("form") UpdateExternalAccount form) {
		front.createAccount(userId, form);
		return new RedirectView("/ui/accounts?userId=" + userId);
	}

	@GetMapping("/{accountId}/edit")
	public String editForm(@RequestParam("userId") Long userId, @PathVariable Long accountId, Model model) {
		AccountView account = front.getAccount(userId, accountId); 
		UpdateExternalAccount form = new UpdateExternalAccount(account.externalAccountId(), account.currency());
		model.addAttribute("userId", userId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("form", form);
		model.addAttribute("currencies", Currency.values());
		return "account-edit";
	}

	@PostMapping("/{accountId}/edit")
	public RedirectView update(@RequestParam("userId") Long userId, @PathVariable Long accountId,
			@ModelAttribute("form") UpdateExternalAccount form) {
		front.updateAccount(userId, accountId, form);
		return new RedirectView("/ui/accounts?userId=" + userId);
	}

	@PostMapping("/{accountId}/delete")
	public RedirectView delete(@RequestParam("userId") Long userId, @PathVariable Long accountId) {
		front.deleteAccount(userId, accountId);
		return new RedirectView("/ui/accounts?userId=" + userId);
	}
}
