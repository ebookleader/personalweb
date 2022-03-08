package personalwebsite.personalweb.service;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
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
    PostService postService;

    @Autowired
    FileService fileService;

    @After
    public void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    public void 게시글저장_조회() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long id = postService.savePost(requestDto);

        //when
        PostResponseDto responseDto = postService.findPostById(id);

        //then
        assertEquals(id, responseDto.getId());
        assertEquals(title, responseDto.getTitle());
        assertEquals(content, responseDto.getContent());
    }

    @Test
    public void 게시글수정() {
//        //given
//        String title1 = "title1";
//        String content1 = "title2";
//        PostForm saveRequestDto = new PostForm();
//        saveRequestDto.setTitle(title1);
//        saveRequestDto.setContent(content1);
//        Long id = postService.savePost(saveRequestDto);
//
//        String title2 = "title2";
//        String content2 = "content2";
//        PostUpdateRequestDto updateRequestDto = PostUpdateRequestDto.builder()
//                .title(title2)
//                .content(content2)
//                .build();
//        //when
//        Long updatedId = postService.updatePost(id, updateRequestDto);
//
//        //then
//        PostResponseDto responseDto = postService.findPostById(updatedId);
//        assertEquals(id, updatedId);
//        assertEquals(title2, responseDto.getTitle());
//        assertEquals(content2, responseDto.getContent());
    }

    @Test
    public void 게시글삭제() {
        //given
        String title = "test title";
        String content = "test content";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long id = postService.savePost(requestDto);

        //when
        postService.deletePost(id);

        //then
        List<Post> postList = postRepository.findAll();
        assertEquals(Collections.EMPTY_LIST, postList);
    }

    @Test
    public void jsoup테스트() {
        String title = "test title";
        String content = "<p><img src=\"/summernoteImage/13\" style=\"width: 25%;\"><img src=\"/summernoteImage/21\" style=\"width: 25%;\"><br></p>";
        PostForm requestDto = new PostForm();
        requestDto.setTitle(title);
        requestDto.setContent(content);
        Long postId = postService.savePost(requestDto);
        fileService.setPostIdForImage(postId, content);
    }


}