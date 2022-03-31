package personalwebsite.personalweb.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personalwebsite.personalweb.domain.BaseTimeEntity;
import personalwebsite.personalweb.domain.comments.Comment;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.domain.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @Builder
    public Post(String title, String content, String summary) {
        this.title = title;
        this.content = content;
        this.summary = summary;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void update(String title, String summary, String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }
}
