package personalwebsite.personalweb.domain.uploadFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    UploadFile findFirstByPostId(Long postId);

    List<UploadFile> findAllByPostIdAndReferenceIsNull(Long postId);

    UploadFile findByPostIdAndReference(Long postId, String reference);

    UploadFile findFirstByPostIdAndReferenceIsNull(Long postId);

    @Transactional
    void deleteByPostId(Long postId);

    Long countByPostId(Long postId);
}
