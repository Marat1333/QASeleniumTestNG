package com.leroy.core.testrail.api;

import com.leroy.core.configuration.Log;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.util.Strings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIClient {
    private String m_user;
    private String m_password;
    private String m_url;

    public APIClient(String base_url) {
        m_user = "ksolkin@luxoft.com";
        m_password = "";
        if (!base_url.endsWith("/")) {
            base_url += "/";
        }
        this.m_url = base_url + "index.php?/api/v2/";
    }

    /**
     * Get/Set User
     * <p>
     * Returns/sets the user used for authenticating the API requests.
     */
    public String getUser() {
        return this.m_user;
    }

    public void setUser(String user) {
        this.m_user = user;
    }

    /**
     * Get/Set Password
     * <p>
     * Returns/sets the password used for authenticating the API requests.
     */
    public String getPassword() {
        return this.m_password;
    }

    public void setPassword(String password) {
        this.m_password = password;
    }

    /**
     * Send Get
     * <p>
     * Issues a GET request (read) against the API and returns the result
     * (as Object, see below).
     * <p>
     * Arguments:
     * <p>
     * uri                  The API method to call including parameters
     * (e.g. get_case/1)
     * <p>
     * Returns the parsed JSON response as standard object which can
     * either be an instance of JSONObject or JSONArray (depending on the
     * API method). In most cases, this returns a JSONObject instance which
     * is basically the same as java.util.Map.
     */
    public Object sendGet(String uri) throws IOException, APIException, InterruptedException {
        return sendGet(uri, 100);
    }

    public Object sendGet(String uri, int attemptsNumber)
            throws IOException, APIException, InterruptedException {
        try {
            return this.sendRequest("GET", uri, null);
        } catch (APIException err) {
            Log.warn(err.getMessage());
            long timeout = 10000;
            if (err.getMessage().contains("Retry after")) {
                try {
                    String timeoutString =
                            getDecimal(StringUtils.substringAfter(err.getMessage(), "Retry after"), 0);
                    if (Strings.isNotNullAndNotEmpty(timeoutString)) {
                        timeout = (Long.parseLong(timeoutString) + 1) * 1000;
                        if (timeout > 60000)
                            timeout = 60000;
                    }
                } catch (Exception e) {
                    Log.warn("An error occurred while calculating the 'Retry after' timeout: " + err.getMessage());
                }
                Thread.sleep(timeout);
                if ((attemptsNumber - 1) > 0)
                    return sendGet(uri, attemptsNumber - 1);
            }
            throw err;
        }
    }

    /**
     * Send POST
     * <p>
     * Issues a POST request (write) against the API and returns the result
     * (as Object, see below).
     * <p>
     * Arguments:
     * <p>
     * uri                  The API method to call including parameters
     * (e.g. add_case/1)
     * data                 The data to submit as part of the request (e.g.,
     * a map)
     * <p>
     * Returns the parsed JSON response as standard object which can
     * either be an instance of JSONObject or JSONArray (depending on the
     * API method). In most cases, this returns a JSONObject instance which
     * is basically the same as java.util.Map.
     */
    public Object sendPost(String uri, Object data)
            throws MalformedURLException, IOException, APIException {
        return this.sendRequest("POST", uri, data);
    }

    private Object sendRequest(String method, String uri, Object data)
            throws MalformedURLException, IOException, APIException {
        URL url = new URL(this.m_url + uri);

        // Create the connection object and set the required HTTP method
        // (GET/POST) and headers (content type and basic auth).
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("Content-Type", "application/json");

        String auth = getAuthorization(this.m_user, this.m_password);
        conn.addRequestProperty("Authorization", "Basic " + auth);

        if (method.equals("POST")) {
            // Add the POST arguments, if any. We just serialize the passed
            // data object (i.e. a dictionary) and then add it to the
            // request body.
            if (data != null) {
                byte[] block = JSONValue.toJSONString(data).
                        getBytes(StandardCharsets.UTF_8);

                conn.setDoOutput(true);
                OutputStream ostream = conn.getOutputStream();
                ostream.write(block);
                ostream.flush();
            }
        }

        // Execute the actual web request (if it wasn't already initiated
        // by getOutputStream above) and record any occurred errors (we use
        // the error stream in this case).
        int status = conn.getResponseCode();

        InputStream istream;
        if (status != 200) {
            istream = conn.getErrorStream();
            if (istream == null) {
                throw new APIException(
                        "TestRail API return HTTP " + status +
                                " (No additional error message received)"
                );
            }
        } else {
            istream = conn.getInputStream();
        }

        // Read the response body, if any, and deserialize it from JSON.
        String text = "";
        if (istream != null) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            istream,
                            StandardCharsets.UTF_8
                    )
            );

            String line;
            while ((line = reader.readLine()) != null) {
                text += line;
                text += System.getProperty("line.separator");
            }

            reader.close();
        }

        Object result;
        if (!text.equals("")) {
            result = JSONValue.parse(text);
        } else {
            result = new JSONObject();
        }

        // Check for any occurred errors and add additional details to
        // the exception message, if any (e.g. the error message returned
        // by TestRail).
        if (status != 200) {
            String error = "No additional error message received";
            if (result != null && result instanceof JSONObject) {
                JSONObject obj = (JSONObject) result;
                if (obj.containsKey("error")) {
                    error = '"' + (String) obj.get("error") + '"';
                }
            }

            throw new APIException(
                    "TestRail API returned HTTP " + status +
                            "(" + error + ")"
            );
        }

        return result;
    }

    private static String getAuthorization(String user, String password) {
        return getBase64((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private static String getBase64(byte[] buffer) {
        final char[] map = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', '+', '/'
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            byte b0 = buffer[i++], b1 = 0, b2 = 0;

            int bytes = 3;
            if (i < buffer.length) {
                b1 = buffer[i++];
                if (i < buffer.length) {
                    b2 = buffer[i];
                } else {
                    bytes = 2;
                }
            } else {
                bytes = 1;
            }

            int total = (b0 << 16) | (b1 << 8) | b2;

            switch (bytes) {
                case 3:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append(map[(total >> 6) & 0x3f]);
                    sb.append(map[total & 0x3f]);
                    break;

                case 2:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append(map[(total >> 6) & 0x3f]);
                    sb.append('=');
                    break;

                case 1:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append('=');
                    sb.append('=');
                    break;
            }
        }

        return sb.toString();
    }

    // ----------- Private methods ---------------- //

    /**
     * Gets decimal number from string
     *
     * @param fromString
     * @param index
     * @return
     */
    private static String getDecimal(String fromString, int index) {
        Pattern p = Pattern.compile("[-+]?\\d+\\.?\\d*");
        Matcher m = p.matcher(fromString);
        int currentIdx = -1;
        while (m.find() && ++currentIdx <= index) {
            if (currentIdx == index)
                return m.group();
        }
        return null;
    }
}
