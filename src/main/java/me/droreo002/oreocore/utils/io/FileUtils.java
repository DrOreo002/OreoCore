package me.droreo002.oreocore.utils.io;

import java.io.File;

public final class FileUtils {

    public static String getFileName(File file, boolean withExtension) {
        if (!withExtension) {
            return file.getName().replaceFirst("[.][^.]+$", "");
        } else {
            return file.getName();
        }
    }
}
