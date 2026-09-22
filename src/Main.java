import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final int PORT = 6379;

    private static final ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

        // Load previous commands from AOF
        loadAOF();

        try {

            ServerSocket serverSocket =
                    new ServerSocket(PORT);

            System.out.println(
                    "MiniRedis server started on port " + PORT
            );

            while (true) {

                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "Client connected: "
                                + clientSocket.getInetAddress()
                );

                threadPool.submit(
                        () -> handleClient(clientSocket)
                );
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            threadPool.shutdown();
        }
    }

    // Load commands from appendonly.aof
    static void loadAOF() {

        File file =
                new File("appendonly.aof");

        if (!file.exists()) {

            System.out.println(
                    "No AOF file found. Starting with empty database."
            );

            return;
        }

        System.out.println(
                "Loading AOF..."
        );

        AOFLogger.setReplaying(true);

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                System.out.println(
                        "Replaying: " + line
                );

                List<String> command =
                        Arrays.asList(
                                line.split(" ")
                        );

                CommandHandler.handle(command);
            }

            reader.close();

            System.out.println(
                    "AOF loading complete."
            );

        } catch (IOException e) {

            System.out.println(
                    "AOF load error: "
                            + e.getMessage()
            );

        } finally {

            AOFLogger.setReplaying(false);
        }
    }

    static void handleClient(Socket clientSocket) {

        try {

            InputStream input =
                    clientSocket.getInputStream();

            OutputStream output =
                    clientSocket.getOutputStream();

            RespParser parser =
                    new RespParser(input);

            while (true) {

                List<String> command =
                        parser.readCommand();

                if (command == null) {
                    break;
                }

                System.out.println(
                        "Command: " + command
                );

                String response =
                        CommandHandler.handle(command);

                output.write(
                        response.getBytes()
                );

                output.flush();
            }

        } catch (IOException e) {

            System.out.println(
                    "Client disconnected."
            );

        } finally {

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}