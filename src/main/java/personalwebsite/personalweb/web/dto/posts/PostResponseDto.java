package personalwebsite.personalweb.web.dto.posts;

import lombok.Getter;
import personalwebsite.personalweb.domain.posts.Post;
import java.time.format.DateTimeFormatter;

@Getter
public class PostResponseDto {

    private Long id;
    private Long writerId;
    private String title;
    private String summary;
    private String content;
    private String modifiedDate;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.writerId = post.getUser().getId();
        this.title = post.getTitle();
        this.summary = post.getSummary();
        this.content = post.getContent();
        this.modifiedDate = post.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
