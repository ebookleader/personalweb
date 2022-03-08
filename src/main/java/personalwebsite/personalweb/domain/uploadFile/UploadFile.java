package personalwebsite.personalweb.domain.uploadFile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personalwebsite.personalweb.domain.posts.Post;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 1000)
    private String filePath;

    @Column(nullable = false, length = 1000)
    private String saveFileName;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Column(nullable = false)
    private Long size;

    private String reference; // null or yes

    private Long postId;

    @Builder
    public UploadFile(String fileName, String filePath, String saveFileName, LocalDateTime registerDate, Long size, String reference) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.saveFileName = saveFileName;
        this.registerDate = registerDate;
        this.size = size;
        this.reference = reference;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

}
