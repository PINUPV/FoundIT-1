package foundit.foundit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Yop-Portatil on 25/10/2017.
 */

public class Util {
    public static String GetWeb(HttpURLConnection urlConnection) {
        StringBuilder out = new StringBuilder("");
        String line;
        InputStream in = null;
        try {
            in = urlConnection.getInputStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(in));
            while ((line = dis.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) { }
        }
        return "";
    }
}
