package com.study.board.service.dto

import com.study.board.domain.Post

data class PostCreateRequestDto(
    val title: String,
    val content: String,
    val createdBy: String,
    val tags: List<String> = emptyList(),
)

// 확장함수 사용해서 팩토리 메서드 만들기
fun PostCreateRequestDto.toEntity() = Post(
    title = title,
    content = content,
    createdBy = createdBy,
    tags = tags
)
