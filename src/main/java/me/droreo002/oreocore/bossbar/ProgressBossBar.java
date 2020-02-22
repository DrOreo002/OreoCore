package me.droreo002.oreocore.bossbar;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.MathUtils;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class ProgressBossBar extends OreoBossBar {

    @Getter
    private boolean reversed;
    @Getter
    private double maxProgress;
    @Getter
    private String barTitle;
    @Getter @Setter
    private boolean automated;
    @Getter @Setter
    private double incrementPerTick, currentProgress;
    @Getter @Setter
    private BarColor halfwayColor;
    @Getter @Setter
    private SimpleCallback<ProgressBossBar> onDone;

    public ProgressBossBar(String barTitle, BarColor barColor, BarStyle barStyle, double cProgress, double mProgress, boolean reversed) {
        super(barTitle, barColor, barStyle);
        if ((mProgress % 10) > 1) throw new IllegalStateException("Max progress must be a multiplication of 10!");
        this.maxProgress = mProgress; // Default, cannot be changed
        this.currentProgress = cProgress;
        this.reversed = reversed;
        this.barTitle = barTitle;

        // Initialize
        if (!reversed) {
            getBossBar().setProgress(0);
            addAnimationFrame(new BBAnimationFrame() {
                @Override
                public double getProgress() {
                    return currentProgress;
                }

                @Override
                public String getTitle() {
                    return getCurrentBarTitle();
                }

                @Override
                public BarColor getColor() {
                    return getCurrentBarColor();
                }
            });
        } else {
            getBossBar().setProgress(1); // Set as full
            addAnimationFrame(new BBAnimationFrame() {
                @Override
                public double getProgress() {
                    // Why 1?, because boss bar progress value from 0.0 to 1
                    return 1 - currentProgress;
                }

                @Override
                public String getTitle() {
                    return getCurrentBarTitle();
                }

                @Override
                public BarColor getColor() {
                    return getCurrentBarColor();
                }
            });
        }
        setOnTick(aVoid -> {
            if (currentProgress >= 1) {
                if (onDone != null) onDone.success(this);
            }
            if (automated) {
                if (incrementPerTick == 0) throw new IllegalStateException("Increment per tick cannot be 0 if automated is enabled!");
                addProgress(incrementPerTick);
            }
        });
    }

    /**
     * Get the current bar color
     *
     * @return The current bar color
     */
    private BarColor getCurrentBarColor() {
        if (halfwayColor != null) {
            if (currentProgress >= (maxProgress / 2)) {
                System.out.println("Is halfway!");
                return halfwayColor;
            }
        }
        return this.getBossBar().getColor();
    }

    /**
     * Get the current bar title
     *
     * @return The current bar title
     */
    private String getCurrentBarTitle() {
        TextPlaceholder placeholder = TextPlaceholder
                .of("%maxProgress%", (int) maxProgress)
                .add("%currentProgress%", (int) Math.floor(currentProgress * maxProgress))
                .add("%percentage%", MathUtils.getPercentage(currentProgress, maxProgress));
        return placeholder.format(this.barTitle);
    }

    /**
     * Send this loading boss bar to player
     *
     * @param player The target player
     */
    public void start(Player player, long animationSpeed) {
        super.send(player, new BBAnimationProperties(false, true, animationSpeed));
    }

    /**
     * Add a progress
     */
    public void addProgress(double progress) {
        if (progress < 0) throw new IllegalStateException("Progress cannot be less than 0!");
        if (progress >= 1) progress = progress / maxProgress;
        this.currentProgress += progress;
    }
}
