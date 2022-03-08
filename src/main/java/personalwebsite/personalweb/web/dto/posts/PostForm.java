package personalwebsite.personalweb.web.dto.posts;

import lombok.*;
import personalwebsite.personalweb.domain.posts.Post;

@Getter @Setter
public class PostForm {

    private Long id;

    private String title;
    private String summary;
    private String content;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .summary(summary)
                .content(content)
                .build();
    }
}
