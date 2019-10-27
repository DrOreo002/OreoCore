package me.droreo002.oreocore.utils.io;

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    public static String getFileName(File file, boolean withExtension) {
        if (!withExtension) {
            return file.getName().replaceFirst("[.][^.]+$", "");
        } else {
            return file.getName();
        }
    }

    public static List<String> readLines(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
        final List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();

        return lines;
    }
}
