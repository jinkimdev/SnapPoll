package dev.jinkim.snappollandroid.event;

/**
 * Created by Jinhyun Kim 1/21/2015
 * <p/>
 * Event fired when GoogleApiClient onConnected
 */
public class GoogleApiClientConnectedEvent {

    public boolean success;

    public GoogleApiClientConnectedEvent(boolean success) {
        this.success = success;
    }
}
