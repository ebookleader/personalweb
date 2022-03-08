package personalwebsite.personalweb.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Message {

    String message = "";
    String href = "";

    public Message(String message, String href) {
        this.message = message;
        this.href = href;
    }
}
