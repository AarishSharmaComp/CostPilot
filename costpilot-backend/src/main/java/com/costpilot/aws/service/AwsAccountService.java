package com.costpilot.aws.service;

import com.costpilot.aws.dto.AwsAccountResponse;
import com.costpilot.aws.dto.CreateAwsAccountRequest;
import com.costpilot.aws.entity.AwsAccount;
import com.costpilot.aws.repository.AwsAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AwsAccountService {

    private final AwsAccountRepository awsAccountRepository;

    public AwsAccountService(AwsAccountRepository awsAccountRepository) {
        this.awsAccountRepository = awsAccountRepository;
    }

    public List<AwsAccountResponse> getAllAccounts() {

        return awsAccountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AwsAccountResponse getAccountById(UUID id) {

        AwsAccount account = awsAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AWS account not found"));

        return toResponse(account);
    }

    public AwsAccountResponse createAccount(CreateAwsAccountRequest request) {

        if (awsAccountRepository.existsByAccountId(request.getAccountId())) {
            throw new RuntimeException("AWS account already exists");
        }

        AwsAccount account = new AwsAccount();

        account.setAccountId(request.getAccountId());
        account.setName(request.getName());
        account.setRoleArn(request.getRoleArn());

        account.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "ACTIVE"
        );

        account.setDefaultRegion(request.getDefaultRegion());

        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        AwsAccount savedAccount = awsAccountRepository.save(account);

        return toResponse(savedAccount);
    }

    public AwsAccountResponse updateAccount(
            UUID id,
            CreateAwsAccountRequest request
    ) {

        AwsAccount account = awsAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AWS account not found"));

        account.setName(request.getName());
        account.setRoleArn(request.getRoleArn());
        account.setDefaultRegion(request.getDefaultRegion());

        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }

        account.setUpdatedAt(LocalDateTime.now());

        AwsAccount updatedAccount = awsAccountRepository.save(account);

        return toResponse(updatedAccount);
    }

    public void deleteAccount(UUID id) {

        AwsAccount account = awsAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AWS account not found"));

        awsAccountRepository.delete(account);
    }

    private AwsAccountResponse toResponse(AwsAccount account) {

        return new AwsAccountResponse(
                account.getId(),
                account.getAccountId(),
                account.getName(),
                account.getRoleArn(),
                account.getStatus(),
                account.getDefaultRegion(),
                account.getLastSyncedAt(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}