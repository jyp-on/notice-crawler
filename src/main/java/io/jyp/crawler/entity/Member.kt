package io.jyp.crawler.entity

import jakarta.persistence.*

@Entity
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Primary Key, 기본값 0

    @Column(nullable = false, unique = true)
    val email: String, // null 불가능

    @Column(nullable = false)
    var noticeFlag: Boolean = false // 알림 여부, 기본값 false
) : BaseTimeEntity()
