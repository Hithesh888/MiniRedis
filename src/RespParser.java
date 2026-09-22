import java.io.*;
import java.util.*;

public class RespParser {

    private BufferedReader reader;

    public RespParser(InputStream input) {
        reader = new BufferedReader(
                new InputStreamReader(input)
        );
    }

    public List<String> readCommand() throws IOException {

        // Read first line
        String line = reader.readLine();

        if (line == null) {
            return null;
        }

        // RESP Array must start with *
        if (!line.startsWith("*")) {
            throw new IOException("Invalid RESP command");
        }

        // Number of arguments
        int argumentCount =
                Integer.parseInt(line.substring(1));

        List<String> command = new ArrayList<>();

        for (int i = 0; i < argumentCount; i++) {

            // Read $length
            String lengthLine = reader.readLine();

            if (!lengthLine.startsWith("$")) {
                throw new IOException("Invalid bulk string");
            }

            int length =
                    Integer.parseInt(lengthLine.substring(1));

            // Read actual value
            char[] data = new char[length];

            reader.read(data);

            // Remove CRLF
            reader.readLine();

            command.add(new String(data));
        }

        return command;
    }
}