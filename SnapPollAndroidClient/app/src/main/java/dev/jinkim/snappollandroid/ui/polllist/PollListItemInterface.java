package dev.jinkim.snappollandroid.ui.polllist;

/**
 * Created by Jin on 4/25/15.
 *
 * Define interface for poll list row item - either section or poll
 */
public interface PollListItemInterface {
    public boolean isPoll();

    public boolean isSection();
}
