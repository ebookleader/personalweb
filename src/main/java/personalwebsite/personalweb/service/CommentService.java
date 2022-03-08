package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.comments.Comment;
import personalwebsite.personalweb.domain.comments.CommentRepository;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
import personalwebsite.personalweb.web.advice.RestException;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
import personalwebsite.personalweb.web.dto.comments.CommentSaveRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 저장
     */
    @Transactional
    public Long saveComments(Long postId, CommentForm commentForm) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = "+postId));
        Comment comment = commentForm.toEntity();
        comment.setPost(post);
        return commentRepository.save(comment).getId();
    }

    /** 댓글 전체 조회 */
    public List<CommentListResponseDto> findAllComments(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedDateDesc(postId).stream()
                .map(CommentListResponseDto::new).collect(Collectors.toList());
    }

    /** 댓글 삭제 */
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id = "+id));
        commentRepository.delete(comment);
    }

    /** 댓글 비밀번호 일치 확인 */
    public boolean checkCommentPassword(String password, Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id = "+id));
        System.out.println("commentpassword: "+comment.getPassword());
        System.out.println("inputpassword: "+password);
        if (comment.getPassword().equals(password)) {
            return true;
        }
        else {
            return false;
        }
    }

    /** 중복 닉네임 확인 */
    public boolean checkUniqueUsername(String username, Long postId) {
        return commentRepository.existsByUsernameAndPostId(username, postId);
    }
}
