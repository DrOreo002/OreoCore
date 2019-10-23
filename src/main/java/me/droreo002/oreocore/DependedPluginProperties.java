package me.droreo002.oreocore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DependedPluginProperties {
    private boolean privatePlugin;
    private boolean premiumPlugin;
    private boolean enableLogging;
}
