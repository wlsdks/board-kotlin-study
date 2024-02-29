package com.study.board.domain

import jakarta.persistence.*

@Entity
class Comment(
    content: String,
    post: Post,
    createdBy: String,
) : BaseEntity(createdBy = createdBy) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var content: String = content
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    var post: Post = post
        protected set
}
