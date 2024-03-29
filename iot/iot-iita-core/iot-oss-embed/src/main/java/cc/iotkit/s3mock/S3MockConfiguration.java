/*
 *  Copyright 2017-2024 Adobe.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cc.iotkit.s3mock;

import cc.iotkit.s3mock.dto.ErrorResponse;
import cc.iotkit.s3mock.service.BucketService;
import cc.iotkit.s3mock.service.MultipartService;
import cc.iotkit.s3mock.service.ObjectService;
import cc.iotkit.s3mock.store.KmsKeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Configuration
@EnableConfigurationProperties(S3MockProperties.class)
public class S3MockConfiguration implements WebMvcConfigurer {

    @Bean
    Filter kmsFilter(final KmsKeyStore kmsKeyStore,
                     MappingJackson2XmlHttpMessageConverter messageConverter) {
        return new KmsValidationFilter(kmsKeyStore, messageConverter);
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML);
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
        configurer.mediaType("xml", MediaType.TEXT_XML);
    }

    @Bean
    @Profile("debug")
    public CommonsRequestLoggingFilter logFilter() {
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    /**
     * Creates an HttpMessageConverter for XML.
     *
     * @return The configured {@link MappingJackson2XmlHttpMessageConverter}.
     */
    @Bean
    MappingJackson2XmlHttpMessageConverter messageConverter() {
        var mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_XML);
        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);

        var xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(mediaTypes);

        return xmlConverter;
    }

    @Bean
    OrderedFormContentFilter httpPutFormContentFilter() {
        return new OrderedFormContentFilter() {
            @Override
            protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
                return true;
            }
        };
    }

    @Bean
    FaviconController faviconController() {
        return new FaviconController();
    }

    @Bean
    ObjectController fileStoreController(ObjectService objectService, BucketService bucketService) {
        return new ObjectController(bucketService, objectService);
    }

    @Bean
    BucketController bucketController(BucketService bucketService, S3MockProperties properties) {
        return new BucketController(bucketService, properties.getRegion());
    }

    @Bean
    MultipartController multipartController(BucketService bucketService,
                                            ObjectService objectService, MultipartService multipartService) {
        return new MultipartController(bucketService, objectService, multipartService);
    }

    @Bean
    S3MockExceptionHandler s3MockExceptionHandler() {
        return new S3MockExceptionHandler();
    }

    @Bean
    IllegalStateExceptionHandler illegalStateExceptionHandler() {
        return new IllegalStateExceptionHandler();
    }

    @Bean
    ObjectCannedAclHeaderConverter objectCannedAclHeaderConverter() {
        return new ObjectCannedAclHeaderConverter();
    }

    @Bean
    TaggingHeaderConverter taggingHeaderConverter() {
        return new TaggingHeaderConverter();
    }

    @Bean
    HttpRangeHeaderConverter httpRangeHeaderConverter() {
        return new HttpRangeHeaderConverter();
    }

    /**
     * {@link ResponseEntityExceptionHandler} dealing with {@link S3Exception}s; Serializes them to
     * response output as suitable ErrorResponses.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/ErrorResponses.html">API Reference</a>
     */
    @ControllerAdvice(basePackages = "cc.iotkit.s3mock")
    static class S3MockExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger LOG = LoggerFactory.getLogger(S3MockExceptionHandler.class);

        /**
         * Handles the given {@link S3Exception}.
         *
         * @param s3Exception {@link S3Exception} to be handled.
         * @return A {@link ResponseEntity} representing the handled {@link S3Exception}.
         */
        @ExceptionHandler(S3Exception.class)
        public ResponseEntity<ErrorResponse> handleS3Exception(final S3Exception s3Exception) {
            LOG.debug("Responding with status {}: {}", s3Exception.getStatus(), s3Exception.getMessage(),
                    s3Exception);

            var errorResponse = new ErrorResponse(
                    s3Exception.getCode(),
                    s3Exception.getMessage(),
                    null,
                    null
            );

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            return ResponseEntity.status(s3Exception.getStatus()).headers(headers).body(errorResponse);
        }
    }

    /**
     * {@link ResponseEntityExceptionHandler} dealing with {@link IllegalStateException}s.
     * Serializes them to response output as a 500 Internal Server Error {@link ErrorResponse}.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/ErrorResponses.html">API Reference</a>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_Error.html">API Reference</a>
     */
    @ControllerAdvice(basePackages = "cc.iotkit.s3mock")
    static class IllegalStateExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger LOG = LoggerFactory.getLogger(IllegalStateExceptionHandler.class);

        /**
         * Handles the given {@link IllegalStateException}.
         *
         * @param exception {@link IllegalStateException} to be handled.
         * @return A {@link ResponseEntity} representing the handled {@link IllegalStateException}.
         */
        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleS3Exception(IllegalStateException exception) {
            LOG.debug("Responding with status {}: {}", INTERNAL_SERVER_ERROR, exception.getMessage(),
                    exception);

            var errorResponse = new ErrorResponse(
                    "InternalError",
                    "We encountered an internal error. Please try again.",
                    null,
                    null
            );

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            return ResponseEntity.internalServerError().headers(headers).body(errorResponse);
        }
    }
}
