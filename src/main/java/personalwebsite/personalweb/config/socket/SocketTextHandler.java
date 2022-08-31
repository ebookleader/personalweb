package personalwebsite.personalweb.config.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import personalwebsite.personalweb.config.auth.dto.SessionUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SocketTextHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> users = new ConcurrentHashMap<String, WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { // 클라이언트 서버 연결

        log.info(">>>>>>>> socket connection established");
        String userId = getUserId(session);
        if (userId != null) {
            log.info(">>>>> session user Id {} connected", userId);
            users.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String msg = message.getPayload(); // 자바스크립트에서 넘어온 메세지
        if (!checkStringNullEmpty(msg)) {
            String[] strings = msg.split(",");
            // type, commentUser, writer, postId, postTitle
            if (strings.length == 5) {
                String type = strings[0];
                String commentUser = strings[1];
                String writer = strings[2];
                String postId = strings[3];
                String postTitle = strings[4];

                WebSocketSession postWriterSession = users.get(writer);

                if (type.equals("comment") && postWriterSession!=null) {
                    log.info(">>>>>> comment session");
                    TextMessage textMessage = new TextMessage(
                            commentUser+"님이"+"<a href='/posts/"+postId+"'>"+postTitle+"에 댓글을 작성하였습니다.</a>"
                    );
                    postWriterSession.sendMessage(textMessage);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(">>>>>> socket closed");
        users.remove(getUserId(session), session);
    }

    private String getUserId(WebSocketSession session) {

        Map<String, Object> httpSession = session.getAttributes();
        String userId = ((SessionUser) httpSession.get("user")).getId().toString();
        return userId == null ? null : userId;
    }

    private boolean checkStringNullEmpty(String str) {
        return str == null || str.equals("");
    }
}
