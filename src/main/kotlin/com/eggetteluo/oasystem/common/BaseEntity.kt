package com.eggetteluo.oasystem.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass // 不会生成数据表，但是它的字段会被子类继承
@EntityListeners(AuditingEntityListener::class)
class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: Long? = null

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null

    // 软删除标记：为空表示未删除，有值表示删除时间
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null

}