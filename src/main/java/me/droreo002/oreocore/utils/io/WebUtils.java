package me.droreo002.oreocore.utils.io;

import me.droreo002.oreocore.utils.misc.ThreadingUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

public final class WebUtils {

    /**
     * Make a GET request to the URL
     *
     * @param rawUrl The raw URL
     * @return content of the GET request
     */
    public static Future<String> getRequest(String rawUrl) {
        return ThreadingUtils.makeFuture(() -> {
            URL url = new URL(rawUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();
        });
    }
}
