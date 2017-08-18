package ru.platonov.shortener.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.platonov.shortener.domain.model.CreateAccountRequest;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;
import ru.platonov.shortener.service.AccountService;
import ru.platonov.shortener.service.LinkService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

/**
 * ApplicationRestController.
 * <p>
 *     Implementation of service REST-api
 * </p>
 *
 * @author Platonov Alexey
 * @since 14.08.2017
 */
@RestController
@RequestMapping(value = "",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class ApplicationRestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private LinkService linkService;

    /**
     * Create new account
     *
     * @param request request with account id
     * @return generated password
     */
    @RequestMapping(value = "account", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateAccountResult createAccount(@RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request.getAccountId());
    }

    /**
     * Register link in the service
     *
     * @param request       link to be registered
     * @param principal     authorized user
     * @return short link
     */
    @RequestMapping(value = "register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RegisterLinkResult register(
            @RequestBody @Valid RegisterLinkRequest request, Principal principal) {
        return linkService.registerLink(request, principal.getName());
    }

    /**
     * Get usage statistics
     *
     * @param accountId   user account to get statistic
     * @return statistic map
     */
    @RequestMapping(value = "statistic/{accountId}", method = RequestMethod.GET)
    public Map<String, Long> statistic(@PathVariable("accountId") String accountId) {
        return linkService.getStatistic(accountId);
    }

}
