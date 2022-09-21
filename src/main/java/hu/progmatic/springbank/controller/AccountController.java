package hu.progmatic.springbank.controller;

import hu.progmatic.springbank.dto.AccountDTO;
import hu.progmatic.springbank.model.Account;
import hu.progmatic.springbank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    // https://www.baeldung.com/spring-boot-thymeleaf-image-upload
    // https://www.baeldung.com/spring-boot-bean-validation
    @PostMapping("/new")
    public String saveNewAccount(@Valid AccountDTO accountDTO, @RequestParam("photo") MultipartFile photo) {
        try {
            Account account = new Account(accountDTO.getName(), accountDTO.getNumber(), accountDTO.isPremium());
            account.setPhotoName(photo.getOriginalFilename());
            account.setPhotoType(photo.getContentType());
            account.setPhotoData(photo.getBytes());
            accountService.saveAccount(account);

            return "redirect:/";
        } catch (IOException e) {
            e.printStackTrace();

            return "redirect:/new";
        }
    }

    // https://www.baeldung.com/spring-controller-return-image-file
    // https://www.baeldung.com/spring-response-status-exception
    @GetMapping(value = "/photo/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] downloadPhoto(@PathVariable long id) {
        Account account = accountService.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.")
        );

        return account.getPhotoData();
    }

    @GetMapping("/transfer")
    public String transfer() {
        accountService.transfer("Józsi", "Pista", 1000);

        return "redirect:/";
    }

    @GetMapping("/owner/{name}")
    public String getByOwnerName(@PathVariable String name, Model model) {
        List<Account> accounts = accountService.getByOwnerName(name);
        model.addAttribute("accounts", accounts);

        return "accounts";
    }

}
