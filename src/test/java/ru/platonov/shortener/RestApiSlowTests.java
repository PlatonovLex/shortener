package ru.platonov.shortener;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.platonov.shortener.config.DomainConfig;
import ru.platonov.shortener.domain.model.CreateAccountRequest;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.model.Link;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;
import ru.platonov.shortener.domain.repository.LinkRepository;
import ru.platonov.shortener.model.ErrorResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * RestApiSlowTests
 * <p>
 *      Check rest-api logic
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@SpringBootTest(webEnvironment = DEFINED_PORT)
@Test(groups = "RestApiSlowTests")
public class RestApiSlowTests extends AbstractTestNGSpringContextTests {

    private static final String URL_ACCOUNT = "http://localhost:8080:/account";
    private static final String URL_REGISTER = "http://localhost:8080:/register";
    private static final String URL_STATISTIC = "http://localhost:8080:/statistic/";
    private static final String REDIRECT_GOOGLE = "http://google.com";
    private static final String REDIRECT_JETBRAINS = "https://www.jetbrains.com/";
    private static CloseableHttpClient httpClient;
    private static final String TEST_ACCOUNT_ID = "myAccount";
    private String accountPassword = "";
    private String registered301ShortUrl = "";
    private String registered302ShortUrl = "";

    @Autowired
    private DomainConfig domainConfig;

    @BeforeClass
    public static void init() {
        httpClient = HttpClientBuilder.create()
                .build();
    }

    @AfterClass
    public static void destroy() throws IOException {
        httpClient.close();
    }

    @AfterMethod
    public void clean() {
        Mockito.reset(linkRepositoryMock);
    }

    @Test
    public void should_returnPassword_when_registerAccount() throws IOException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        createAccountRequest.setAccountId(TEST_ACCOUNT_ID);

        HttpMethodExecutor.MethodResponse<CreateAccountResult> methodResponse =
                new HttpMethodExecutor(httpClient)
                        .executePostJson(
                                URL_ACCOUNT, createAccountRequest, CreateAccountResult.class);

        assertTrue(methodResponse.isSuccess());
        String password = methodResponse.getResponse().getPassword();

