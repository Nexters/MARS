package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user_categories")
class UserCategory(
    @Id @Column("user_category_id") var id: Long? = null,
    @Column("user_id") val userId: Long,
    @Column("category_id") val categoryId: Long,
)
