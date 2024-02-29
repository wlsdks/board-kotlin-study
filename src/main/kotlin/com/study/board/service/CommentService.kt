package com.study.board.service

import com.study.board.exception.CommentNotDeletableException
import com.study.board.exception.CommentNotFoundException
import com.study.board.exception.PostNotFoundException
import com.study.board.repository.CommentRepository
import com.study.board.repository.PostRepository
import com.study.board.service.dto.CommentCreateRequestDto
import com.study.board.service.dto.CommentUpdateRequestDto
import com.study.board.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {

    @Transactional
    fun createComment(postId: Long, createRequestDto: CommentCreateRequestDto): Long {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        return commentRepository.save(createRequestDto.toEntity(post)).id
    }

    @Transactional
    fun updateComment(id: Long, updateRequestDto: CommentUpdateRequestDto): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        comment.update(updateRequestDto)
        return comment.id
    }

    @Transactional
    fun deleteComment(id: Long, deletedBy: String): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        if (comment.createdBy != deletedBy) throw CommentNotDeletableException()
        commentRepository.delete(comment)
        return id
    }
}
