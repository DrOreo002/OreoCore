package me.droreo002.oreocore.utils.misc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public final class FileDownloader {

    /**
     * Force download the file
     *
     * @param url The url to download
     * @param file The save file
     */
    public static void forceDownload(URL url, File file, int maxTries) {
        int totalTries = 0;
        while (true) {
            try {
                download(url, file);
                break;
            } catch (IOException e) {
                if (totalTries >= maxTries) throw new RuntimeException("Failed to download file from " + url.toString(), e);
                totalTries++;
            }
        }
    }

    /**
     * Download the file from direct URL
     *
     * @param url The url (must be direct link)
     * @param save The save file
     * @throws IOException If something goes wrong
     */
    public static void download(URL url, File save) throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla");
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);

        try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream()); FileOutputStream fileOutputStream = new FileOutputStream(save)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}
