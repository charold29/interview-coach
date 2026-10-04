package dev.charold.coach.domain;

/**
 * Seniority levels and the score bands that map to them (see docs/rubric.md).
 */
public enum Level {
    JUNIOR("Junior", "Doesn't clear the technical bar."),
    MID("Mid", "Passes if the rest of the interview compensates."),
    SENIOR("Senior", "Passes comfortably."),
    STAFF("Staff", "The interviewer takes notes on this answer.");

    private final String label;
    private final String reading;

    Level(String label, String reading) {
        this.label = label;
        this.reading = reading;
    }

    public String label() {
        return label;
    }

    public String reading() {
        return reading;
    }

    /** Maps a 0-10 average to a level. Bands: &lt;5, 5-6.9, 7-8.4, &ge;8.5. */
    public static Level fromScore(double score) {
        if (score >= 8.5) {
            return STAFF;
        }
        if (score >= 7.0) {
            return SENIOR;
        }
        if (score >= 5.0) {
            return MID;
        }
        return JUNIOR;
    }

    /** Lenient parse for form input; unknown values fall back to SENIOR. */
    public static Level parse(String value) {
        if (value == null) {
            return SENIOR;
        }
        for (Level level : values()) {
            if (level.name().equalsIgnoreCase(value.strip()) || level.label.equalsIgnoreCase(value.strip())) {
                return level;
            }
        }
        return SENIOR;
    }
}
