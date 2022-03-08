package personalwebsite.personalweb.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import personalwebsite.personalweb.domain.user.Role;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOauth2UserService customOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable() // h2-console 화면을 사용하기위해 해당 옵션 disable
                .and()
                    .authorizeRequests()    // URL별 권한 관리 설정 시작점
                    .antMatchers("/", "/css/**", "/image/**", "/js/**", "/h2-console", "/posts/**", "/api/**").permitAll()
                    .antMatchers("/admin/**").hasRole(Role.USER.name())   // 사용자만 사용 가능
                .and()
                    .logout()
                        .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()
                        .userInfoEndpoint()
                            .userService(customOauth2UserService);
    }
}
