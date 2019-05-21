package me.droreo002.oreocore.debugging;

import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/*
This class is for management only, so I don't need to copy and paste Debug class
 */
public abstract class Debugger {

    public abstract LogFile getLogFile();
    public abstract String getPrefix();
    public abstract boolean usePrefixLogFile();

    /**
     * Log the msg
     *
     * @param msg : The message
     * @param logLevel : The log level
     * @param addPrefix : Should we add prefix?
     * @param logToFile : Should we log it to file?
     */
    public void log(String msg, Level logLevel, boolean addPrefix, boolean logToFile) {
        if (addPrefix) {
            msg = getPrefix() + msg;
        }
        if (logToFile) {
            if (getLogFile() == null) return;
            if (!usePrefixLogFile()) msg = msg.replace(getPrefix(), "");
            getLogFile().getLogger().log(logLevel, StringUtils.stripColor(msg));
        }
        sendConsoleMessage(msg);
    }

    /**
     * Log the msg
     *
     * @param msg : The message
     * @param logLevel : The log level
     * @param logToFile : Should we log it to file?
     */
    public void log(String msg, Level logLevel, boolean logToFile) {
        if (logToFile) {
            if (getLogFile() == null) return;
            getLogFile().getLogger().log(logLevel, StringUtils.stripColor(msg));
        }
        sendConsoleMessage(msg);
    }

    private void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.color(msg));
    }
}
