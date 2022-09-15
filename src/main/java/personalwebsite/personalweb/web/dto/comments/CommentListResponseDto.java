package personalwebsite.personalweb.web.dto.comments;

import lombok.Getter;
import personalwebsite.personalweb.domain.comments.Comment;
import java.time.format.DateTimeFormatter;

@Getter
public class CommentListResponseDto {

    private Long commentId;
    private String username;
    private String text;
    private String modifiedDate;

    public CommentListResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.username = comment.getUsername();
        this.text = comment.getText();
        this.modifiedDate = comment.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
