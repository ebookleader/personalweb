package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.user.Role;
import personalwebsite.personalweb.exception.ErrorCode;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.service.PostService;
import personalwebsite.personalweb.web.dto.posts.PostListResponseDto;
import personalwebsite.personalweb.web.dto.user.LoginForm;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;
    private final PostService postService;
    private final CommentService commentService;

    /** 메인페이지로 이동한다. */
    @GetMapping(value = "/")
    public String home(Model model) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("sessionUser", user);
            if (user.getRole() == Role.USER) {
                model.addAttribute("alarms", commentService.findAllUncheckedAlarms());
            }
        }

        Map<Integer, PostListResponseDto> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);

        return "home";
    }

    /** 로그인페이지로 이동한다. */
    @GetMapping(value = "/loginPage")
    public String loginPage(@RequestParam(defaultValue = "false") String error, Model model) {

        if (error.equals("true")) {
            model.addAttribute("errorMessage", ErrorCode.ALREADY_SIGNUP.getMessage());
        }
        model.addAttribute("loginForm", new LoginForm());
        return "beforeLogin";
    }


}
