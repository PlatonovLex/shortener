package ru.platonov.shortener;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.platonov.shortener.config.DomainConfig;
import ru.platonov.shortener.domain.model.CreateAccountRequest;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * SecuritySlowTest.
 * <p>
 *      Check security logic
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class SecuritySlowTest extends AbstractTestNGSpringContextTests {
    public static final int CHARS_COUNT = 6;
    private static CloseableHttpClient httpClient;

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

    @Test
    public void should_returnAuthorizationError_when_passWordIncorrect() throws IOException {
        checkIncorrectState(
                executeMethod(httpMethodExecutor -> {
                    httpMethodExecutor.setBasicAuthEnabled(true);
                    httpMethodExecutor.setPassword("incorrectPassword");
                }));
    }

    @Test
    public void should_returnAuthorizationError_when_userIncorrect() throws IOException {
        checkIncorrectState(
                executeMethod(httpMethodExecutor -> {
                    httpMethodExecutor.setBasicAuthEnabled(true);
                    httpMethodExecutor.setUserName("incorrectLogin");
                }));
    }

    private static void checkIncorrectState(HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse) {
        assertFalse(methodResponse.isSuccess());
        assertEquals(methodResponse.getErrorResponse().getStatus(), Integer.valueOf(HttpStatus.SC_UNAUTHORIZED));
    }

    private HttpMethodExecutor.MethodResponse<RegisterLinkResult> executeMethod(
            Consumer<HttpMethodExecutor> setUpBasicAuthorization) throws IOException {
        String accountId = RandomStringUtils.randomAlphabetic(CHARS_COUNT);
        String accountPassword = getNewAccountPassword(accountId);

        RegisterLinkRequest registerLinkRequest = new RegisterLinkRequest();

        registerLinkRequest.setUrl("yandex.ru/test");
        HttpMethodExecutor httpMethodExecutor = new HttpMethodExecutor(httpClient);

        httpMethodExecutor.setBasicAuthEnabled(true);
        httpMethodExecutor.setUserName(accountId);
        httpMethodExecutor.setPassword(accountPassword);

        setUpBasicAuthorization.accept(httpMethodExecutor);

        return httpMethodExecutor.executePostJson("http://localhost:8080:/register",
                        registerLinkRequest, RegisterLinkResult.class);
    }

    @Test
    public void should_successExecute_when_correctBasicHeaders() throws IOException {
        HttpMethodExecutor.MethodResponse<RegisterLinkResult> methodResponse =
                executeMethod(httpMethodExecutor -> { });

        assertTrue(methodResponse.isSuccess());
        RegisterLinkResult response = methodResponse.getResponse();

        assertNotNull(response);
        assertNotNull(response.getShortUrl());
        assertEquals(new URL(response.getShortUrl()).getHost(), domainConfig.getName());

    }

    private String getNewAccountPassword(String accountId) throws IOException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        createAccountRequest.setAccountId(accountId);
        HttpMethodExecutor httpMethodExecutor = new HttpMethodExecutor(httpClient);

        HttpMethodExecutor.MethodResponse<CreateAccountResult> methodResponse =
                httpMethodExecutor.executePostJson("http://localhost:8080:/account",
                        createAccountRequest, CreateAccountResult.class);

        assertTrue(methodResponse.isSuccess());
        assertTrue(methodResponse.getResponse().getSuccess());
        String password = methodResponse.getResponse().getPassword();

        assertTrue(StringUtils.isNotBlank(password));

        return password;
    }
}
