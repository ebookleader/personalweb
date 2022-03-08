package personalwebsite.personalweb.web.dto.comments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personalwebsite.personalweb.domain.comments.Comment;

@Getter
@NoArgsConstructor
public class CommentSaveRequestDto {

    private String username;
    private String password;
    private String text;

    @Builder
    public CommentSaveRequestDto(String username, String password, String text) {
        this.username = username;
        this.password = password;
        this.text = text;
    }

    public Comment toEntity() {
        return Comment.builder()
                .username(username)
                .password(password)
                .text(text)
                .build();
    }
}
