package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.user.BasicUser;
import personalwebsite.personalweb.service.UserService;
import personalwebsite.personalweb.web.dto.user.LoginForm;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;

    @PostMapping("/basicUserLogin")
    public String basicUserLogin(LoginForm form) {
        BasicUser user = userService.saveBasicUser(form);
        httpSession.setAttribute("user", new SessionUser(user));
        return "redirect:/";
    }
}
