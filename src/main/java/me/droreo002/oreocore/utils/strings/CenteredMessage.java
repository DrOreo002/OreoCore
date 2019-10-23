package me.droreo002.oreocore.utils.strings;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Originally Char enum
 *
 * @author https://www.spigotmc.org/threads/message-centering.210080/
 */
public enum CenteredMessage {

    A('A', 5),
    a('a', 5),
    B('B', 5),
    b('b', 5),
    C('C', 5),
    c('c', 5),
    D('D', 5),
    d('d', 5),
    E('E', 5),
    e('e', 5),
    F('F', 5),
    f('f', 4),
    G('G', 5),
    g('g', 5),
    H('H', 5),
    h('h', 5),
    I('I', 3),
    i('i', 1),
    J('J', 5),
    j('j', 5),
    K('K', 5),
    k('k', 4),
    L('L', 5),
    l('l', 1),
    M('M', 5),
    m('m', 5),
    N('N', 5),
    n('n', 5),
    O('O', 5),
    o('o', 5),
    P('P', 5),
    p('p', 5),
    Q('Q', 5),
    q('q', 5),
    R('R', 5),
    r('r', 5),
    S('S', 5),
    s('s', 5),
    T('T', 5),
    t('t', 4),
    U('U', 5),
    u('u', 5),
    V('V', 5),
    v('v', 5),
    W('W', 5),
    w('w', 5),
    X('X', 5),
    x('x', 5),
    Y('Y', 5),
    y('y', 5),
    Z('Z', 5),
    z('z', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('a', 4);

    private static final int CENTER_PX = 127;
    private static final int MAX_PX = 240;

    private static final int CENTER_CHAT_PX = 154;
    private static final int MAX_CHAT_PX = 250;

    @Getter
    private char character;
    @Getter
    private int length;

    CenteredMessage(char character, int length) {
        this.character = character;
        this.length = length;
    }

    /**
     * Get the default font info
     *
     * @param c The character
     * @return The default font information
     */
    public static CenteredMessage getDefaultFontInfo(char c) {
        for (CenteredMessage dFI : CenteredMessage.values()) {
            if (dFI.getCharacter() == c) {
                return dFI;
            }
        }
        return CenteredMessage.DEFAULT;
    }

    /**
     * Center the message
     *
     * @param message The message tobe centered
     * @return The centered message
     */
    public static String centerMessage(String message) {
        message = StringUtils.color(message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        String toSendAfter = null;
        String recentColorCode = "";
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                recentColorCode = "§" + c;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else if (c == ' ') {
                lastSpaceIndex = charIndex;
            } else {
                CenteredMessage dFI = CenteredMessage.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            if (messagePxSize >= MAX_PX) {
                toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1);
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }
            charIndex++;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = CenteredMessage.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        if (toSendAfter != null) centerMessage(toSendAfter);
        return sb.toString() + message;
    }

    /**
     * Send a centered message to player
     * Version number 2
     *
     * @param message The message tobe centered and send to player
     * @return The centered message
     */
    public static String centerMessageV2(String message) {
        message = StringUtils.color(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                CenteredMessage dFI = CenteredMessage.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = CenteredMessage.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;
    }

    /**
     * Send a centered message to player
     * Version number 3
     *
     * @param message The message tobe centered and send to player
     * @return The centered message
     */
    public static String centerMessageV3(String message) {
        message = StringUtils.color(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        String toSendAfter = null;
        String recentColorCode = "";
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                recentColorCode = "§" + c;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else if (c == ' ') {
                lastSpaceIndex = charIndex;
            } else {
                CenteredMessage dFI = CenteredMessage.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            if (messagePxSize >= MAX_CHAT_PX) {
                toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1);
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }
            charIndex++;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_CHAT_PX - halvedMessageSize;
        int spaceLength = CenteredMessage.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        if (toSendAfter != null) {
            return centerMessageV2(toSendAfter);
        } else {
            return sb.toString() + message;
        }
    }

    /**
     * Get the bold length for this character
     *
     * @return The bold length
     */
    public int getBoldLength() {
        if (this == CenteredMessage.SPACE) {
            return this.getLength();
        }
        return this.length + 1;
    }
}

