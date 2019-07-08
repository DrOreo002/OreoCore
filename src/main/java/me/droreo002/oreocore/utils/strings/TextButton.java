package me.droreo002.oreocore.utils.strings;

import lombok.Getter;
import lombok.Setter;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

@Getter
@Setter
public class TextButton {

    private String displayText;
    private String commandToRun;
    private String hoverMessage;

    public TextButton(String displayText, String commandToRun, String hoverMessage) {
        this.displayText = color(displayText);
        this.hoverMessage = color(hoverMessage);
        if (!commandToRun.startsWith("/")) {
            this.commandToRun = "/" + commandToRun;
        } else {
            this.commandToRun = commandToRun;
        }
    }
}
