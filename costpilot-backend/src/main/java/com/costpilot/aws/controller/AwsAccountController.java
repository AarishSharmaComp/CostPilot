package com.costpilot.aws.controller;

import com.costpilot.aws.dto.AwsAccountResponse;
import com.costpilot.aws.dto.CreateAwsAccountRequest;
import com.costpilot.aws.service.AwsAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws/accounts")
public class AwsAccountController {

    private final AwsAccountService awsAccountService;

    public AwsAccountController(AwsAccountService awsAccountService) {
        this.awsAccountService = awsAccountService;
    }

    @GetMapping
    public List<AwsAccountResponse> getAllAccounts() {
        return awsAccountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AwsAccountResponse getAccountById(
            @PathVariable UUID id
    ) {
        return awsAccountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AwsAccountResponse createAccount(
            @Valid @RequestBody CreateAwsAccountRequest request
    ) {
        return awsAccountService.createAccount(request);
    }

    @PutMapping("/{id}")
    public AwsAccountResponse updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAwsAccountRequest request
    ) {
        return awsAccountService.updateAccount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @PathVariable UUID id
    ) {
        awsAccountService.deleteAccount(id);
    }
}