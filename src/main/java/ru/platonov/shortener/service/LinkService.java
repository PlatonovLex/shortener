package ru.platonov.shortener.service;

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
import ru.platonov.shortener.exceptions.ResourceNotFoundException;
import ru.platonov.shortener.spring.RetryConcurrentOperation;

import java.net.URISyntaxException;
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

        String trimmedUrl = linkRequest.getUrl().trim();

        Optional<Link> linkByUrlAndAccountId =
                linkRepository.findLinkByUrlAndAccount_Id(trimmedUrl, accountId);

        if (linkByUrlAndAccountId.isPresent()) {
            Link savedLink = linkByUrlAndAccountId.get();

            return RegisterLinkResult.builder()
                    .shortUrl(getFullUrl(savedLink.getShortUrlPart()))
                    .build();
        }

        Link link = Link.builder()
                .url(trimmedUrl)
                .redirectType(linkRequest.getRedirectType())
                .account(account)
                .build();

        Link savedLink = linkRepository.save(link);

        return RegisterLinkResult.builder()
                .shortUrl(getFullUrl(savedLink.getShortUrlPart()))
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
            throw new ResourceNotFoundException("Statistic for User not found");
        }

        return account.getLinks()
                .stream()
                .collect(Collectors.toMap(Link::getUrl, Link::getRedirectsAmount));
    }

    /**
     * Get real link by short link
     * Even if someone has already changed the object before saving, then this should not affect the user in any way.
     * Therefore, the retention attempt will be repeated
     *
     * @param shortLink associated short link
     * @return real link
     */
    @Transactional(propagation = Propagation.NEVER)
    @RetryConcurrentOperation(exception = ObjectOptimisticLockingFailureException.class, retries = 3)
    public Optional<Link> getRealLink(String shortLink) {
        Optional<Link> link = linkRepository.findLinkByShortUrlPart(shortLink);

        link.ifPresent(
                linkInner -> {
                    linkInner.setRedirectsAmount(linkInner.getRedirectsAmount() + 1L);
                    linkRepository.save(linkInner);
                }
        );

        return link;
    }

}
