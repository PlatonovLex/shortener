package ru.platonov.shortener.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ErrorResponse.
 * <p>
 *     Spring error response object, that returns if any error occurs
 *     in rest-api
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "timestamp",
        "status",
        "error",
        "exception",
        "errors",
        "message",
        "path"
})
@ToString
public class ErrorResponse {

    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("error")
    private String error;
    @JsonProperty("exception")
    private String exception;
    @JsonProperty("errors")
    private List<Error> errors;
    @JsonProperty("message")
    private String message;
    @JsonProperty("path")
    private String path;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("exception")
    public String getException() {
        return exception;
    }

    @JsonProperty("exception")
    public void setException(String exception) {
        this.exception = exception;
    }

    @JsonProperty("errors")
    public List<Error> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

}
