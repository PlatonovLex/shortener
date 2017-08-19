package ru.platonov.shortener.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.platonov.shortener.domain.model.Account;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.repository.AccountRepository;

/**
 * AccountService.
 * <p>
 *     Service implements operations with links
 * </p>
 *
 * @author Platonov Alexey
 * @since 14.08.2017
 */
@Service
public class AccountService {

    private static final int MAX_CHAR_LENGTH = 8;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Create account in the system
     *
     * @param accountId the ID with which to create the account
     * @return command result
     */
    public CreateAccountResult createAccount(String accountId) {
        String accountIdTrimmed = accountId.trim();

        if (accountRepository.exists(accountIdTrimmed)) {

            return CreateAccountResult
                    .forError()
                    .withDescription("account with that ID already exists")
                    .build();
        }

        Account account = Account.builder()
                .id(accountIdTrimmed)
                .password(RandomStringUtils.randomAlphanumeric(MAX_CHAR_LENGTH))
                .build();

        Account savedAccount = accountRepository.save(account);

        return CreateAccountResult.forSuccess()
                .withPassword(savedAccount.getPassword())
                .build();
    }

}
