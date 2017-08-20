package ru.platonov.shortener.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.platonov.shortener.domain.model.CreateAccountRequest;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;
import ru.platonov.shortener.service.AccountService;
import ru.platonov.shortener.service.LinkService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
@Api("Rest-api interface")
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
    @ApiResponses(@ApiResponse(code = 200, message = "Account created successfully"))
    @ApiOperation(value = "Register new user account", response = CreateAccountResult.class)
    @RequestMapping(value = "account", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateAccountResult createAccount(
            @ApiParam("User account id to be registered")
            @RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request.getAccountId());
    }

    /**
     * Register link in the service
     *
     * @param request       link to be registered
     * @param principal     authorized user
     * @return short link
     */
    @ApiResponses({
            @ApiResponse(code = 201, message = "Short link created successfully"),
            @ApiResponse(code = 401, message = "You are not authenticated"),
    })
    @ApiOperation(code = 201, value = "Register new link in service", response = RegisterLinkResult.class,
            authorizations = @Authorization(value = "basicAuth"))
    @RequestMapping(value = "register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterLinkResult> register(
            @RequestBody @Valid RegisterLinkRequest request, Principal principal) throws URISyntaxException {
        RegisterLinkResult registerLinkResult = linkService.registerLink(request, principal.getName());

        return ResponseEntity.created(new URI(registerLinkResult.getShortUrl())).body(registerLinkResult);
    }

    /**
     * Get usage statistics
     *
     * @param accountId   user account to get statistic
     * @return statistic map
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful execution"),
            @ApiResponse(code = 401, message = "You are not authenticated"),
            @ApiResponse(code = 404, message = "Account not found"),
    })
    @ApiOperation(value = "Get usage statistic for account", notes = "Response consists of Map with String and Long params, " +
            "where String - url, and Long - request count", responseContainer = "Map", httpMethod = "GET",
            authorizations = @Authorization(value = "basicAuth"))
    @RequestMapping(value = "statistic/{accountId}", method = RequestMethod.GET)
    public Map<String, Long> statistic(
            @ApiParam("Registered User account id")
            @PathVariable("accountId") String accountId) {
        return linkService.getStatistic(accountId);
    }

}
