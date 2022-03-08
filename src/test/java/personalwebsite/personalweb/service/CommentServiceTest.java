package personalwebsite.personalweb.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.comments.Comment;
import personalwebsite.personalweb.domain.comments.CommentRepository;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.web.advice.RestException;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
import personalwebsite.personalweb.web.dto.comments.CommentSaveRequestDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;

    @Test
    public void 댓글작성_조회() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long postId = postService.savePost(requestDto);

        String username1 = "user1";
        String password1 = "pass1";
        String text1 = "text1";
        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .username(username1)
                .password(password1)
                .text(text1)
                .build();

        Long commentId = commentService.saveComments(postId, commentSaveRequestDto);

        //when
        List<CommentListResponseDto> allComments = commentService.findAllComments(postId);

        //then
        CommentListResponseDto responseDto = allComments.get(0);
        assertEquals(username1, responseDto.getUsername());
        assertEquals(text1, responseDto.getText());
    }

    @Test
    public void 댓글중복닉네임불가() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);

        Long postId = postService.savePost(requestDto);

        String username1 = "user1";
        String password1 = "pass1";
        String text1 = "text1";

        String username2 = "user1";
        String password2 = "pass2";
        String text2 = "text2";

        CommentSaveRequestDto commentSaveRequestDto1 = CommentSaveRequestDto.builder()
                .username(username1)
                .password(password1)
                .text(text1)
                .build();

        CommentSaveRequestDto commentSaveRequestDto2 = CommentSaveRequestDto.builder()
                .username(username2)
                .password(password2)
                .text(text2)
                .build();

        //when
        Long commentId1 = commentService.saveComments(postId, commentSaveRequestDto1);

        //then
        assertThrows(RestException.class, () -> {
            commentService.saveComments(postId, commentSaveRequestDto2);
        });
    }

    @Test
    public void 댓글삭제_성공() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long postId = postService.savePost(requestDto);

        String username1 = "user1";
        String password1 = "pass1";
        String text1 = "text1";
        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .username(username1)
                .password(password1)
                .text(text1)
                .build();
        Long commentId = commentService.saveComments(postId, commentSaveRequestDto);

        //when
        String inputPass = password1;
        if (commentService.checkCommentPassword(inputPass, commentId)) {
            commentService.deleteComment(commentId);
        }

        //then
        List<Comment> commentList = commentRepository.findAll();
        assertEquals(Collections.EMPTY_LIST, commentList);
    }

    @Test
    public void 댓글삭제_실패() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long postId = postService.savePost(requestDto);

        String username1 = "user1";
        String password1 = "pass1";
        String text1 = "text1";
        CommentSaveRequestDto commentSaveRequestDto = CommentSaveRequestDto.builder()
                .username(username1)
                .password(password1)
                .text(text1)
                .build();
        Long commentId = commentService.saveComments(postId, commentSaveRequestDto);

        //when
        String inputPass = "pass2";
        if (commentService.checkCommentPassword(inputPass, commentId)) {
            commentService.deleteComment(commentId);
        }

        //then
        List<Comment> commentList = commentRepository.findAll();
        assertEquals(1, commentList.size());
    }
}