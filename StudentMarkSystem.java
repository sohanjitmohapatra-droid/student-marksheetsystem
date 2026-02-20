import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;

public class StudentMarkSystem {

    static List<String> records = new ArrayList<>();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Frontend
            server.createContext("/", (HttpExchange exchange) -> {
                try {
                    String html = """
                        <html>
                        <head><title>Student Mark Management</title></head>
                        <body>
                            <h2>Student Mark Management System</h2>

                            <form method="post" action="/add">
                                Name: <input name="name"><br><br>
                                Roll: <input name="roll"><br><br>
                                Marks: <input name="marks"><br><br>
                                <button type="submit">Save</button>
                            </form>

                            <h3>Saved Records</h3>
                            <pre>""" + String.join("\n", records) + """
                            </pre>
                        </body>
                        </html>
                    """;

                    exchange.sendResponseHeaders(200, html.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(html.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Backend
            server.createContext("/add", (HttpExchange exchange) -> {
                try {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        InputStream is = exchange.getRequestBody();
                        String body = new String(is.readAllBytes());

                        Map<String, String> data = parseFormData(body);

                        String record = "Name: " + data.get("name")
                                + ", Roll: " + data.get("roll")
                                + ", Marks: " + data.get("marks");

                        records.add(record);

                        exchange.getResponseHeaders().add("Location", "/");
                        exchange.sendResponseHeaders(302, -1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            server.start();
            System.out.println("Server running at http://localhost:8080");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Map<String, String> parseFormData(String form) {
        Map<String, String> map = new HashMap<>();
        try {
            String[] pairs = form.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}