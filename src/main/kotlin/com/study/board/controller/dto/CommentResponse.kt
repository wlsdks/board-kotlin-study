package com.study.board.controller.dto

import com.study.board.service.dto.CommentResponseDto

data class CommentResponse(
    val id: Long,
    val content: String,
    val createdAt: String,
    val createdBy: String,
)

fun CommentResponseDto.toResponse() = CommentResponse(
    id = id,
    content = content,
    createdAt = createdAt,
    createdBy = createdBy
)
