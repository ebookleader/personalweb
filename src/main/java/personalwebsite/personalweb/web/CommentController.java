package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.web.dto.Message;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import personalwebsite.personalweb.web.dto.comments.CommentListResponseDto;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final HttpSession httpSession;

    /** 댓글을 저장하고 결과에 따라 결과 메시지와 이동할 주소를 넣은 ModelAndView 객체를 리턴한다. */
    @PostMapping("/postComment/{postId}")
    public Long saveComment(@PathVariable Long postId, @RequestBody CommentForm commentForm) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            commentService.checkUniqueUsername(commentForm.getUsername(), postId);
        }

        return commentService.saveComments(postId, commentForm);
    }

    /** 댓글을 삭제하고 삭제 성공 메시지와 이동할 주소를 넣은 ModelAndView 객체를 리턴한다. */
    @DeleteMapping("/admin/postComment/{commentId}")
    public ModelAndView deleteComment(@RequestParam("postId") Long postId, @PathVariable Long commentId, ModelAndView mav) {

        commentService.deleteComment(commentId);

        String hre = "/posts/"+postId;

        mav.addObject("data", new Message("delete comment success", hre));
        mav.setViewName("message");

        return mav;
    }
}
