package personalwebsite.personalweb.web.dto.user;

import lombok.Getter;
import lombok.Setter;
import personalwebsite.personalweb.domain.user.BasicUser;
import personalwebsite.personalweb.domain.user.Role;

@Getter
@Setter
public class LoginForm {

    private String name;
    private String email;

    public BasicUser toEntity() {
        return BasicUser.builder()
                .name(name)
                .email(email)
                .role(Role.GUEST)
                .build();
    }
}
