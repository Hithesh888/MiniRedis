import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DataStore {

    private final Map<String, String> data;
    private final Map<String, Long> expiry;

    public DataStore() {

        data = new ConcurrentHashMap<>();
        expiry = new ConcurrentHashMap<>();
    }

    // SET
    public void set(String key, String value) {

        data.put(key, value);
        expiry.remove(key);
    }

    // SET with expiration in seconds
    public void set(String key, String value, long seconds) {

        data.put(key, value);

        long expiryTime =
                System.currentTimeMillis()
                        + (seconds * 1000);

        expiry.put(key, expiryTime);
    }

    // SET with exact expiration timestamp
    public void setWithExpiry(
            String key,
            String value,
            long expiryTime) {

        data.put(key, value);
        expiry.put(key, expiryTime);
    }

    // GET
    public String get(String key) {

        if (isExpired(key)) {

            delete(key);
            return null;
        }

        return data.get(key);
    }

    // DELETE
    public boolean delete(String key) {

        expiry.remove(key);

        return data.remove(key) != null;
    }

    // EXISTS
    public boolean exists(String key) {

        if (isExpired(key)) {

            delete(key);
            return false;
        }

        return data.containsKey(key);
    }

    // INCR
    public long increment(String key) {

        if (isExpired(key)) {
            delete(key);
        }

        String value =
                data.get(key);

        if (value == null) {

            data.put(key, "1");

            return 1;
        }

        long number =
                Long.parseLong(value);

        number++;

        data.put(
                key,
                String.valueOf(number)
        );

        return number;
    }

    // EXPIRE
    public boolean expire(
            String key,
            long seconds) {

        if (!data.containsKey(key)) {
            return false;
        }

        long expiryTime =
                System.currentTimeMillis()
                        + (seconds * 1000);

        expiry.put(
                key,
                expiryTime
        );

        return true;
    }

    // TTL
    public long ttl(String key) {

        if (isExpired(key)) {

            delete(key);
            return -2;
        }

        if (!data.containsKey(key)) {
            return -2;
        }

        Long expiryTime =
                expiry.get(key);

        // No expiration
        if (expiryTime == null) {
            return -1;
        }

        long remaining =
                expiryTime
                        - System.currentTimeMillis();

        return Math.max(
                0,
                (remaining + 999) / 1000
        );
    }

    // Check expiration
    private boolean isExpired(String key) {

        Long expiryTime =
                expiry.get(key);

        if (expiryTime == null) {
            return false;
        }

        return System.currentTimeMillis()
                >= expiryTime;
    }
}