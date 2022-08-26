package personalwebsite.personalweb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import personalwebsite.personalweb.web.dto.Message;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

//    @ExceptionHandler(value = CustomException.class)
//    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
//        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
//    }

//    @ExceptionHandler(value = CustomException.class)
//    public String handleCustomException(CustomException e, Model model) {
//        model.addAttribute("exceptionMessage", ErrorResponseEntity.toResponseEntity(e.getErrorCode()).getBody().getMessage());
//        return "errorPage";
//    }

    @ExceptionHandler(value = CustomException.class)
    public ModelAndView handleCustomException(CustomException e, HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();
        String hre = request.getHeader("Referer");
        String message = ErrorResponseEntity.toResponseEntity(e.getErrorCode()).getBody().getMessage();
        mav.addObject("data", new Message(message, hre));
        mav.setViewName("message");
        return mav;
    }
}
