package personalwebsite.personalweb.web.dto.posts;

import lombok.Getter;
import personalwebsite.personalweb.domain.posts.Post;

import java.time.LocalDateTime;

@Getter
public class PostListResponseDto {

    private Long postId;
    private String title;
    private String summary;
    private Long thumbnailId;

    public PostListResponseDto(Post post, Long thumbnailId) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.summary = post.getSummary();
        this.thumbnailId = thumbnailId;
    }
}
