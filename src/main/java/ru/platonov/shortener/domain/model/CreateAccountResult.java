package ru.platonov.shortener.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateAccountResult {

    @NonNull
    @JsonProperty("success")
    private Boolean success;

    @NonNull
    @JsonProperty("description")
    private String description;

    @JsonProperty("password")
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    public interface ResultStep {
        PasswordStep forSuccess();

        BuildWithDescriptionStep forError();
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
    public static class Builder implements ResultStep, PasswordStep, DescriptionStep {
        private Boolean success;
        private String description;
        private String password;

        private Builder() {
        }

        @Override
        public PasswordStep forSuccess() {
            success = Boolean.TRUE;
            description = "Your account is opened";
            return this;
        }

        @Override
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
