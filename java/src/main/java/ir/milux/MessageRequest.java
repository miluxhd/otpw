package ir.milux;

public class MessageRequest {

    String username;
    String uuid;
    String challenge;

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getUuid () {
        return uuid;
    }

    public void setUuid (String uuid) {
        this.uuid = uuid;
    }

    public String getChallenge () {
        return challenge;
    }

    public void setChallenge (String challenge) {
        this.challenge = challenge;
    }

}
