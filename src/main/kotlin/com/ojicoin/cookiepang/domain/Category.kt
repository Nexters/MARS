package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("categories")
class Category(
    @Id @Column("category_id") var id: Long?,
    @Column("name") @field:Size(max = 20) val name: String,
    @Column("color") val color: CategoryColor,
)

enum class CategoryColor { BLUE, LIME, PINK, PURPLE }
