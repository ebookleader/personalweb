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
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final HttpSession httpSession;

    /** 댓글을 저장하고 결과에 따라 결과 메시지와 이동할 주소를 넣은 ModelAndView 객체를 리턴한다. */
    @PostMapping("/postComment")
    public ModelAndView saveComment(@RequestParam("postId") Long postId, @ModelAttribute CommentForm commentForm, ModelAndView mav) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        boolean isDuplicated = false; // 관리자는 여러개의 댓글 입력 가능

        if (user == null) { // 관리자가 아닌 경우 중복 닉네임 체크
            isDuplicated = commentService.checkUniqueUsername(commentForm.getUsername(), postId);
        }

        String hre = "/posts/"+postId;

        if (isDuplicated) { // 중복 닉네임이 있을 경우
            mav.addObject("data", new Message("comment save failed (duplicated username)", hre));
        }
        else {  // 중복된 닉네임이 없는 경우, 댓글 저장
            Long commentId = commentService.saveComments(postId, commentForm);
            mav.addObject("data", new Message("comment save success", hre));
        }
        mav.setViewName("message");

        return mav;
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
