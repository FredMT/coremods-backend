package com.tofutracker.Coremods.config;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
@EnableConfigurationProperties(CloudflareR2Properties.class)
@RequiredArgsConstructor
public class CloudflareR2Config {

    private final CloudflareR2Properties cloudflareProperties;

    @Bean
    public S3Client s3Client() {

        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        return S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(Region.of("auto"))
                .endpointOverride(URI.create(cloudflareProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                cloudflareProperties.getAccessKey(),
                                cloudflareProperties.getSecretKey())))
                .serviceConfiguration(serviceConfig)
                .build();

    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(false)
                .build();

        return S3AsyncClient.builder()
                .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
                .region(Region.of("auto"))
                .endpointOverride(URI.create(cloudflareProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                cloudflareProperties.getAccessKey(),
                                cloudflareProperties.getSecretKey())))
                 .serviceConfiguration(serviceConfig)
                .multipartEnabled(true)
                .build();
    }

    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        return S3Presigner.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(cloudflareProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                cloudflareProperties.getAccessKey(),
                                cloudflareProperties.getSecretKey())))
                 .serviceConfiguration(serviceConfig)
                .build();
    }

}
