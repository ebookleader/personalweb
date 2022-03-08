package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.domain.uploadFile.UploadFileRepository;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.web.dto.posts.PostListResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UploadFileRepository fileRepository;
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    /** 게시글 저장 */
    @Transactional
    public Long savePost(PostForm postForm) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        User user = userRepository.findByEmail(sessionUser.getEmail());
        Post post = postForm.toEntity();
        post.setUser(user);
        return postRepository.save(post).getId();
    }

    /**게시글 전부 불러오기 */
    public List<PostListResponseDto> findAllPosts() {
        List<Post> allPosts = postRepository.findAll();
        List<PostListResponseDto> postList = new ArrayList<>();
        for (Post post : allPosts) {
            UploadFile thumbnail = fileRepository.findFirstByPostId(post.getId());
            PostListResponseDto dto = new PostListResponseDto(post, thumbnail.getId());
            postList.add(dto);
        }
        return postList;
    }

    /**특정 게시글 불러오기 */
    public PostResponseDto findPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = "+id));
        return new PostResponseDto(post);
    }

    public PostForm getUpdatePostForm(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = "+id));
        PostForm form = new PostForm();
        form.setId(post.getId());
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        form.setSummary(post.getSummary());
        return form;
    }

    /** 게시글 수정 */
    @Transactional
    public Long updatePost(Long id, PostForm postForm) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = "+id));
        post.update(postForm.getTitle(), postForm.getSummary(), postForm.getContent());
        return id;
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));
        postRepository.delete(post);
    }

}
