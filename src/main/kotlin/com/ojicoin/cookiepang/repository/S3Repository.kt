package com.ojicoin.cookiepang.repository

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.ojicoin.cookiepang.config.S3Properties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.io.InputStream

@Profile("!local")
@Repository
class S3Repository(
    private val s3Properties: S3Properties,
) : StorageRepository {

    private val amazonS3: AmazonS3 = AmazonS3ClientBuilder.defaultClient()

    override fun store(key: String, inputStream: InputStream): String {
        val keyWithDirectory = s3Properties.directory + key

        val putObjectRequest = PutObjectRequest(s3Properties.bucket, keyWithDirectory, inputStream, ObjectMetadata())
        amazonS3.putObject(putObjectRequest)

        return amazonS3.getUrl(s3Properties.bucket, keyWithDirectory).toString()
    }
}
