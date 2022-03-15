package personalwebsite.personalweb.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.comments.Comment;
import personalwebsite.personalweb.domain.comments.CommentRepository;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.domain.user.Role;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
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
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

    private User user;
    private Long postId;
    MockHttpSession session;
    MockHttpServletRequest request;

    @Before()
    public void setUp() throws Exception {
        user = User.builder()
                .name("kim")
                .email("example@gmail.com")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        session = new MockHttpSession();
        session.setAttribute("user", new SessionUser(user));

        request = new MockHttpServletRequest();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PostForm form = new PostForm();
        form.setTitle("title");
        form.setSummary("summary");
        form.setContent("content");
        postId = postService.savePost(form);
    }

    @After
    public void cleanup() {
        session.clearAttributes();
        session = null;
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void 댓글작성_조회() {
        //given
        String username1 = "user1";
        String text1 = "text1";
        CommentForm form = makeCommentForm(username1, text1);

        Long commentId = commentService.saveComments(postId, form);

        //when
        List<CommentListResponseDto> allComments = commentService.findAllComments(postId);

        //then
        CommentListResponseDto responseDto = allComments.get(0);
        assertEquals(username1, responseDto.getUsername());
        assertEquals(text1, responseDto.getText());
    }

    @Test
    public void 댓글삭제() {
        //given
        String username1 = "user1";
        String text1 = "text1";
        CommentForm form = makeCommentForm(username1, text1);

        Long commentId = commentService.saveComments(postId, form);

        //when
        commentService.deleteComment(commentId);

        //then
        List<Comment> commentList = commentRepository.findAll();
        assertEquals(Collections.EMPTY_LIST, commentList);
    }

    @Test
    public void 작성자중복확인() {
        //given
        String username1 = "user1";
        String text1 = "text1";
        CommentForm form = makeCommentForm(username1, text1);
        Long id = commentService.saveComments(postId, form);

        //when
        String username2 = "user2";

        //then
        assertEquals(commentService.checkUniqueUsername(username2, postId), false);
        assertEquals(commentService.checkUniqueUsername(username1, postId), true);

    }

    private CommentForm makeCommentForm(String username, String text) {
        CommentForm form = new CommentForm();
        form.setUsername(username);
        form.setText(text);
        return form;
    }
}