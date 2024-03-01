package com.study.board.controller.dto

import com.study.board.service.dto.PostDetailResponseDto

data class PostDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val createdBy: String,
    val comments: List<CommentResponse> = emptyList(),
    val tags: List<String> = emptyList(),
)

fun PostDetailResponseDto.toResponse() = PostDetailResponse(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    createdBy = createdBy,
    comments = comments.map { it.toResponse() },
    tags = tags
)
