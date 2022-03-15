package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.service.FileService;
import personalwebsite.personalweb.service.PostService;
import personalwebsite.personalweb.web.dto.FileResponseDto;
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

    /** 지울거 **/
    @GetMapping(value = "/index")
    public String index() {
        return "index";
    }

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

    @GetMapping(value = "/admin/home")
    public String adminHome(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        List<PostListResponseDto> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "adminHome";
    }

    @GetMapping(value = "/admin/posts/write")
    public String writePost(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("postForm", new PostForm());
        return "writePost";
    }

    @GetMapping(value = "/posts/{postId}")
    public String getPostDetail(Model model, @PathVariable Long postId) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("post", postService.findPostById(postId));
        if (fileService.checkPostHasAttachment(postId)) {
            model.addAttribute("attach", fileService.findReferenceByPostId(postId));
        }
        model.addAttribute("commentForm", new CommentForm());
        List<CommentListResponseDto> comments = commentService.findAllComments(postId);
        model.addAttribute("comments", comments);
        return "postDetail";
    }

    @GetMapping(value = "/admin/posts/update/{postId}")
    public String updatePostForm(Model model, @PathVariable Long postId) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("postForm", postService.getUpdatePostForm(postId));
        return "updatePost";
    }

}
