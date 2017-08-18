package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.HttpStatus;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * RegisterLinkRequest.
 * <p>
 *     Request for registration link command
 * </p>
 *
 * @author Platonov Alexey
 * @since 15.08.2017
 */
@Getter
@Setter
@ToString
public class RegisterLinkRequest {

    @NotEmpty
    @JsonProperty("url")
    private String url;

    @Min(301)
    @Max(302)
    @JsonProperty("redirectType")
    private Integer redirectType = HttpStatus.SC_MOVED_TEMPORARILY;

}
