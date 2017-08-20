package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * CreateAccountResult.
 * <p>
 *     Account creation result
 * </p>
 *
 * @author Platonov Alexey
 * @since 14.08.2017
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@ApiModel(description = "Result for creation account")
public class CreateAccountResult {

    @NonNull
    @JsonProperty("success")
    @ApiModelProperty(value = "action result", example = "true", required = true)
    private Boolean success;

    @NonNull
    @JsonProperty("description")
    @ApiModelProperty(value = "result description", example = "Your account is opened", required = true)
    private String description;

    @JsonProperty("password")
    @ApiModelProperty(value = "created password for account", example = "XCb78")
    private String password;

    public static PasswordStep forSuccess() {
        return new Builder().forSuccess();
    }

    public static DescriptionStep forError() {
        return new Builder().forError();
    }

    public interface PasswordStep {
        DescriptionStep withPassword(String password);
    }

    public interface DescriptionStep extends BuildWithDescriptionStep, BuildStep {
    }

    public interface BuildWithDescriptionStep {
        BuildStep withDescription(String description);
    }

    public interface BuildStep {
        CreateAccountResult build();
    }

    /**
     * Typical step builder
     */
    public static class Builder implements PasswordStep, DescriptionStep {
        private Boolean success;
        private String description;
        private String password;

        private Builder() {
        }

        public PasswordStep forSuccess() {
            success = Boolean.TRUE;
            description = "Your account is opened";
            return this;
        }

        public DescriptionStep forError() {
            success = Boolean.FALSE;
            return this;
        }

        @Override
        public BuildStep withDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public DescriptionStep withPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public CreateAccountResult build() {
            return new CreateAccountResult(
                    success,
                    description,
                    password
            );
        }
    }
}
