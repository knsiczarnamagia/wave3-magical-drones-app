package dev.jlynx.magicaldrones.auth;

import dev.jlynx.magicaldrones.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> registerAccount(@RequestBody @Valid AccountRegistration body) {
        AccountView accountView = accountService.registerAccount(body);
        return new ResponseEntity<>(accountView, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/username")
    public ResponseEntity<?> updateUsername(
            @PathVariable("id") @Min(1) long accountId,
            @RequestBody @Valid UpdateMyUsernameRequest body
    ) {
        AccountView accountView = accountService.updateUsername(accountId, body);
        return new ResponseEntity<>(accountView, HttpStatus.OK);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable("id") @Min(1) long accountId,
            @RequestBody @Valid UpdateMyPasswordRequest body
    ) {
        AccountView accountView = accountService.updatePassword(accountId, body);
        return new ResponseEntity<>(accountView, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(
            @PathVariable("id") @Min(1) long accountId,
            @RequestBody @Valid DeleteMyAccountRequest body
    ) {
        accountService.deleteAccount(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
