package com.study.board.controller.dto

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String,
)
