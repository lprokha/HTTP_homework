import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try{
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 9889);

        System.out.printf("Запускаем сервер по адресу: %s:%s%n", address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50);
        System.out.println("    удачно!");
        return server;
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRoot);
        server.createContext("/apps/", Main::handleApp);
        server.createContext("/apps/profile", Main::handleProfile);
    }

    private static void handleProfile(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.println("Профиль");
                writer.println("Имя: Иван");
                writer.println("Фамилия: Иванов");
                writer.println("Статус: online");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleApp(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.println("Apps");
                writer.println("1) Notes");
                writer.println("2) Music");
                writer.println("3) Calendar");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRoot(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders()
                    .add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int responseLength = 0;

            exchange.sendResponseHeaders(responseCode, responseLength);

            try(PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String route = exchange.getHttpContext().getPath();

                write(writer, "Страница: ", "Главная");
                write(writer, "HTTP метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через ", route);
                writeHeaders(writer, "Заголовки запроса ", exchange.getRequestHeaders());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream out = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(out, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(PrintWriter writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

}