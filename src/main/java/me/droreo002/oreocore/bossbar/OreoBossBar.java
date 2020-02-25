package me.droreo002.oreocore.bossbar;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OreoBossBar {

    @Getter
    private BossBar bossBar;
    @Getter
    private List<BBAnimationFrame> animationFrames;
    @Getter
    private int animationTask;
    @Getter
    private SimpleCallback<Void> onTick;
    @Getter
    private BBAnimationProperties animationProperties;

    public OreoBossBar(String barTitle) {
        this.bossBar = Bukkit.createBossBar(barTitle, BarColor.WHITE, BarStyle.SOLID);
        this.animationFrames = new ArrayList<>();
    }

    public OreoBossBar(String barTitle, BarStyle barStyle) {
        this.bossBar = Bukkit.createBossBar(barTitle, BarColor.WHITE, barStyle);
        this.animationFrames = new ArrayList<>();
    }

    public OreoBossBar(String barTitle, BarColor barColor, BarStyle barStyle) {
        this.bossBar = Bukkit.createBossBar(barTitle, barColor, barStyle);
        this.animationFrames = new ArrayList<>();
    }

    /**
     * Add an animation frame
     *
     * @param frame The frame to add
     * @return OreoBossBar
     */
    public OreoBossBar addAnimationFrame(BBAnimationFrame frame) {
        this.animationFrames.add(frame);
        return this;
    }

    /**
     * Set the on tick listener
     *
     * @param onTick The listener
     * @return OreoBossBar
     */
    public OreoBossBar setOnTick(SimpleCallback<Void> onTick) {
        this.onTick = onTick;
        return this;
    }

    /**
     * Send this boss bar to player
     *
     * @param player The target player
     * @param animationProperties The animation properties
     */
    public void send(Player player, @Nullable BBAnimationProperties animationProperties) {
        bossBar.addPlayer(player);
        if (animationProperties != null) {
            this.animationProperties = animationProperties;
            if (animationProperties.isAddFirstState()) {
                // Add first state first
                addAnimationFrame(new BBAnimationFrame() {
                    @Override
                    public double getProgress() {
                        return -1; // We don't really want to use this
                    }

                    @Override
                    public String getTitle() {
                        return getBossBar().getTitle();
                    }

                    @Override
                    public BarColor getColor() {
                        return getBossBar().getColor();
                    }
                });
            }
            this.animationTask = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), new AnimationExecutor(), 0L, animationProperties.getAnimationSpeed()).getTaskId();
        }
    }

    /**
     * Remove this boss bar
     */
    public void remove() {
        bossBar.removeAll();
        if (animationTask != 0) Bukkit.getScheduler().cancelTask(animationTask);
    }

    public class AnimationExecutor implements Runnable {

        private int currentFrame;

        public AnimationExecutor() {
            this.currentFrame = 0;
        }

        @Override
        public void run() {
            // Every added player is offline
            if (bossBar.getPlayers().stream().noneMatch(Player::isOnline)) {
                remove();
                return;
            }
            if (onTick != null) onTick.success(null);
            if (currentFrame >= getAnimationFrames().size()) {
                if (animationProperties.isRepeating()) {
                    currentFrame = 0;
                } else {
                    return;
                }
            }
            BBAnimationFrame frame = getAnimationFrames().get(currentFrame);
            bossBar.setTitle(frame.getTitle());
            double progress = frame.getProgress();
            if (!(progress < 0) && !(progress > 1.0D)) { // Because boss bar progress is 0.0 to 1.0
                bossBar.setProgress(progress);
            }
            bossBar.setColor(frame.getColor());
            currentFrame++;
        }
    }
}
