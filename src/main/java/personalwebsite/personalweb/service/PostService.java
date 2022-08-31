package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.comments.CommentRepository;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.domain.uploadFile.UploadFileRepository;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.exception.CustomException;
import personalwebsite.personalweb.exception.ErrorCode;
import personalwebsite.personalweb.web.dto.posts.PostListResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UploadFileRepository fileRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final HttpSession httpSession;

    /**
     * 게시글 저장: post를 저장하고 저장된 post의 id를 리턴한다.
     * @param postForm 저장할 게시글의 정보를 담은 dto
     * @return post id
     */
    @Transactional
    public Long savePost(PostForm postForm) {

        if (postForm == null) {
            throw new CustomException(ErrorCode.EMPTY_OBJECT);
        }

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        User user = userRepository.findByEmail(sessionUser.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postForm.toEntity();
        post.setUser(user);

        return postRepository.save(post).getId();
    }

    /**
     * 게시글 불러오기: post를 찾아 해당 post의 상세 정보를 리턴한다.
     * @param id post id
     * @return 게시글 정보를 담은 response dto
     */
    public PostResponseDto findPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return new PostResponseDto(post);
    }

    /**게시글 전부 불러오기 */
    public Map<Integer, PostListResponseDto> findAllPosts() {

        List<Post> allPosts = postRepository.findAll();
        Map<Integer, PostListResponseDto> result = new HashMap<>();

        for (Post post : allPosts) {

            Document doc = Jsoup.parse(post.getContent());
            Element el = doc.select("img").first();
            UploadFile thumbnail = null;
            if (el != null) {
                Long imgId = Long.parseLong(el.attr("src").substring(17)); // 17 ~
                thumbnail = fileRepository.findById(imgId)
                        .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND)); // 첨부파일 x & 게시글에 포함된 이미지 중 첫번째 이미지
            }

            PostListResponseDto dto;
            if (thumbnail == null) {
                dto = new PostListResponseDto(post, null);
            }
            else {
                dto = new PostListResponseDto(post, thumbnail.getId());
            }
            result.put(post.getId().intValue(), dto);
        }

        return result;
    }

    /**
     * id로 찾은 post의 정보를 수정 페이지에서 쓰일 form에 담아 리턴한다.
     * @param id post id
     * @return post 정보를 담은 form
     */
    public PostForm getUpdatePostForm(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostForm form = new PostForm(post.getId(), post.getTitle(), post.getSummary(), post.getContent());
        return form;
    }

    /**
     * 게시글 수정: postForm에 담긴 내용으로 post 업데이트후 업데이트된 post id를 리턴한다.
     * @param id post id
     * @param postForm 수정된 내용을 담은 게시글 dto
     * @return 수정된 post id
     */
    @Transactional
    public Long updatePost(Long id, PostForm postForm) {

        if (postForm == null) {
            throw new CustomException(ErrorCode.EMPTY_OBJECT);
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.update(postForm.getTitle(), postForm.getSummary(), postForm.getContent());

        return id;
    }

    /**
     * 게시글 삭제: post를 찾아 게시글을 삭제한다.
     * 댓글과 업로드 파일도 같이 삭제
     * @param id post id
     */
    @Transactional
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        int deletedCommentCnt = commentRepository.deleteByPostId(id);
        log.info("deleted comment count: {}", deletedCommentCnt);

        int deletedFileCnt = fileRepository.deleteByPostId(id);
        log.info("deleted file count: {}", deletedFileCnt);

        postRepository.delete(post);
    }

}
