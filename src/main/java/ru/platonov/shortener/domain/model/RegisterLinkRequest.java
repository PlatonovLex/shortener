package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.HttpStatus;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

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
@ApiModel(description = "Register link in service")
public class RegisterLinkRequest {

    @NotBlank
    @URL
    @JsonProperty("url")
    @ApiModelProperty(value = "url to be registered", example = "http://google.com", required = true,
    notes = "URL should consist of: " +
            "schema: \"http\" or \"https\" and domain name - required params " +
            "port and url-path with parameters - is optional" )
    private String url;

    @Min(301)
    @Max(302)
    @JsonProperty("redirectType")
    @ApiModelProperty(value = "redirect type", example = "301",
            allowableValues = "301 - MOVED_PERMANENTLY, 302 - MOVED_TEMPORARY")
    private Integer redirectType = HttpStatus.SC_MOVED_TEMPORARILY;

}
