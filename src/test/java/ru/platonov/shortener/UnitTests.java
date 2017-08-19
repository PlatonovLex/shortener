package ru.platonov.shortener;

import org.testng.annotations.Test;
import ru.platonov.shortener.domain.model.Account;
import ru.platonov.shortener.domain.model.CreateAccountRequest;
import ru.platonov.shortener.domain.model.CreateAccountResult;
import ru.platonov.shortener.domain.model.Link;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;

import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * UnitTests.
 * <p>
 *     Need for coverage, because all user-cases were tested in SlowTests
 * </p>
 *
 * @author Platonov Alexey
 * @since 19.08.2017
 */
public class UnitTests {

    @Test
    public void should_objectsAccountsEquals_when_objectsIdsEquals() {
        assertTrue(
                Objects.equals(
                        Account.builder().id("id").build(),
                        Account.builder().id("id").build())
        );
    }

    @Test
    public void should_objectsLinksEquals_when_objectsIdsEquals() {
        assertTrue(
                Objects.equals(
                        Link.builder().id(1L).build(),
                        Link.builder().id(1L).build())
        );
    }

    @Test
    public void should_returnAccount_when_accountSet() {
        Link test = Link.builder().account(Account.builder().id("test").build()).build();

        assertFalse(Objects.isNull(test.getAccount()));
    }

    @Test
    public void should_returnStringRepresentation_when_toStringCalled() {
        CreateAccountRequest request = new CreateAccountRequest();

        assertEquals(request.toString(),
        "CreateAccountRequest(accountId=null)");

        assertEquals(CreateAccountResult.forSuccess().withPassword("123").build().toString(),
                "CreateAccountResult(success=true, description=Your account is opened, password=123)");

        RegisterLinkRequest linkRequest = new RegisterLinkRequest();

        assertEquals(linkRequest.toString(),
                "RegisterLinkRequest(url=null, redirectType=302)");

        assertEquals(RegisterLinkResult.builder().shortUrl("ERER").build().toString(),
                "RegisterLinkResult(shortUrl=ERER)");
    }

}
