import java.util.*;

public class MiniRedisTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("       MiniRedis Test Suite");
        System.out.println("================================");

        testPing();
        testEcho();
        testSetGet();
        testDelete();
        testExists();
        testIncrement();
        testExpire();
        testTTL();
        testExpiredKey();

        System.out.println();
        System.out.println("================================");
        System.out.println("Tests Passed: " + passed);
        System.out.println("Tests Failed: " + failed);
        System.out.println("================================");

        if (failed == 0) {
            System.out.println("ALL TESTS PASSED!");
        } else {
            System.out.println("SOME TESTS FAILED!");
        }
    }

    // =========================
    // PING
    // =========================

    static void testPing() {

        String response =
                CommandHandler.handle(
                        Arrays.asList("PING")
                );

        check(
                "PING",
                "+PONG\r\n",
                response
        );
    }

    // =========================
    // ECHO
    // =========================

    static void testEcho() {

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "ECHO",
                                "hello"
                        )
                );

        check(
                "ECHO",
                "$5\r\nhello\r\n",
                response
        );
    }

    // =========================
    // SET + GET
    // =========================

    static void testSetGet() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "name",
                        "Hithesh"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "GET",
                                "name"
                        )
                );

        check(
                "SET + GET",
                "$7\r\nHithesh\r\n",
                response
        );
    }

    // =========================
    // DEL
    // =========================

    static void testDelete() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "deleteKey",
                        "value"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "DEL",
                                "deleteKey"
                        )
                );

        check(
                "DEL",
                ":1\r\n",
                response
        );
    }

    // =========================
    // EXISTS
    // =========================

    static void testExists() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "existsKey",
                        "value"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "EXISTS",
                                "existsKey"
                        )
                );

        check(
                "EXISTS",
                ":1\r\n",
                response
        );
    }

    // =========================
    // INCR
    // =========================

    static void testIncrement() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "counter",
                        "10"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "INCR",
                                "counter"
                        )
                );

        check(
                "INCR",
                ":11\r\n",
                response
        );
    }

    // =========================
    // EXPIRE
    // =========================

    static void testExpire() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "expireKey",
                        "value"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "EXPIRE",
                                "expireKey",
                                "10"
                        )
                );

        check(
                "EXPIRE",
                ":1\r\n",
                response
        );
    }

    // =========================
    // TTL
    // =========================

    static void testTTL() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "ttlKey",
                        "value"
                )
        );

        CommandHandler.handle(
                Arrays.asList(
                        "EXPIRE",
                        "ttlKey",
                        "10"
                )
        );

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "TTL",
                                "ttlKey"
                        )
                );

        boolean valid =
                response.equals(":10\r\n")
                || response.equals(":9\r\n")
                || response.equals(":8\r\n");

        checkBoolean(
                "TTL",
                valid
        );
    }

    // =========================
    // EXPIRED KEY
    // =========================

    static void testExpiredKey() {

        CommandHandler.handle(
                Arrays.asList(
                        "SET",
                        "shortKey",
                        "value",
                        "EX",
                        "1"
                )
        );

        try {

            Thread.sleep(1500);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        String response =
                CommandHandler.handle(
                        Arrays.asList(
                                "GET",
                                "shortKey"
                        )
                );

        check(
                "Expired Key",
                "$-1\r\n",
                response
        );
    }

    // =========================
    // CHECK RESULT
    // =========================

    static void check(
            String testName,
            String expected,
            String actual) {

        if (expected.equals(actual)) {

            System.out.println(
                    "[PASS] " + testName
            );

            passed++;

        } else {

            System.out.println(
                    "[FAIL] " + testName
            );

            System.out.println(
                    "Expected: " + expected
            );

            System.out.println(
                    "Actual: " + actual
            );

            failed++;
        }
    }

    static void checkBoolean(
            String testName,
            boolean result) {

        if (result) {

            System.out.println(
                    "[PASS] " + testName
            );

            passed++;

        } else {

            System.out.println(
                    "[FAIL] " + testName
            );

            failed++;
        }
    }
}