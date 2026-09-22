import java.io.*;
import java.util.*;

public class TestParser {

    public static void main(String[] args) throws Exception {

        String request =
                "*1\r\n" +
                "$4\r\n" +
                "PING\r\n";

        InputStream input =
                new ByteArrayInputStream(
                        request.getBytes()
                );

        RespParser parser =
                new RespParser(input);

        List<String> command =
                parser.readCommand();

        System.out.println(command);
    }
}