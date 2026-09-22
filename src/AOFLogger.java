import java.io.*;

public class AOFLogger {

    private static final String FILE_NAME = "appendonly.aof";

    private static boolean replaying = false;

    public static synchronized void log(String command) {

        if (replaying) {
            return;
        }

        try (FileWriter writer =
                     new FileWriter(FILE_NAME, true)) {

            writer.write(command);
            writer.write("\n");

        } catch (IOException e) {

            System.out.println(
                    "AOF write error: "
                            + e.getMessage()
            );
        }
    }

    public static void setReplaying(boolean value) {
        replaying = value;
    }
}