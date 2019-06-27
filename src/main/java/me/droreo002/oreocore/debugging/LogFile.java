package me.droreo002.oreocore.debugging;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.io.FileUtils;
import me.droreo002.oreocore.utils.misc.TimeStampUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import sun.rmi.log.LogHandler;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public abstract class LogFile {

    @Getter
    private final TimeStampUtils utils;
    @Getter
    private final Logger logger;
    @Getter
    private final JavaPlugin owner;

    @Getter @Setter
    private String currentLogFileName;
    @Getter @Setter
    private FileHandler logHandler;
    @Getter @Setter
    private File currentLogFile;

    public LogFile(JavaPlugin owner) {
        this.owner = owner;
        this.utils = new TimeStampUtils((getTimestampFormat().contains("/")) ? getTimestampFormat().replace("/", "-") : getTimestampFormat());
        this.logger = Logger.getLogger(getLoggerName());
        this.currentLogFileName = getNextLogName();
        setup();
        if (getLogUpdateTime() != -1) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(getOwner(), new LogFileUpdater(), 0L, 20L * getLogUpdateTime());
        }
    }

    /**
     * Setup the log file
     */
    private void setup() {
        final File logsFolder = getLogFolder();

        if (!owner.getDataFolder().exists()) owner.getDataFolder().mkdir();
        if (!logsFolder.exists()) logsFolder.mkdir();
        currentLogFile = new File(owner.getDataFolder(), "logs" + File.separator + currentLogFileName + ".log");
        if (!currentLogFile.exists()) {
            try {
                currentLogFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Current log file name " + currentLogFileName + " This might be invalid. Please check!");
                e.printStackTrace();
                return;
            }
        }
        try {
            logHandler = new FileHandler(currentLogFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logHandler.setFormatter(new LogFormat());
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.addHandler(logHandler);
    }

    /**
     * Get the next log file name
     *
     * @return The next file name of the log file
     */
    private String getNextLogName() {
        final File logsFolder = getLogFolder();

        if (logsFolder.listFiles() == null) return utils.getDateFormat().format(new Date()) + "_0";
        File[] logs = logsFolder.listFiles();
        List<File> sameFile = new ArrayList<>();
        for (File f : logs) {
            String fileName = FileUtils.getFileName(f, false);
            String date = utils.getDateFormat().format(new Date());
            if (fileName.contains(date)) sameFile.add(f);
        }
        String currentFileName = utils.getDateFormat().format(new Date());
        int currentNumber = 0;
        for (File f : sameFile) {
            String fileName = FileUtils.getFileName(f, false);
            try {
                int logFileNumber = Integer.parseInt(fileName.split("_")[1]);
                if (currentNumber < logFileNumber) {
                    currentFileName = fileName;
                    currentNumber = logFileNumber;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignore) {} // Ignore
        }
        currentFileName = currentFileName.replace("_" + currentNumber, "");
        return currentFileName + "_" + (currentNumber + 1);
    }

    private class LogFormat extends Formatter {

        private final String logFormat = getLogFormat();

        @Override
        public String format(LogRecord record) {
            return logFormat
                    .replace("%date", utils.getCurrentTimestampString())
                    .replace("%logLevel", String.valueOf(record.getLevel()))
                    .replace("%message", formatMessage(record)) + System.lineSeparator();
        }

        public String getHead(Handler h) {
            return super.getHead(h);
        }

        public String getTail(Handler h) {
            return super.getTail(h);
        }
    }

    private class LogFileUpdater implements Runnable {

        @Override
        public void run() {
            Timestamp before = utils.convertStringToTimestamp(currentLogFileName.split("_")[0]);
            Timestamp now = utils.getCurrentTimestamp();
            if (before.after(now)) {
                currentLogFileName = getNextLogName();
                setup();
            }
        }
    }

    /**
     * Get the log folder
     *
     * @return : The folder
     */
    public abstract File getLogFolder();

    /**
     * Get the Timestamp format, if format contains / it will automatically convert to - , so make sure the separator
     * is - only!
     *
     * @return : The format
     */
    public abstract String getTimestampFormat();

    /**
     * Get the log name
     *
     * @return : The log name
     */
    public abstract String getLoggerName();

    /**
     * Get the log update time (in second) set to -1 to disable
     *
     * @return : The update time
     */
    public abstract int getLogUpdateTime();

    /**
     * Get the log format, override to remove default value
     *
     * @return : The log format
     */
    public String getLogFormat() {
        return "%date [LOG] %logLevel %message";
    }
}
