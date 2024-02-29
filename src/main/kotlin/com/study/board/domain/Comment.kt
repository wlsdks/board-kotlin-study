package com.study.board.domain

import com.study.board.exception.CommentNotUpdatableException
import com.study.board.service.dto.CommentUpdateRequestDto
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

    fun update(updateRequestDto: CommentUpdateRequestDto) {
        if (updateRequestDto.updatedBy != this.createdBy) {
            throw CommentNotUpdatableException()
        }
        this.content = updateRequestDto.content
        super.updatedBy(updateRequestDto.updatedBy)
    }
}
