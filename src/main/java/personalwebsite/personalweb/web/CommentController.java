package personalwebsite.personalweb.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.service.CommentService;
import personalwebsite.personalweb.web.dto.Message;
import personalwebsite.personalweb.web.dto.comments.AlarmListResponseDto;
import personalwebsite.personalweb.web.dto.comments.CommentForm;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final HttpSession httpSession;

    /** 댓글을 저장하고 새로운 댓글에 대한 알림정보를 리턴한다. (ajax에서 사용) */
    @PostMapping("/postComment/{postId}")
    @ResponseBody
    public AlarmListResponseDto saveComment(@PathVariable Long postId, @RequestBody CommentForm commentForm) {

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

    /** 알림을 클릭하면 해당 알림이 등록된 게시글로 이동하고 알림 체크 여부를 설정해준다. */
    @GetMapping("/alarm/{alarmId}")
    public String checkAlarm(@PathVariable Long alarmId) {

        Long postId = commentService.checkAlarm(alarmId);
        return "redirect:/posts/" + postId;
    }
}
