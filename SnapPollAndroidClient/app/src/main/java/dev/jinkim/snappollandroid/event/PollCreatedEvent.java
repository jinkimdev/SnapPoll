package dev.jinkim.snappollandroid.event;

import dev.jinkim.snappollandroid.model.Poll;

/**
 * Created by Jin on 1/31/15.
 *
 * Event fired after creating a poll has been successful
 */
public class PollCreatedEvent {
    public Poll poll;

    public PollCreatedEvent(Poll poll) {
        this.poll = poll;
    }
}
