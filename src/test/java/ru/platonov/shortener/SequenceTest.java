package ru.platonov.shortener;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.platonov.shortener.domain.model.Account;
import ru.platonov.shortener.domain.model.Link;
import ru.platonov.shortener.domain.repository.AccountRepository;
import ru.platonov.shortener.domain.repository.LinkRepository;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.testng.Assert.assertEquals;

/**
 * SequenceTest.
 * <p>
 *     Testing sequence generation
 * </p>
 *
 * @author Platonov Alexey
 * @since 21.08.2017
 */
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class SequenceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void keyGeneration() {

        Account account = accountRepository.save(Account.builder()
                .id("test")
                .password("xdfd")
                .build());

        Link save = linkRepository.save(Link.builder()
                .account(account)
                .url("http://" + RandomStringUtils.randomAlphabetic(6) + ".ru")
                .build());

        save.setRedirectsAmount(save.getRedirectsAmount() + 1L);

        Link amountIncreased = linkRepository.save(save);

        assertEquals(save, amountIncreased);
    }

}
