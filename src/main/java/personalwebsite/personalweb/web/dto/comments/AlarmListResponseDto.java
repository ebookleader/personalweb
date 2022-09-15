package personalwebsite.personalweb.web.dto.comments;

import lombok.Getter;
import personalwebsite.personalweb.domain.alarm.Alarm;
import java.time.format.DateTimeFormatter;

@Getter
public class AlarmListResponseDto {

    private Long id;
    private String message;
    private Long postId;
    private String createdDate;

    public AlarmListResponseDto(Alarm alarm) {
        this.id = alarm.getId();
        this.message = alarm.getMessage();
        this.postId = alarm.getPostId();
        this.createdDate = alarm.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
