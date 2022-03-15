package personalwebsite.personalweb.domain.comments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personalwebsite.personalweb.domain.BaseTimeEntity;
import personalwebsite.personalweb.domain.posts.Post;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false, length = 500)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
