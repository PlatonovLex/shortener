package ru.platonov.shortener;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.platonov.shortener.domain.repository.LinkRepository;

/**
 * SlowTestConfiguration.
 * <p>
 *     Spying beans
 * </p>
 *
 * @author Platonov Alexey
 * @since 20.08.2017
 */
@Configuration
public class SlowTestConfiguration {

    @Bean
    @Primary
    public LinkRepository linkRepositoryMock(LinkRepository linkRepository) {
        return Mockito.mock(LinkRepository.class, AdditionalAnswers.delegatesTo(linkRepository));
    }

}
