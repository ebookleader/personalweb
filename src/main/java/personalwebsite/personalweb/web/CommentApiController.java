package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.web.dto.Message;
import personalwebsite.personalweb.web.dto.comments.CommentForm;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/api/posts/comments")
    public String saveComment(@RequestParam("postId") Long postId, @ModelAttribute CommentForm commentForm) {
        Long commentId = commentService.saveComments(postId, commentForm);
        return "redirect:/posts/"+postId;
    }

    @DeleteMapping("/api/posts/comments/{commentId}")
    public ModelAndView deleteComment(@RequestParam("postId") Long postId, @PathVariable Long commentId, ModelAndView mav) {
        commentService.deleteComment(commentId);
        String hre = "/posts/"+postId;
        mav.addObject("data", new Message("delete comment success", hre));
        mav.setViewName("message");
        return mav;
    }
}
