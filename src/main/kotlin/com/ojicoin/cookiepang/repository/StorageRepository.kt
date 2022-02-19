package com.ojicoin.cookiepang.repository

import java.io.InputStream

interface StorageRepository {
    fun store(key: String, inputStream: InputStream): String
}
