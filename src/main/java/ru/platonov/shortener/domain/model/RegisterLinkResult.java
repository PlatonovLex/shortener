package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * RegisterLinkResult.
 * <p>
 *     Result of registration link in service
 * </p>
 *
 * @author Platonov Alexey
 * @since 15.08.2017
 */
@Builder
@Getter
@ToString
public class RegisterLinkResult {

    @NonNull
    @JsonProperty("shortUrl")
    private String shortUrl;

}
