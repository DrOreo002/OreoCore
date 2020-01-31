package me.droreo002.oreocore.utils.misc;

import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public final class HastebinUtils {

    /**
     * Post a new hastebin
     *
     * @param txtData The text data as list of string
     * @return The link
     * @throws IOException If something goes wrong
     */
    public static String postHastebin(List<String> txtData) throws IOException {
        String text = txtData.stream().map(s -> s + "\n").collect(Collectors.joining());
        byte[] postData = text.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String requestURL = "https://hastebin.com/documents";
        URL url = new URL(requestURL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Hastebin Java Api");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        String response = null;
        DataOutputStream wr;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.contains("\"key\"")) {
            response = response.substring(response.indexOf(":") + 2, response.length() - 2);
            response = "https://hastebin.com/" + response;
        }

        return response;
    }

}
