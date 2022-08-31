package personalwebsite.personalweb.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class BasicUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public BasicUser(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
