package dev.charold.coach.domain;

/**
 * One English correction: what the candidate said, a better phrasing, and why.
 */
public record EnglishFix(String youSaid, String better, String why) {
}
