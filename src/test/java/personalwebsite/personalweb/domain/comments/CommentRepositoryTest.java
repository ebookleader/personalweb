package personalwebsite.personalweb.domain.comments;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @After
    public void tearDown() {
        commentRepository.deleteAll();
    }

    @Test
    public void 댓글저장_불러오기() {
        //given
        Post post = createPost("test1", "test content1");
        postRepository.save(post);

        String username = "test user1";
        String password = "1234";
        String text = "test text1";
        Comment comment = createComment(username, password, text);
        comment.setPost(post);
        commentRepository.save(comment);

        //when
        List<Comment> commentList = commentRepository.findAll();
        List<Post> postList = postRepository.findAll();

        //then
        Comment comment1 = commentList.get(0);
        Post post1 = postList.get(0);
        assertEquals(username, comment1.getUsername());
        assertEquals(password, comment1.getPassword());
        assertEquals(text, comment1.getText());
        assertEquals(post1.getId(), comment1.getPost().getId());
    }

    @Test
    public void 게시글댓글불러오기() {
        //given
        Post post1 = createPost("test1", "test content1");
        Post post2 = createPost("test2", "test content2");
        postRepository.save(post1);
        postRepository.save(post2);

        Comment comment1 = createComment("user1", "pass1", "text1");
        Comment comment2 = createComment("user2", "pass2", "text2");
        Comment comment3 = createComment("user3", "pass3", "text3");

        comment1.setPost(post1);
        comment2.setPost(post2);
        comment3.setPost(post2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        //when
        List<Post> postList = postRepository.findAll();
        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedDateDesc(postList.get(1).getId());

        //then
        Comment comment = commentList.get(0);
        assertEquals(2, commentList.size());
        assertEquals(comment2.getUsername(), comment.getUsername());
        assertEquals(postList.get(1).getId(), comment.getPost().getId());
    }


    private Post createPost(String title, String content) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();
        return post;
    }

    private Comment createComment(String username, String password, String text) {
        Comment comment = Comment.builder()
                .username(username)
                .password(password)
                .text(text)
                .build();
        return comment;
    }
}