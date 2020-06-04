package me.droreo002.oreocore.utils.io;

import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class FileUtils {

    public static String getFileName(File file, boolean withExtension) {
        if (!withExtension) {
            return file.getName().replaceFirst("[.][^.]+$", "");
        } else {
            return file.getName();
        }
    }

    public static void getListFile(File directory, List<File> files) {
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getListFile(file, files);
                }
            }
        }
    }

    public static boolean copyFile(final File toCopy, final File destFile) {
        try {
            return FileUtils.copyStream(new FileInputStream(toCopy),
                    new FileOutputStream(destFile));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyFilesRecusively(final File toCopy,
                                               final File destDir) {
        assert destDir.isDirectory();

        if (!toCopy.isDirectory()) {
            return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
        } else {
            final File newDestDir = new File(destDir, toCopy.getName());
            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                return false;
            }
            for (final File child : toCopy.listFiles()) {
                if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection) throws IOException {
        final JarFile jarFile = jarConnection.getJarFile();

        for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
            final JarEntry entry = e.nextElement();
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                final String filename = StringUtils.removeStart(entry.getName(),
                        jarConnection.getEntryName());

                final File f = new File(destDir, filename);
                if (f.exists()) continue; // Ignore if already exists
                if (!entry.isDirectory()) {
                    final InputStream entryInputStream = jarFile.getInputStream(entry);
                    if (!FileUtils.copyStream(entryInputStream, f)) {
                        return false;
                    }
                    entryInputStream.close();
                } else {
                    if (!FileUtils.ensureDirectoryExists(f)) {
                        throw new IOException("Could not create directory: "
                                + f.getAbsolutePath());
                    }
                }
            }
        }
        return true;
    }

    public static boolean copyResourcesRecursively(final URL originUrl, final File destination) {
        try {
            final URLConnection urlConnection = originUrl.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                return FileUtils.copyJarResourcesRecursively(destination,
                        (JarURLConnection) urlConnection);
            } else {
                return FileUtils.copyFilesRecusively(new File(originUrl.getPath()),
                        destination);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final InputStream is, final File f) {
        try {
            return FileUtils.copyStream(is, new FileOutputStream(f));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final InputStream is, final OutputStream os) {
        try {
            final byte[] buf = new byte[1024];

            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean ensureDirectoryExists(final File f) {
        return f.exists() || f.mkdir();
    }

    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }

    public static List<String> readLines(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
        final List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        lines.removeIf(String::isEmpty);
        return lines;
    }

    public static List<String> readLines(InputStream stream, String... ignoreStarting) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
        final List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            boolean contains = false;
            for (String s : ignoreStarting) {
                if (line.startsWith(s)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) lines.add(line);
        }
        reader.close();
        lines.removeIf(String::isEmpty);
        return lines;
    }

    public static void write(File file, String... strings) throws IOException {
        FileWriter writer = new FileWriter(file);
        for (String s : strings) {
            writer.write((!s.endsWith(System.lineSeparator()) ? s + System.lineSeparator() : s));
        }
        writer.flush();
        writer.close();
    }
}
