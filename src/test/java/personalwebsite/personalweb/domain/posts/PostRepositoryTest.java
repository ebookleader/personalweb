package personalwebsite.personalweb.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @After
    public void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        //given
        String title = "테스트 게시글 제목";
        String content = "테스트 본문";

        Post post = Post.builder().title(title).content(content).build();
        postRepository.save(post);

        //when
        List<Post> postList = postRepository.findAll();

        //then
        Post post1 = postList.get(0);
        assertEquals(title, post1.getTitle());
        assertEquals(content, post1.getContent());
    }
}