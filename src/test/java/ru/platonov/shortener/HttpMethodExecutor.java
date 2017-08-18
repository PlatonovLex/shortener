package ru.platonov.shortener;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.platonov.shortener.model.ErrorResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * HttpMethodExecutor.
 * <p>
 *     Executor for http requests with serialization / deserialization
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpMethodExecutor {

    private static final Logger logger = LoggerFactory.getLogger(HttpMethodExecutor.class);

    private CloseableHttpClient httpClient;

    private boolean basicAuthEnabled;

    private String userName;

    private String password;

    public HttpMethodExecutor(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Execute Post method
     *
     * @param uri               request uri
     * @param requestObject     pojo request
     * @param responseClass     class for response object
     * @param <T>               response type
     * @return                  response structure
     * @throws IOException      if json parsing fails
     */
    public  <T> MethodResponse<T> executePostJson(
            String uri, Object requestObject, Class<T> responseClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(requestObject);

        ByteArrayEntity entity = new ByteArrayEntity(bytes);

        entity.setContentType(ContentType.APPLICATION_JSON.toString());
        HttpPost request = new HttpPost(uri);

        request.setEntity(entity);
        setBasicAuthHeader(request);

        return executeMethod(request, objectMapper, responseClass);
    }

    /**
     * Execute Get method
     *
     * @param uri               request uri
     * @param responseClass     class for response object
     * @param <T>               response type
     * @return                  response structure
     * @throws IOException      if json parsing fails
     */
    public <T> MethodResponse<T> executeGetMethod(String uri, Class<T> responseClass) throws IOException {
        HttpGet request = new HttpGet(uri);

        setBasicAuthHeader(request);
        return executeMethod(request, new ObjectMapper(), responseClass);
    }

    private <T> MethodResponse<T> executeMethod(
            HttpUriRequest request, ObjectMapper objectMapper, Class<T> responseClass) throws IOException {
        try(CloseableHttpResponse response = httpClient.execute(request)) {
            MethodResponse<T> objectMethodResponse = new MethodResponse<>();

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                objectMethodResponse.setErrorResponse(
                        objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class));

                logger.warn(objectMethodResponse.toString());
                return objectMethodResponse;
            }

            objectMethodResponse.setSuccess(true);
            objectMethodResponse.setResponse(
                    objectMapper.readValue(response.getEntity().getContent(), responseClass));

            return objectMethodResponse;
        }
    }

    private HttpMethodExecutor setBasicAuthHeader(HttpMessage request) {
        if (!basicAuthEnabled) {
            return this;
        }

        String auth = userName + ':' + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);

        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        return this;
    }

    /**
     * Structure with deserialized response
     * @param <T> response class
     */
    @Getter
    @Setter
    @ToString
    public static class MethodResponse<T> {

        /**
         * true if results with http code 200
         */
        private boolean success;

        /**
         * deserialized response object
         */
        private T response;

        /**
         * deserialized spring error response
         */
        private ErrorResponse errorResponse;

    }

}
