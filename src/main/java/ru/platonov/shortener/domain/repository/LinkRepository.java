package ru.platonov.shortener.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.platonov.shortener.domain.model.Link;

import java.util.Optional;

/**
 * LinkRepository.
 * <p>
 *     Repository of link entity
 * </p>
 *
 * @author Platonov Alexey
 * @since 15.08.2017
 */
@Repository
public interface LinkRepository extends CrudRepository<Link, Long>{

    /**
     * Get link by its short link
     *
     * @param shortUrl - short link
     * @return saved real link
     */
    Optional<Link> findLinkByShortUrl(String shortUrl);

    /**
     * Get link associated with specified account by real link
     *
     * @param url        real link
     * @param accountId  associated account
     * @return link object
     */
    Optional<Link> findLinkByUrlAndAccount_Id(String url, String accountId);

}
