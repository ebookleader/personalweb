package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personalwebsite.personalweb.domain.user.BasicUser;
import personalwebsite.personalweb.domain.user.BasicUserRepository;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.exception.CustomException;
import personalwebsite.personalweb.exception.ErrorCode;
import personalwebsite.personalweb.web.dto.user.LoginForm;
import personalwebsite.personalweb.web.dto.user.UserResponseDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BasicUserRepository basicUserRepository;

//    public UserResponseDto getUserInfo(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//        UserResponseDto userResponseDto = null;  // 후에 repo Optional로 바꿔서 코드 수정 (map)
//        if(user != null) {
//            userResponseDto = new UserResponseDto(user);
//        }
//        return userResponseDto;
//    }

    public BasicUser saveBasicUser(LoginForm form) {

        if (form == null) {
            throw new CustomException(ErrorCode.EMPTY_OBJECT);
        }

        BasicUser b = basicUserRepository.save(form.toEntity());
        return b;
    }

}
