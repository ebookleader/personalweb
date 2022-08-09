package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.exception.ErrorCode;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.service.FileService;
import personalwebsite.personalweb.service.PostService;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostListResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;
    private final PostService postService;
    private final CommentService commentService;
    private final FileService fileService;

    /** 메인페이지로 이동한다. */
    @GetMapping(value = "/")
    public String home(Model model) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        List<PostListResponseDto> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);

        return "home";
    }

    /** 로그인페이지로 이동한다. */
    @GetMapping(value = "/loginPage")
    public String loginPage(@RequestParam(defaultValue = "false") String error, Model model) {
        if (error.equals("true")) {
            model.addAttribute("errorMessage", ErrorCode.ALREADY_SIGNUP.getMessage());
        }
        return "beforeLogin";
    }


    /** 게시글 작성 페이지로 이동한다. */
    @GetMapping(value = "/admin/posts/write")
    public String writePost(Model model) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        model.addAttribute("postForm", new PostForm());

        return "writePost";
    }

    /** 게시글 상세 페이지로 이동한다. */
    @GetMapping(value = "/posts/{postId}")
    public String getPostDetail(Model model, @PathVariable Long postId) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        // 게시글 정보
        model.addAttribute("post", postService.findPostById(postId));
        if (fileService.checkPostHasAttachment(postId)) { // 첨부파일이 있는 경우
            model.addAttribute("attach", fileService.findReferenceByPostId(postId));
        }
        // 댓글 정보
        CommentForm commentForm = new CommentForm();
        if (user != null) {
            commentForm.setUsername(user.getName());
        }
        model.addAttribute("commentForm", commentForm); // 댓글 저장 form

        List<CommentListResponseDto> comments = commentService.findAllComments(postId);
        model.addAttribute("comments", comments);

        return "postDetail";
    }

    /** 게시글 수정 페이지로 이동한다. */
    @GetMapping(value = "/admin/posts/update/{postId}")
    public String updatePostForm(Model model, @PathVariable Long postId) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        // 수정 폼
        model.addAttribute("postForm", postService.getUpdatePostForm(postId));
        return "updatePost";
    }

}
