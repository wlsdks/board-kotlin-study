package com.study.board.event.dto

data class LikeEvent(
    val postId: Long,
    val createdBy: String,
)
