package personalwebsite.personalweb.domain.alarm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personalwebsite.personalweb.domain.BaseTimeEntity;
import personalwebsite.personalweb.domain.user.User;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    private Long postId;

    private String message;

    private int checked; // 0 == no, 1 == yes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 알림이 등록된 게시글을 작성한 유저

    @Builder
    public Alarm(Long postId, String message, int checked) {
        this.postId = postId;
        this.message = message;
        this.checked = checked;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCheckedYes() {
        this.checked = 1;
    }

}
