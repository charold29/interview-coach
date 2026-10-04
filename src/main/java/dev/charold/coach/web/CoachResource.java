package dev.charold.coach.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import dev.charold.coach.domain.Evaluation;
import dev.charold.coach.domain.Level;
import dev.charold.coach.domain.PracticeProfile;
import dev.charold.coach.service.CoachService;
import dev.charold.coach.service.CoachService.Direction;
import dev.charold.coach.service.CoachService.LimitReachedException;
import dev.charold.coach.service.UsageGuard;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

/**
 * Server-rendered UI. Full page on GET /, HTML fragments for htmx on the POSTs.
 * Errors come back as fragments with status 200 so htmx swaps them in place.
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class CoachResource {

    private static final Logger LOG = Logger.getLogger(CoachResource.class);

    @CheckedTemplate
    static class Templates {
        static native TemplateInstance index(Level[] levels, boolean accessCodeRequired, boolean mock);

        static native TemplateInstance evaluation(Evaluation evaluation, PracticeProfile profile, String question);

        static native TemplateInstance modelAnswer(String answer);

        static native TemplateInstance error(String message);

        /** Keeps the current model answer on screen when a rewrite fails. */
        static native TemplateInstance rewriteError(String answer, String message);
    }

    @Inject
    CoachService coach;

    @Inject
    UsageGuard guard;

    @GET
    public TemplateInstance index() {
        return Templates.index(Level.values(), guard.accessCodeRequired(), coach.isMock());
    }

    @POST
    @Path("evaluate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance evaluate(@BeanParam PracticeForm form) {
        String problem = validate(form, form.answer, PracticeForm.MAX_ANSWER, "your answer");
        if (problem != null) {
            return Templates.error(problem);
        }
        try {
            PracticeProfile profile = form.profile();
            Evaluation evaluation = coach.evaluate(profile, form.question.strip(), form.answer.strip());
            return Templates.evaluation(evaluation, profile, form.question.strip());
        } catch (LimitReachedException e) {
            return Templates.error("The daily practice limit for this instance has been reached. Try again tomorrow.");
        } catch (RuntimeException e) {
            LOG.error("Evaluation failed", e);
            return Templates.error("The evaluation failed. Wait a moment and try again.");
        }
    }

    @POST
    @Path("rewrite")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance rewrite(@BeanParam PracticeForm form) {
        String current = form.currentAnswer == null ? "" : form.currentAnswer.strip();
        String problem = validate(form, current, PracticeForm.MAX_ANSWER, "the model answer");
        if (problem != null) {
            return Templates.rewriteError(current, problem);
        }
        try {
            String rewritten = coach.rewrite(form.profile(), form.question.strip(), current,
                    Direction.parse(form.direction));
            return Templates.modelAnswer(rewritten);
        } catch (LimitReachedException e) {
            return Templates.rewriteError(current,
                    "The daily practice limit for this instance has been reached. Try again tomorrow.");
        } catch (RuntimeException e) {
            LOG.error("Rewrite failed", e);
            return Templates.rewriteError(current, "The rewrite failed. Wait a moment and try again.");
        }
    }

    private String validate(PracticeForm form, String text, int maxText, String textName) {
        if (!guard.accepts(form.accessCode)) {
            return "Wrong or missing access code.";
        }
        if (isBlank(form.question)) {
            return "Write the interview question first.";
        }
        if (isBlank(text)) {
            return "Write " + textName + " first.";
        }
        if (form.question.length() > PracticeForm.MAX_QUESTION || text.length() > maxText) {
            return "That's too long. Keep the question under " + PracticeForm.MAX_QUESTION
                    + " characters and the answer under " + maxText + ".";
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
