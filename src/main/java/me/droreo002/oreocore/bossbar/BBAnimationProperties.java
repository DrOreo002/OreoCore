package me.droreo002.oreocore.bossbar;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BBAnimationProperties {
    private boolean addFirstState;
    private boolean repeating;
    private long animationSpeed; // Speed in tick
}