        assertTrue(StringUtils.isNotBlank(password));
        accountPassword = password;

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnValidationError_when_registerLinkWithoutUrl() throws IOException {
        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setRedirectType(301);

        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse = HttpMethodExecutor.builder()
                .basicAuthEnabled(true)
                .userName(TEST_ACCOUNT_ID)
                .password(accountPassword)
                .httpClient(httpClient)
                .build()
                .executePostJson(URL_REGISTER,
                        registerLinkRequest, RegisterLinkResult.class);

        assertFalse(methodResponse.isSuccess());
        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getStatus(), Integer.valueOf(HttpStatus.SC_BAD_REQUEST));
        assertEquals(errorResponse.getErrors().size(), 1);
        assertEquals(errorResponse.getErrors().get(0).getField(), "url");
        assertEquals(errorResponse.getErrors().get(0).getCode(), "NotBlank");

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnValidationError_when_registerLinkWithRedirectTypeOverMaxValue() throws IOException {
        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setRedirectType(305);
        registerLinkRequest.setUrl(REDIRECT_GOOGLE);

        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse = HttpMethodExecutor.builder()
                .basicAuthEnabled(true)
                .userName(TEST_ACCOUNT_ID)
                .password(accountPassword)
                .httpClient(httpClient)
                .build()
                .executePostJson(URL_REGISTER,
                        registerLinkRequest, RegisterLinkResult.class);

        assertFalse(methodResponse.isSuccess());
        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getStatus(), Integer.valueOf(HttpStatus.SC_BAD_REQUEST));
        assertEquals(errorResponse.getErrors().size(), 1);
        assertEquals(errorResponse.getErrors().get(0).getField(), "redirectType");
        assertEquals(errorResponse.getErrors().get(0).getCode(), "Max");

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnShortLink_when_registerLinkWithRedirect() throws IOException {
        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse =
                registerLinkWithRedirect();

        assertTrue(methodResponse.isSuccess());
        RegisterLinkResult response = methodResponse.getResponse();

        checkResponse(response);

        registered301ShortUrl = response.getShortUrl();
    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnSameShortLink_when_registerLinkWithRedirectTwice() throws IOException {
        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse =
                registerLinkWithRedirect();

        assertTrue(methodResponse.isSuccess());
        RegisterLinkResult response = methodResponse.getResponse();

        checkResponse(response);

        HttpMethodExecutor.MethodResponse<RegisterLinkResult> secondMethodResponse =
                registerLinkWithRedirect();

        assertTrue(secondMethodResponse.isSuccess());
        RegisterLinkResult secondResponse = methodResponse.getResponse();

        checkResponse(secondResponse);

        assertEquals(response.getShortUrl(), secondResponse.getShortUrl());
    }

    private void checkResponse(RegisterLinkResult response) throws MalformedURLException {
        assertNotNull(response);
        assertNotNull(response.getShortUrl());
        assertEquals(new URL(response.getShortUrl()).getHost(), domainConfig.getName());
    }

    private HttpMethodExecutor.MethodResponse<RegisterLinkResult> registerLinkWithRedirect() throws IOException {
        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setRedirectType(HttpStatus.SC_MOVED_PERMANENTLY);
        registerLinkRequest.setUrl(REDIRECT_GOOGLE);

        return HttpMethodExecutor.builder()
                .basicAuthEnabled(true)
                .userName(TEST_ACCOUNT_ID)
                .password(accountPassword)
                .httpClient(httpClient)
                .build()
                .executePostJson(URL_REGISTER,
                        registerLinkRequest, RegisterLinkResult.class);

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnShortLink_when_registerLinkWithDefaultRedirect() throws IOException {
        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setUrl(REDIRECT_JETBRAINS);

        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse = HttpMethodExecutor.builder()
                .basicAuthEnabled(true)
                .userName(TEST_ACCOUNT_ID)
                .password(accountPassword)
                .httpClient(httpClient)
                .build()
                .executePostJson(URL_REGISTER,
                        registerLinkRequest, RegisterLinkResult.class);

        assertTrue(methodResponse.isSuccess());
        RegisterLinkResult response = methodResponse.getResponse();

        assertNotNull(response);
        String shortUrl = response.getShortUrl();

        assertNotNull(shortUrl);
        assertEquals(new URL(shortUrl).getHost(), domainConfig.getName());
        registered302ShortUrl = shortUrl;
    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnValidationError_when_registerLinkWithRedirectTypeLessMinValue() throws IOException {
        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setRedirectType(300);
        registerLinkRequest.setUrl(REDIRECT_GOOGLE);

        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse = HttpMethodExecutor.builder()
                .basicAuthEnabled(true)
                .userName(TEST_ACCOUNT_ID)
                .password(accountPassword)
                .httpClient(httpClient)
                .build()
                .executePostJson(URL_REGISTER,
                        registerLinkRequest, RegisterLinkResult.class);

        assertFalse(methodResponse.isSuccess());
        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getStatus(), Integer.valueOf(HttpStatus.SC_BAD_REQUEST));
        assertEquals(errorResponse.getErrors().size(), 1);
        assertEquals(errorResponse.getErrors().get(0).getField(), "redirectType");
        assertEquals(errorResponse.getErrors().get(0).getCode(), "Min");

    }

    @Test
    public void should_returnValidationError_when_registerAccountWithEmptyId() throws IOException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        createAccountRequest.setAccountId("");

        HttpMethodExecutor.MethodResponse<CreateAccountResult> methodResponse =
                new HttpMethodExecutor(httpClient)
                        .executePostJson(
                                URL_ACCOUNT, createAccountRequest, CreateAccountResult.class);

        assertFalse(methodResponse.isSuccess());
        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getStatus(), Integer.valueOf(HttpStatus.SC_BAD_REQUEST));
        assertEquals(errorResponse.getErrors().size(), 1);
        assertEquals(errorResponse.getErrors().get(0).getField(), "accountId");
        assertEquals(errorResponse.getErrors().get(0).getCode(), "NotBlank");

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnDuplicateResponse_when_registerAccountTwice() throws IOException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        createAccountRequest.setAccountId(TEST_ACCOUNT_ID + ' ');

        HttpMethodExecutor.MethodResponse<CreateAccountResult> methodResponse =
                new HttpMethodExecutor(httpClient)
                        .executePostJson(
                                URL_ACCOUNT, createAccountRequest, CreateAccountResult.class);

        assertTrue(methodResponse.isSuccess());
        CreateAccountResult response = methodResponse.getResponse();

        assertFalse(response.getSuccess());
        assertEquals(response.getDescription(), "account with that ID already exists");

    }

    @Test(dependsOnMethods = "should_returnPassword_when_registerAccount")
    public void should_returnEmptyMap_when_getStatisticWithWrongAccountId() throws IOException {
        HttpMethodExecutor.MethodResponse<HashMap> methodResponse =
                HttpMethodExecutor.builder()
                        .httpClient(httpClient)
                        .basicAuthEnabled(true)
                        .userName(TEST_ACCOUNT_ID)
                        .password(accountPassword)
                        .build()
                        .executeGetMethod(
                                URL_STATISTIC + "notExists", HashMap.class);

        assertFalse(methodResponse.isSuccess());
        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertEquals(errorResponse.getStatus().intValue(), HttpStatus.SC_NOT_FOUND);
        assertEquals(errorResponse.getError(), "Not Found");

    }

