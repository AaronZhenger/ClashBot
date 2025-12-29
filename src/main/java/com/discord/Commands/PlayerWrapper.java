package com.discord.Commands;

import com.discord.Tokens;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PlayerWrapper {
    public static JSONObject getPlayerStuff(String tag) throws IOException, JSONException {
        if (tag == null || !tag.startsWith("#")) tag = "#" + tag;

        URL url = new URL("https://api.clashroyale.com/v1/players/%23" + tag.substring(1));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", Tokens.APIToken);
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200)
            return null;

        InputStream stream = conn.getInputStream();

        Scanner scanner = new Scanner(stream);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) sb.append(scanner.nextLine());
        scanner.close();

        return new JSONObject(sb.toString());
    }
}
