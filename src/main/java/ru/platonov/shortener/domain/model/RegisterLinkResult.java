package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Register link in service result")
public class RegisterLinkResult {

    @NonNull
    @JsonProperty("shortUrl")
    @ApiModelProperty(value = "short url", example = "http://localhost:8080/iPqRJH",
            required = true)
    private String shortUrl;

}
