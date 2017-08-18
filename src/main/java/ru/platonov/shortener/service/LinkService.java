package ru.platonov.shortener.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.platonov.shortener.config.DomainConfig;
import ru.platonov.shortener.domain.model.Account;
import ru.platonov.shortener.domain.model.Link;
import ru.platonov.shortener.domain.model.RegisterLinkRequest;
import ru.platonov.shortener.domain.model.RegisterLinkResult;
import ru.platonov.shortener.domain.repository.AccountRepository;
import ru.platonov.shortener.domain.repository.LinkRepository;
import ru.platonov.shortener.spring.RetryConcurrentOperation;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LinkService.
 * <p>
 *     Service implements operations with links
 * </p>
 *
 * @author Platonov Alexey
 * @since 15.08.2017
 */
@Service
public class LinkService {

    private static final int MIN_COUNT = 6; //maximum 20358520 combinations

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private DomainConfig domainConfig;

    /**
     * Register link
     *
     * @param linkRequest link to be registered
     * @param accountId   аor which account
     * @return short link
     */
    public RegisterLinkResult registerLink(RegisterLinkRequest linkRequest, String accountId) {
        Account account = accountRepository.findOne(accountId);

        if (account == null) {
            String errorMsg = String.format("Account not found in db: account=%s ", accountId);

            throw new IllegalStateException(errorMsg);
        }

        Link link = Link.builder()
                .url(linkRequest.getUrl().trim())
                .shortUrl(RandomStringUtils.randomAlphabetic(MIN_COUNT))
                .redirectType(linkRequest.getRedirectType())
                .build();

        link.setUrl(linkRequest.getUrl());

        String generatedUrl = RandomStringUtils.randomAlphabetic(MIN_COUNT);

        link.setShortUrl(generatedUrl);
        link.setRedirectType(linkRequest.getRedirectType());
        link.setAccount(account);

        Link savedLink = linkRepository.save(link);

        return RegisterLinkResult.builder()
                .shortUrl(getFullUrl(savedLink.getShortUrl()))
                .build();
    }

    private String getFullUrl(String generatedPath) {
        try {
            return new URIBuilder()
                    .setScheme("http")
                    .setHost(domainConfig.getName())
                    .setPort(domainConfig.getPort())
                    .setPath('/' + generatedPath)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            //Effectively unreachable
            throw new IllegalStateException("Can't create short link", e);
        }

    }

    /**
     * Get usage statistic
     *
     * @param accountId for witch account
     * @return statistic map
     */
    public Map<String, Long> getStatistic(String accountId) {
        Account account = accountRepository.findOne(accountId);

        if (account == null) {
            return Collections.emptyMap();
        }

        return account.getLinks()
                .stream()
                .collect(Collectors.toMap(Link::getUrl, Link::getRedirectsAmount));
    }

    /**
     * Get real link by short link
     *
     * @param shortLink associated short link
     * @return real link
     */
    @Transactional(propagation = Propagation.NEVER)
    @RetryConcurrentOperation(exception = ObjectOptimisticLockingFailureException.class, retries = 3)
    public Optional<Link> getRealLink(String shortLink) {
        Optional<Link> link = linkRepository.findLinkByShortUrl(shortLink);

        link.ifPresent(
                linkInner -> {
                    linkInner.setRedirectsAmount(linkInner.getRedirectsAmount() + 1L);
                    linkRepository.save(linkInner);
                }
        );

        return link;
    }

}
