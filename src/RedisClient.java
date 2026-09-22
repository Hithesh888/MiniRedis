import java.io.*;
import java.net.*;

public class RedisClient {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 6379);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // SET name Hithesh EX 5
            String setCommand =
                    "*5\r\n" +
                    "$3\r\n" +
                    "SET\r\n" +
                    "$4\r\n" +
                    "name\r\n" +
                    "$7\r\n" +
                    "Hithesh\r\n" +
                    "$2\r\n" +
                    "EX\r\n" +
                    "$1\r\n" +
                    "5\r\n";

            sendCommand(output, input, setCommand);

            // GET name
            String getCommand =
                    "*2\r\n" +
                    "$3\r\n" +
                    "GET\r\n" +
                    "$4\r\n" +
                    "name\r\n";

            sendCommand(output, input, getCommand);

            // TTL name
            String ttlCommand =
                    "*2\r\n" +
                    "$3\r\n" +
                    "TTL\r\n" +
                    "$4\r\n" +
                    "name\r\n";

            sendCommand(output, input, ttlCommand);

            // Wait 6 seconds
            System.out.println("Waiting 6 seconds...");

            Thread.sleep(6000);

            // GET after expiration
            sendCommand(output, input, getCommand);

            // TTL after expiration
            sendCommand(output, input, ttlCommand);

            socket.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    static void sendCommand(
            OutputStream output,
            InputStream input,
            String command) throws IOException {

        output.write(command.getBytes());
        output.flush();

        byte[] buffer = new byte[1024];

        int bytesRead = input.read(buffer);

        String response =
                new String(buffer, 0, bytesRead);

        System.out.println("Server response:");
        System.out.println(response);
    }
}
