package io.jyp.crawler.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    var createdDate: LocalDateTime? = null
        private set // 외부에서 수정하지 못하도록 setter를 private으로 설정

    @LastModifiedDate
    @Column
    var modifiedDate: LocalDateTime? = null
        private set // 외부에서 수정하지 못하도록 setter를 private으로 설정
}
