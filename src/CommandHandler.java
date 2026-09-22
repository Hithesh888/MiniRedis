import java.util.*;

public class CommandHandler {

    private static final DataStore store = new DataStore();

    public static String handle(List<String> command) {

        if (command == null || command.isEmpty()) {
            return "-ERR empty command\r\n";
        }

        String name = command.get(0).toUpperCase();

        // PING
        if (name.equals("PING")) {

            if (command.size() == 1) {
                return "+PONG\r\n";
            }

            if (command.size() == 2) {
                return bulkString(command.get(1));
            }

            return "-ERR wrong number of arguments for 'ping' command\r\n";
        }

        // ECHO
        if (name.equals("ECHO")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'echo' command\r\n";
            }

            return bulkString(command.get(1));
        }

        // SET
        if (name.equals("SET")) {

            // SET key value
            if (command.size() == 3) {

                store.set(
                        command.get(1),
                        command.get(2)
                );

                AOFLogger.log(
                        "SET "
                        + command.get(1)
                        + " "
                        + command.get(2)
                );

                return "+OK\r\n";
            }

            // SET key value EX seconds
            if (command.size() == 5
                    && command.get(3).equalsIgnoreCase("EX")) {

                try {

                    long seconds =
                            Long.parseLong(command.get(4));

                    if (seconds <= 0) {
                        return "-ERR invalid expiration time\r\n";
                    }

                    store.set(
                            command.get(1),
                            command.get(2),
                            seconds
                    );

                    long expiryTime =
                            System.currentTimeMillis()
                                    + (seconds * 1000);

                    AOFLogger.log(
                            "SET "
                            + command.get(1)
                            + " "
                            + command.get(2)
                            + " EXAT "
                            + expiryTime
                    );

                    return "+OK\r\n";

                } catch (NumberFormatException e) {

                    return "-ERR invalid expiration time\r\n";
                }
            }

            // Internal SET with absolute expiration timestamp
            if (command.size() == 5
                    && command.get(3).equalsIgnoreCase("EXAT")) {

                try {

                    long expiryTime =
                            Long.parseLong(command.get(4));

                    store.setWithExpiry(
                            command.get(1),
                            command.get(2),
                            expiryTime
                    );

                    return "+OK\r\n";

                } catch (NumberFormatException e) {

                    return "-ERR invalid expiration timestamp\r\n";
                }
            }

            return "-ERR wrong number of arguments for 'set' command\r\n";
        }

        // GET
        if (name.equals("GET")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'get' command\r\n";
            }

            String value =
                    store.get(command.get(1));

            if (value == null) {
                return "$-1\r\n";
            }

            return bulkString(value);
        }

        // DEL
        if (name.equals("DEL")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'del' command\r\n";
            }

            if (store.delete(command.get(1))) {

                AOFLogger.log(
                        "DEL " + command.get(1)
                );

                return ":1\r\n";
            }

            return ":0\r\n";
        }

        // EXISTS
        if (name.equals("EXISTS")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'exists' command\r\n";
            }

            if (store.exists(command.get(1))) {
                return ":1\r\n";
            }

            return ":0\r\n";
        }

        // INCR
        if (name.equals("INCR")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'incr' command\r\n";
            }

            try {

                long result =
                        store.increment(command.get(1));

                AOFLogger.log(
                        "INCR " + command.get(1)
                );

                return ":" + result + "\r\n";

            } catch (NumberFormatException e) {

                return "-ERR value is not an integer\r\n";
            }
        }

        // EXPIRE
        if (name.equals("EXPIRE")) {

            if (command.size() != 3) {
                return "-ERR wrong number of arguments for 'expire' command\r\n";
            }

            try {

                long seconds =
                        Long.parseLong(command.get(2));

                if (seconds <= 0) {
                    return "-ERR invalid expiration time\r\n";
                }

                boolean result =
                        store.expire(
                                command.get(1),
                                seconds
                        );

                if (result) {

                    long expiryTime =
                            System.currentTimeMillis()
                                    + (seconds * 1000);

                    AOFLogger.log(
                            "SET "
                            + command.get(1)
                            + " "
                            + store.get(command.get(1))
                            + " EXAT "
                            + expiryTime
                    );
                }

                return result
                        ? ":1\r\n"
                        : ":0\r\n";

            } catch (NumberFormatException e) {

                return "-ERR invalid expiration time\r\n";
            }
        }

        // TTL
        if (name.equals("TTL")) {

            if (command.size() != 2) {
                return "-ERR wrong number of arguments for 'ttl' command\r\n";
            }

            long result =
                    store.ttl(command.get(1));

            return ":" + result + "\r\n";
        }

        return "-ERR unknown command\r\n";
    }

    private static String bulkString(String value) {

        return "$"
                + value.length()
                + "\r\n"
                + value
                + "\r\n";
    }
}