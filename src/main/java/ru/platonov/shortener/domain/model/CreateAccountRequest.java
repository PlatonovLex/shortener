package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

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
public class CreateAccountRequest {

    @Valid
    @NotEmpty
    @JsonProperty("AccountId")
    private String accountId;

}
