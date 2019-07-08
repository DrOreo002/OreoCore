package me.droreo002.oreocore.utils.strings;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.inventory.Paginator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class PagedMessage {

    @Getter @Setter
    private List<TextBuilder> text;
    @Getter @Setter
    private int messagePerPage;
    @Getter @Setter
    private String borderUp;
    @Getter @Setter
    private String borderBottom;
    @Getter @Setter
    private TextButton nextPageButton;
    @Getter @Setter
    private TextButton prevPageButton;
    @Getter
    private int totalPages;
    @Getter
    private int currentPage;
    @Getter
    private Paginator<TextBuilder> paginator;
    @Getter
    private List<List<TextBuilder>> pages;

    public PagedMessage() {
        this.text = new ArrayList<>();
        this.messagePerPage = 4;
        this.currentPage = 0;
    }

    /**
     * Initialize it
     */
    public void init() {
        if (borderUp == null) {
            this.borderUp = color("&8&m––––––––––––––––––&r &7[ &b%currentPage &7/ &a%totalPage &7] &8&m––––––––––––––––––&r");
        }
        if (borderBottom == null) {
            this.borderBottom = color("&8&m––––––––––––––––––&r &7[ &r%prevPageButton &7| &r%nextPageButton &7] &8&m––––––––––––––––––&r");
        }
        if (messagePerPage == 0) {
            this.messagePerPage = 5;
        }
        this.paginator = new Paginator<>(text);
        this.pages = paginator.paginates(messagePerPage);
        this.totalPages = pages.size() - 1;
    }

    /**
     * Send message to player
     *
     * @param player Player to send
     * @param addSpace Should we add space after sending border?
     * @param page What page to open?
     * @throws NullPointerException if the PagedMessage is not initialized yet
     */
    public void send(Player player, boolean addSpace, int page) {
        if (paginator == null) throw new NullPointerException("Please initialize the PagedMessage first!");
        if (nextPageButton == null || prevPageButton == null) throw new NullPointerException("Next page or prev page button cannot be null!");
        if (page >= totalPages) return;
        if (page < 0) return;
        PlayerUtils.clearChat(player); // Clear chat first, just to make things cleaner

        this.currentPage = page;
        player.sendMessage(color(borderUp
                .replace("%currentPage", String.valueOf(currentPage + 1))
                .replace("%totalPage", String.valueOf(totalPages))));
        if (addSpace) player.sendMessage(" ");
        List<TextBuilder> texts = pages.get(page);
        texts.forEach(textBuilder -> textBuilder.send(player));
        if (addSpace) player.sendMessage(" ");
        final TextBuilder nextButton = TextBuilder.of(nextPageButton.getDisplayText())
                .setClickEvent(ClickEvent.Action.RUN_COMMAND,
                        nextPageButton.getCommandToRun().replace("%nextPage", String.valueOf(currentPage + 1)))
                .setHoverEvent(HoverEvent.Action.SHOW_TEXT, nextPageButton.getHoverMessage());
        final TextBuilder prevButton = TextBuilder.of(prevPageButton.getDisplayText())
                .setClickEvent(ClickEvent.Action.RUN_COMMAND,
                        prevPageButton.getCommandToRun().replace("%prevPage", String.valueOf(currentPage - 1)))
                .setHoverEvent(HoverEvent.Action.SHOW_TEXT, prevPageButton.getHoverMessage());

        TextBuilder borderBottom = new TextBuilder();
        for (String s : this.borderBottom.split(" ")) {
            borderBottom.addText(s).addText(" "); // Make sure it's added one by one
        }
        borderBottom.replace("%nextPageButton", nextButton.getList());
        borderBottom.replace("%prevPageButton", prevButton.getList());

        borderBottom.send(player);
    }

    /**
     * Add a message
     *
     * @param textBuilder The text builder to add
     */
    public void addMessage(TextBuilder textBuilder) {
        text.add(textBuilder);
    }

    /**
     * Add a message
     *
     * @param message The message to add
     */
    public void addMessage(String message) {
        text.add(TextBuilder.of(StringUtils.color(message)));
    }
}
