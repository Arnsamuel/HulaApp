package e.aaronsamuel.hulaapp;

public class PushUserDb {
    String userId;
    String name;

    public PushUserDb(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
