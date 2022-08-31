package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.comments.Comment;
import personalwebsite.personalweb.domain.comments.CommentRepository;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.exception.CustomException;
import personalwebsite.personalweb.exception.ErrorCode;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 저장: 댓글이 저장될 post를 찾고 form에 담겨진 댓글을 저장한뒤 저장된 댓글의 id를 리턴한다.
     * @param postId post id
     * @param commentForm 저장할 댓글의 정보를 담은 dto
     * @return comment id
     */
    @Transactional
    public Long saveComments(Long postId, CommentForm commentForm) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (commentForm == null) {
            throw new CustomException(ErrorCode.EMPTY_OBJECT);
        }

        Comment comment = commentForm.toEntity();
        comment.setPost(post);

        return commentRepository.save(comment).getId();
    }

    /**
     * 댓글 전체 조회: post에 저장된 모든 댓글을 작성 날짜 기준 내림차순 정렬한 리스트를 리턴한다.
     * @param postId post id
     * @return 댓글 정보를 담은 response dto의 리스트
     */
    public Map<Integer, CommentListResponseDto> findAllComments(Long postId) {

        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedDateDesc(postId);
        Map<Integer, CommentListResponseDto> allComments = new HashMap<>();
        for (Comment comment : comments) {
            allComments.put(comment.getId().intValue(), new CommentListResponseDto(comment));
        }
        return allComments;
    }

    /**
     * 댓글 삭제: 댓글의 id로 댓글을 찾아 삭제한다.
     * @param id comment id
     */
    @Transactional
    public void deleteComment(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }

    /**
     * 댓글 저장 전 입력된 작성자이름이 저장된 작성자 이름들 중 중복되는 것이 있는지 확인해 중복여부를 리턴한다.
     * @param username 댓글 작성자 이름
     * @param postId post id
     * @return 중복 닉네임이 존재하면 true, 아니면 false
     */
    public void checkUniqueUsername(String username, Long postId) {

        if (username == null || username.equals("")) {
            log.error("username is null");
            throw new CustomException(ErrorCode.COMMENT_USERNAME_NULL);
        }
        if (commentRepository.existsByUsernameAndPostId(username, postId)) {
            log.error("username duplicated");
            throw new CustomException(ErrorCode.DUPLICATE_COMMENT_USERNAME);
        }
    }
}
