/*
    학번 : 2091193
    이름 : 최재영
 */

package week10;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class ChatMsg implements Serializable {
    public static final int MODE_LOGIN      = 0x1;
    public static final int MODE_LOGOUT     = 0x2;
    public static final int MODE_TX_STRING  = 0x10;
    public static final int MODE_TX_FILE    = 0x20;
    public static final int MODE_TX_IMAGE   = 0x40;

    String userId;
    int mode;
    String message;
    ImageIcon image;

    public ChatMsg(String userId, int code, String message, ImageIcon image) {
        this.userId = userId;
        this.mode = code;
        this.message = message;
        this.image = image;
    }

    public ChatMsg(String userId, int code) {
        this(userId, code, null, null);
    }

    public ChatMsg(String userId, int code, String message) {
        this(userId, code, message, null);
    }
}
