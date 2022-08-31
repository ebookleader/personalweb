package personalwebsite.personalweb.config.auth.dto;

import lombok.Getter;
import personalwebsite.personalweb.domain.user.BasicUser;
import personalwebsite.personalweb.domain.user.Role;
import personalwebsite.personalweb.domain.user.User;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private Long id;
    private String name;
    private String email;
    private Role role;

    public SessionUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public SessionUser(BasicUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
