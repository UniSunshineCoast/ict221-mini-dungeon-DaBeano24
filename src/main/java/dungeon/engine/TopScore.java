package dungeon.engine;

import java.io.Serializable;
import java.time.LocalDate;

public class TopScore implements Comparable<TopScore>, Serializable {
    private final int score;
    private final LocalDate date;

    public TopScore(int score, LocalDate date) {
        this.score = score;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public int compareTo(TopScore other) {
        return Integer.compare(other.score, this.score); // descending order
    }

    @Override
    public String toString() {
        return String.format("%d %s", score, date);
    }
}
