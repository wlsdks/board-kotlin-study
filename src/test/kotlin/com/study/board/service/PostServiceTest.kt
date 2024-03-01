package com.study.board.service

import com.study.board.domain.Comment
import com.study.board.domain.Post
import com.study.board.exception.PostNotDeletableException
import com.study.board.exception.PostNotFoundException
import com.study.board.exception.PostNotUpdatableException
import com.study.board.repository.CommentRepository
import com.study.board.repository.PostRepository
import com.study.board.repository.TagRepository
import com.study.board.service.dto.PostCreateRequestDto
import com.study.board.service.dto.PostSearchRequestDto
import com.study.board.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
) : BehaviorSpec({

    beforeSpec {
        postRepository.saveAll(
            listOf(
                Post(title = "title1", content = "content1", createdBy = "jinan1"),
                Post(title = "title12", content = "content1", createdBy = "jinan1"),
                Post(title = "title13", content = "content1", createdBy = "jinan1"),
                Post(title = "title14", content = "content1", createdBy = "jinan1"),
                Post(title = "title15", content = "content1", createdBy = "jinan1"),
                Post(title = "title6", content = "content1", createdBy = "jinan2"),
                Post(title = "title7", content = "content1", createdBy = "jinan2"),
                Post(title = "title8", content = "content1", createdBy = "jinan2"),
                Post(title = "title9", content = "content1", createdBy = "jinan2"),
                Post(title = "title10", content = "content1", createdBy = "jinan2")
            )
        )
    }

    given("게시글 생성 시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "jinan"
                )
            )
            then("게시글이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "jinan"
            }
        }
        When("태그가 추가되면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "jinan",
                    tags = listOf("tag1", "tag2")
                )
            )
            then("태그가 정상적으로 추가됨을 확인한다.") {
                val tags = tagRepository.findByPostId(postId)
                tags.size shouldBe 2
                tags[0].name shouldBe "tag1"
                tags[1].name shouldBe "tag2"
            }
        }
    }

    given("게시글 수정시") {
        val saved = postRepository.save(
            Post(title = "title", content = "content", createdBy = "jinan", tags = listOf("tag1", "tag2"))
        )
        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "jinan"
                )
            )
            then("게시글이 정상적으로 수정됨을 확인한다.") {
                saved.id shouldBe updatedId
                val updated = postRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.title shouldBe "update title"
                updated?.content shouldBe "update content"
            }
        }

        When("게시글이 없을 때") {
            then("게시글을 찾을 수 없다는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "update jinan"
                        )
                    )
                }
            }
        }

        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다. 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "another jinan"
                        )
                    )
                }
            }
        }

        When("태그가 수정되었을 때") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "제목",
                    content = "내용",
                    updatedBy = "jinan",
                    tags = listOf("tag1", "tag2", "tag3")
                )
            )
            then("정상적으로 수정됨을 확인한다.") {
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag3"
            }
            then("태그 순서가 변경되었을때 정상적으로 변경됨을 확인한다.") {
                postService.updatePost(
                    saved.id,
                    PostUpdateRequestDto(
                        title = "제목",
                        content = "내용",
                        updatedBy = "jinan",
                        tags = listOf("tag3", "tag2", "tag1")
                    )
                )
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag1"
            }
        }
    }

    given("게시글 삭제시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "jinan"))
        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "jinan")
            then("게시글이 정상적으로 삭제됨을 확인한다.") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }
        When("작성자가 동일하지 않으면") {
            val saved2 = postRepository.save(Post(title = "title", content = "content", createdBy = "jinan"))
            then("삭제할 수 없는 게시물입니다. 예외가 발생한다.") {
                shouldThrow<PostNotDeletableException> {
                    postService.deletePost(saved2.id, "jinan2")
                }
            }
        }
    }

    given("게시글 조회시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "jinan"))
        When("정상 조회시") {
            val post = postService.getPost(saved.id)
            then("게시글의 내용이 정상적으로 반환됨을 확인한다.") {
                post.title shouldBe "title"
                post.content shouldBe "content"
                post.createdBy shouldBe "jinan"
            }
        }
        When("게시글이 없을 때") {
            then("게시글을 찾을수 없다는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.getPost(9999L)
                }
            }
        }
        When("댓글 추가시") {
            commentRepository.save(Comment("댓글 내용1", saved, "댓글 작성자"))
            commentRepository.save(Comment("댓글 내용2", saved, "댓글 작성자"))
            commentRepository.save(Comment("댓글 내용3", saved, "댓글 작성자"))
            val post = postService.getPost(saved.id)
            Then("댓글이 함께 조회됨을 확인한다.") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용1"
                post.comments[1].content shouldBe "댓글 내용2"
                post.comments[2].content shouldBe "댓글 내용3"
                post.comments[0].createdBy shouldBe "댓글 작성자"
                post.comments[1].createdBy shouldBe "댓글 작성자"
                post.comments[2].createdBy shouldBe "댓글 작성자"
            }
        }
    }

    given("게시글 목록 조회시") {
        When("정상 조회시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글 페이지가 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title"
                postPage.content[0].createdBy shouldContain "jinan"
            }
        }
        When("타이틀로 검색") {
            then("타이틀에 해당하는 게시글이 반환된다.") {
                val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "title1"))
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldContain "jinan"
            }
        }
        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "jinan1"))
            then("작성자에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldBe "jinan1"
            }
        }
    }
})
