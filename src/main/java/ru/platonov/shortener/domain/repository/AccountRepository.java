package ru.platonov.shortener.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.platonov.shortener.domain.model.Account;

/**
 * AccountRepository.
 * <p>
 *     Repository fo user entity
 * </p>
 *
 * @author Platonov Alexey
 * @since 14.08.2017
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, String>{
}
