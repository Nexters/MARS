package com.ojicoin.cookiepang.repository

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.ojicoin.cookiepang.config.S3Properties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.io.InputStream

@Profile("!local")
@Repository
class S3Repository(
    private val s3Properties: S3Properties,
    @Value("#{systemEnvironment['AWS_S3_ACCESS_KEY']}") val s3AccessKey: String,
    @Value("#{systemEnvironment['AWS_S3_SECRET_KEY']}") val s3SecretKey: String,
) : StorageRepository {
    private val amazonS3 = AmazonS3Client.builder()
        .withRegion("ap-northeast-2")
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(s3AccessKey, s3SecretKey)))
        .build()

    override fun store(key: String, inputStream: InputStream): String {
        val keyWithDirectory = s3Properties.directory + key

        val putObjectRequest = PutObjectRequest(s3Properties.bucket, keyWithDirectory, inputStream, ObjectMetadata())
        amazonS3.putObject(putObjectRequest)

        return amazonS3.getUrl(s3Properties.bucket, keyWithDirectory).toString()
    }
}
