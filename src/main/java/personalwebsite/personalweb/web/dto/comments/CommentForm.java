package personalwebsite.personalweb.web.dto.comments;

import lombok.Getter;
import lombok.Setter;
import personalwebsite.personalweb.domain.comments.Comment;

@Getter @Setter
public class CommentForm {

    private String username;
    private String text;

    public Comment toEntity() {
        return Comment.builder()
                .username(username)
                .text(text)
                .build();
    }

}