    @Test(dependsOnMethods = {"should_returnPassword_when_registerAccount",
            "should_returnShortLink_when_registerLinkWithRedirect"})
    public void should_redirect301_when_linkRegistered() throws IOException {
        HttpUriRequest request = new HttpGet(registered301ShortUrl);

        try (CloseableHttpClient build = HttpClientBuilder.create()
                .disableRedirectHandling()
                .build(); CloseableHttpResponse execute = build.execute(request)) {
            assertEquals(execute.getStatusLine().getStatusCode(), HttpStatus.SC_MOVED_PERMANENTLY);
            assertEquals(execute.getFirstHeader("Location").getValue(), REDIRECT_GOOGLE);
        }

    }

    @Test(invocationCount = 2,
            dependsOnMethods = {"should_returnPassword_when_registerAccount",
            "should_returnShortLink_when_registerLinkWithDefaultRedirect"})
    public void should_redirect302_when_linkRegistered() throws IOException {
        HttpUriRequest request = new HttpGet(registered302ShortUrl);

        try (CloseableHttpClient build = HttpClientBuilder.create()
                .disableRedirectHandling()
                .build(); CloseableHttpResponse execute = build.execute(request)) {
            assertEquals(execute.getStatusLine().getStatusCode(), HttpStatus.SC_MOVED_TEMPORARILY);
            assertEquals(execute.getFirstHeader("Location").getValue(), REDIRECT_JETBRAINS);
        }

    }

    @Test(dependsOnMethods = {"should_returnPassword_when_registerAccount",
            "should_returnShortLink_when_registerLinkWithDefaultRedirect",
            "should_redirect302_when_linkRegistered",
            "should_redirect301_when_linkRegistered",
            "should_returnShortLink_when_registerLinkWithRedirect"
    })
    public void should_returnEmptyMap_when_getStatistic() throws IOException {
        HttpMethodExecutor.MethodResponse<HashMap> methodResponse =
                HttpMethodExecutor.builder()
                        .httpClient(httpClient)
                        .basicAuthEnabled(true)
                        .userName(TEST_ACCOUNT_ID)
                        .password(accountPassword)
                        .build()
                        .executeGetMethod(
                                URL_STATISTIC + TEST_ACCOUNT_ID, HashMap.class);

        assertTrue(methodResponse.isSuccess());
        Map<String, Integer> response = methodResponse.getResponse();

        assertEquals(response.get(REDIRECT_GOOGLE).intValue(), 1);
        assertEquals(response.get(REDIRECT_JETBRAINS).intValue(), 2);
    }

    @Autowired
    private LinkRepository linkRepositoryMock;

    @Test(dependsOnMethods = {"should_returnPassword_when_registerAccount",
            "should_returnShortLink_when_registerLinkWithRedirect"})
    public void should_attemptMaximumRetries_when_optimisticException() throws IOException {
        Mockito.doThrow(new ObjectOptimisticLockingFailureException("Link.class", "hey"))
                .when(linkRepositoryMock).save(Matchers.any(Link.class));

        HttpMethodExecutor.MethodResponse<Void> methodResponse =
                HttpMethodExecutor.builder()
                        .httpClient(httpClient)
                        .basicAuthEnabled(true)
                        .userName(TEST_ACCOUNT_ID)
                        .password(accountPassword)
                        .build()
                        .executeGetMethod(
                                registered301ShortUrl, Void.class);

        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertEquals(errorResponse.getStatus().intValue(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertEquals(errorResponse.getException(), "org.springframework.orm.ObjectOptimisticLockingFailureException");
    }

    @Test(dependsOnMethods = {"should_returnPassword_when_registerAccount",
            "should_returnShortLink_when_registerLinkWithRedirect"})
    public void should_interceptorRetrunException_when_repositoryThrowExcpetion() throws IOException {
        Mockito.doThrow(new IllegalArgumentException("test"))
                .when(linkRepositoryMock).save(Matchers.any(Link.class));

        HttpMethodExecutor.MethodResponse<Void> methodResponse =
                HttpMethodExecutor.builder()
                        .httpClient(httpClient)
                        .basicAuthEnabled(true)
                        .userName(TEST_ACCOUNT_ID)
                        .password(accountPassword)
                        .build()
                        .executeGetMethod(
                                registered301ShortUrl, Void.class);

        ErrorResponse errorResponse = methodResponse.getErrorResponse();

        assertEquals(errorResponse.getStatus().intValue(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertEquals(errorResponse.getException(), "org.springframework.dao.InvalidDataAccessApiUsageException");
    }

}
