package com.study.board.service.dto

import com.study.board.domain.Comment
import com.study.board.domain.Post

data class CommentCreateRequestDto(
    val content: String,
    val createdBy: String,
)

fun CommentCreateRequestDto.toEntity(post: Post) = Comment(
    content = content,
    post = post,
    createdBy = createdBy
)
