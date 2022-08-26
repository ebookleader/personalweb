package personalwebsite.personalweb.domain.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostIdOrderByCreatedDateDesc(Long postId);

    boolean existsByUsernameAndPostId(String username, Long postId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.post.id=:postId")
    int deleteByPostId(@Param("postId") Long postId);
}
