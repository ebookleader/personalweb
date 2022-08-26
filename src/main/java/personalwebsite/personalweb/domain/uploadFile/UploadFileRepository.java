package personalwebsite.personalweb.domain.uploadFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findAllByPostId(Long postId);

    List<UploadFile> findAllByPostIdAndReferenceIsNull(Long postId);

    UploadFile findByPostIdAndReference(Long postId, String reference);

    List<UploadFile> findAllByTempIsNotNull();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from UploadFile f where f.post.id=:postId")
    int deleteByPostId(@Param("postId") Long postId);
}
