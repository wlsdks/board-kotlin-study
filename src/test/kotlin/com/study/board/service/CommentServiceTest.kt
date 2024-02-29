package com.study.board.service

import com.study.board.domain.Comment
import com.study.board.domain.Post
import com.study.board.exception.CommentNotDeletableException
import com.study.board.exception.CommentNotUpdatableException
import com.study.board.exception.PostNotFoundException
import com.study.board.repository.CommentRepository
import com.study.board.repository.PostRepository
import com.study.board.service.dto.CommentCreateRequestDto
import com.study.board.service.dto.CommentUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class CommentServiceTest(
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) : BehaviorSpec({

    given("댓글 생성시") {
        val post = postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "게시글 생성자"
            )
        )
        When("인풋이 정상적으로 들어오면") {
            val commentId = commentService.createComment(
                post.id,
                CommentCreateRequestDto(
                    content = "댓글 내용",
                    createdBy = "댓글 생성자"
                )
            )
            then("정상 생성됨을 확인한다.") {
                commentId shouldBeGreaterThan 0L
                val comment = commentRepository.findByIdOrNull(commentId)
                comment shouldNotBe null
                comment?.content shouldBe "댓글 내용"
                comment?.createdBy shouldBe "댓글 생성자"
            }
        }
        When("게시글이 존재하지 않으면") {
            then("게시글이 존재하지 않으면 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    commentService.createComment(
                        9999L,
                        CommentCreateRequestDto(
                            content = "댓글 내용",
                            createdBy = "댓글 생성자"
                        )
                    )
                }
            }
        }
    }

    given("댓글 수정시") {
        val post = postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "게시글 생성자"
            )
        )
        val saved = commentRepository.save(Comment("댓글 내용", post, "댓글 수정자"))
        When("인풋이 정상적으로 들어오면") {
            val updatedId = commentService.updateComment(
                saved.id,
                CommentUpdateRequestDto(
                    content = "수정된 댓글 내용",
                    updatedBy = "댓글 수정자"
                )
            )
            then("정상 수정됨을 확인한다.") {
                updatedId shouldBe saved.id
                val updated = commentRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.content shouldBe "수정된 댓글 내용"
                updated?.updatedBy shouldBe "댓글 수정자"
            }
        }
        When("작성자와 수정자가 다르면") {
            then("수정할 수 없는 게시물이라는 예외가 발생한다.") {
                shouldThrow<CommentNotUpdatableException> {
                    commentService.updateComment(
                        saved.id,
                        CommentUpdateRequestDto(
                            content = "수정된 댓글 내용",
                            updatedBy = "다른 사용자"

                        )
                    )
                }
            }
        }
    }

    given("댓글 삭제시") {
        val post = postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "게시글 생성자"
            )
        )
        val saved1 = commentRepository.save(Comment("댓글 내용", post, "댓글 생성자"))
        val saved2 = commentRepository.save(Comment("댓글 내용2", post, "댓글 생성자2"))

        When("인풋이 정상적으로 들어오면") {
            val commentId = commentService.deleteComment(saved1.id, "댓글 생성자")
            then("정상 삭제됨을 확인한다.") {
                commentId shouldBe saved1.id
                commentRepository.findByIdOrNull(commentId) shouldBe null
            }
        }
        When("작성자와 삭제자가 다르면") {
            then("삭제할 수 없는 댓글 예외가 발생한다.") {
                shouldThrow<CommentNotDeletableException> { commentService.deleteComment(saved2.id, "삭제자") }
            }
        }
    }
})
