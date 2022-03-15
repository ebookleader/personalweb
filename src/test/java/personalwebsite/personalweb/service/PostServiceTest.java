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
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.domain.user.Role;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.web.dto.posts.PostResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostService postService;
    @Autowired
    FileService fileService;

    private User user;
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
    }

    @After
    public void cleanup() {
        session.clearAttributes();
        session = null;
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void 유저저장확인() {
        List<User> users = userRepository.findAll();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), "kim");
    }

    @Test
    public void 게시글저장_조회() {
        //given
        String title = "test title";
        String summary = "test summary";
        String content = "test content";
        PostForm form = makePostForm(title, summary, content);
        Long id = postService.savePost(form);

        //when
        PostResponseDto responseDto = postService.findPostById(id);

        //then
        assertEquals(id, responseDto.getId());
        assertEquals(title, responseDto.getTitle());
        assertEquals(summary, responseDto.getSummary());
        assertEquals(content, responseDto.getContent());
    }

    @Test
    public void 게시글수정() {
        //given
        String title1 = "title1";
        String summary1 = "summary1";
        String content1 = "content1";
        PostForm form = makePostForm(title1, summary1, content1);
        Long id = postService.savePost(form);

        PostForm updatePostForm = postService.getUpdatePostForm(id);
        String title2 = "title2";
        String summary2 = "summary2";
        String content2 = "content2";
        updatePostForm.setTitle(title2);
        updatePostForm.setSummary(summary2);
        updatePostForm.setContent(content2);

        //when
        Long updatedId = postService.updatePost(id, updatePostForm);

        //then
        PostResponseDto responseDto = postService.findPostById(updatedId);
        assertEquals(id, updatedId);
        assertEquals(title2, responseDto.getTitle());
        assertEquals(summary2, responseDto.getSummary());
        assertEquals(content2, responseDto.getContent());
    }

    @Test
    public void 게시글삭제() {
        //given
        String title = "test title";
        String summary = "test summary";
        String content = "test content";
        PostForm form = makePostForm(title, summary, content);
        Long id = postService.savePost(form);

        //when
        postService.deletePost(id);

        //then
        List<Post> postList = postRepository.findAll();
        assertEquals(Collections.EMPTY_LIST, postList);
    }

    private PostForm makePostForm(String title, String summary, String content) {
        PostForm form = new PostForm();
        form.setTitle(title);
        form.setSummary(summary);
        form.setContent(content);
        return form;
    }


}