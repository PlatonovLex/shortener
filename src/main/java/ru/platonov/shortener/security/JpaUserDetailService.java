package ru.platonov.shortener.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.platonov.shortener.domain.model.Account;
import ru.platonov.shortener.domain.repository.AccountRepository;

/**
 * JpaUserDetailService.
 * <p>
 *     Implementation of security user detail service.
 *     The logic is this, if there is an entry in the database,
 *     then the user can use the service.
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@Service
public class JpaUserDetailService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Account userAccount = accountRepository.findOne(username);

        if(userAccount == null) {
            throw new UsernameNotFoundException(username);
        }

        return userAccount;
    }
}
