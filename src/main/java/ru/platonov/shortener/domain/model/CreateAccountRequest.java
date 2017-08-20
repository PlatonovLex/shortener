package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

/**
 * CreateAccountRequest.
 * <p>
 *     Request for create account command
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@Getter
@Setter
@ToString
@ApiModel(description = "Request for creation account")
public class CreateAccountRequest {

    @Valid
    @NotBlank
    @JsonProperty("AccountId")
    @ApiModelProperty(value = "User account id", example = "myAccount", required = true)
    private String accountId;

}
