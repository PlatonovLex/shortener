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

}
