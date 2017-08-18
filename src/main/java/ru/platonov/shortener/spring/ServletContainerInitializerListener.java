package ru.platonov.shortener.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.platonov.shortener.config.DomainConfig;

/**
 * ServletContainerInitializerListener.
 * <p>
 *     Complements the site configuration with the port number,
 *     if the value has not been set
 * </p>
 *
 * @author Platonov Alexey
 * @since 18.08.2017
 */
@Component
public class ServletContainerInitializerListener implements
        ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @Autowired
    private DomainConfig config;

    /**
     * Sets the port number to the site configuration
     * received after the container was initialized
     *
     * @param e container event
     */
    @Override
    public void onApplicationEvent(
            EmbeddedServletContainerInitializedEvent e) {

        if(config.getPort() == 0) {
            config.setPort(e.getEmbeddedServletContainer().getPort());
        }
    }
}
