package me.droreo002.oreocore.actionbar;

import lombok.Getter;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.ChatColor;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

@Getter
public class ProgressActionBar extends OreoActionBar {

    private double currentProgress;
    private double maxProgress;
    private ChatColor baseProgressColor;
    private ChatColor progressColor;
    private char loadedProgressChar;
    private char defaultProgressChar;
    private String progressFormat;
    private SimpleCallback<Void> onDone;

    public ProgressActionBar(double currentProgress, double maxProgress, ChatColor baseProgressColor, ChatColor progressColor, char loadedProgressChar, char defaultProgressChar, String progressFormat) {
        super(generateLoadingBar(currentProgress, maxProgress, baseProgressColor, progressColor, loadedProgressChar, defaultProgressChar, progressFormat));
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.baseProgressColor = baseProgressColor;
        this.progressColor = progressColor;
        this.loadedProgressChar = loadedProgressChar;
        this.defaultProgressChar = defaultProgressChar;
        this.progressFormat = progressFormat;
    }

    /**
     * Set callback when progress is done
     *
     * @param onDone On done
     */
    public void setOnDone(SimpleCallback<Void> onDone) {
        this.onDone = onDone;
    }

    /**
     * Add a progress, use a negative value to decrease
     *
     * @param val The value to add
     */
    public void addProgress(double val) {
        currentProgress += val;
        if (currentProgress >= maxProgress && onDone != null) {
            onDone.success(null);
            stop(true);
            return;
        }
        setMessage(generateLoadingBar(currentProgress, maxProgress, baseProgressColor, progressColor, loadedProgressChar, defaultProgressChar, progressFormat));
    }
}
