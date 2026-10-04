package dev.charold.coach.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Runs with coach.mock=true (see the %test profile), so no API key is needed
 * and no tokens are spent.
 */
@QuarkusTest
class CoachResourceTest {

    @Test
    void rendersThePracticePage() {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(containsString("Score my answer"))
                .body(containsString("Mock mode is on"))
                .body(not(containsString("Access code")));
    }

    @Test
    void evaluatesAnAnswer() {
        given()
                .formParam("role", "Backend Engineer")
                .formParam("targetLevel", "SENIOR")
                .formParam("years", "6")
                .formParam("question", "Why would you add a cache?")
                .formParam("answer", "The cache makes the reads faster but the data can be old.")
                .when().post("/evaluate")
                .then()
                .statusCode(200)
                .body(containsString("class=\"scorecard\""))
                .body(containsString("6.0"))
                .body(containsString("Reads as <strong>Mid</strong>"))
                .body(containsString("You're aiming for Senior"))
                .body(containsString("the data can be stale"));
    }

    @Test
    void asksForTheAnswerWhenItIsMissing() {
        given()
                .formParam("question", "Why would you add a cache?")
                .formParam("answer", "  ")
                .when().post("/evaluate")
                .then()
                .statusCode(200)
                .body(containsString("Write your answer first."));
    }

    @Test
    void rewritesTheModelAnswer() {
        given()
                .formParam("question", "Why would you add a cache?")
                .formParam("currentAnswer", "Some long answer.")
                .formParam("direction", "shorter")
                .when().post("/rewrite")
                .then()
                .statusCode(200)
                .body(containsString("class=\"model-answer\""))
                .body(containsString("name=\"currentAnswer\""));
    }

    @Test
    void escapesUserInput() {
        given()
                .formParam("question", "<script>alert(1)</script>")
                .formParam("answer", "an answer")
                .when().post("/evaluate")
                .then()
                .statusCode(200)
                .body(not(containsString("<script>alert(1)</script>")));
    }
}
