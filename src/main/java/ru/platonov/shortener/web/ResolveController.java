package ru.platonov.shortener.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.platonov.shortener.exceptions.ResourceNotFoundException;
import ru.platonov.shortener.service.LinkService;

/**
 * ResolveController
 * <p>
 *     Controller process all user GET queries from root
 * </p>
 *
 * @author Platonov Alexey
 * @since 17.08.2017
 */
@Controller
public class ResolveController {

    @Autowired
    private LinkService linkService;

    /**
     * Processes user queries on short links
     *
     * @param shortUrl short url
     * @return redirect view
     * @throws ResourceNotFoundException if link is not in the database
     */
    @RequestMapping(value = "/{shortUrl}", method = RequestMethod.GET)
    public ModelAndView resolveLink(@PathVariable("shortUrl") String shortUrl) {
        return linkService.getRealLink(shortUrl)
                .map(realLinkInner -> {
                    RedirectView redirectView = new RedirectView();
                    redirectView.setStatusCode(
                            HttpStatus.valueOf(realLinkInner.getRedirectType()));
                    redirectView.setUrl(realLinkInner.getUrl());

                    return new ModelAndView(redirectView);
                })
                .orElseThrow(ResourceNotFoundException::new);
    }

}
