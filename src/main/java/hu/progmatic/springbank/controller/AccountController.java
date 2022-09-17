package hu.progmatic.springbank.controller;

import hu.progmatic.springbank.model.Account;
import hu.progmatic.springbank.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.getAccounts();
        model.addAttribute("accounts", accounts);

        return "accounts";
    }

    @PostMapping("/")
    public String searchAccounts(String name, String number, Model model) {
        List<Account> accounts = accountService.searchAccounts(name, number);
        model.addAttribute("accounts", accounts);

        return "accounts";
    }

    @GetMapping("/new")
    public String newAccount(Model model) {
        Account account = new Account();
        model.addAttribute("account", account);

        return "newaccount";
    }

    @PostMapping("/new")
    public String saveNewAccount(Account account) {
        accountService.saveAccount(account);

        return "redirect:/";
    }

    @GetMapping("/transfer")
    public String transfer() {
        accountService.transfer("Józsi", "Pista", 1000);

        return "redirect:/";
    }

}
