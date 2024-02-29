package com.study.board.controller.dto

data class CommentResponse(
    val id: Long,
    val content: String,
    val createdAt: String,
    val createdBy: String,
)
