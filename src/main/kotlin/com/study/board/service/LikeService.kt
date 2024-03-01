package com.study.board.service

import com.study.board.domain.Like
import com.study.board.exception.PostNotFoundException
import com.study.board.repository.LikeRepository
import com.study.board.repository.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
) {

    @Transactional
    fun createLike(postId: Long, createdBy: String): Long {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        return likeRepository.save(Like(post, createdBy)).id
    }

    fun countLike(postId: Long): Long {
        return likeRepository.countByPostId(postId)
    }
}
