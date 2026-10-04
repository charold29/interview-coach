package dev.charold.coach.practice.infrastructure.adapter.in.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.charold.coach.practice.domain.exception.PracticeLimitReachedException;
import dev.charold.coach.practice.domain.model.Evaluation;
import dev.charold.coach.practice.domain.model.Level;
import dev.charold.coach.practice.domain.model.PracticeProfile;
import dev.charold.coach.practice.domain.port.in.EvaluateAnswerUseCase;
import dev.charold.coach.practice.domain.port.in.RewriteAnswerUseCase;
import dev.charold.coach.practice.infrastructure.adapter.in.web.dto.PracticeForm;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

/**
 * CoachResource
 *
 * @author Harold Rojas Plasencia
 * @since 2026-10-03
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class CoachResource {

    private static final Logger LOG = Logger.getLogger(CoachResource.class);

    private static final String LIMIT_MESSAGE =
            "The daily practice limit for this instance has been reached. Try again tomorrow.";

    /**
     * Templates
     *
     * @author Harold Rojas Plasencia
     * @since 2026-10-03
     */
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
    EvaluateAnswerUseCase evaluateAnswer;

    @Inject
    RewriteAnswerUseCase rewriteAnswer;

    @Inject
    AccessCodeGuard accessCode;

    @Inject
    @ConfigProperty(name = "coach.mock", defaultValue = "false")
    boolean mock;

    @GET
    public TemplateInstance index() {
        return Templates.index(Level.values(), accessCode.required(), mock);
    }

    @POST
    @Path("evaluate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance evaluate(@BeanParam PracticeForm form) {
        String problem = validate(form, form.answer, "your answer");
        if (problem != null) {
            return Templates.error(problem);
        }
        try {
            PracticeProfile profile = form.toProfile();
            String question = form.question.strip();
            Evaluation evaluation = evaluateAnswer.evaluate(profile, question, form.answer.strip());
            return Templates.evaluation(evaluation, profile, question);
        } catch (PracticeLimitReachedException e) {
            return Templates.error(LIMIT_MESSAGE);
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
        String problem = validate(form, current, "the model answer");
        if (problem != null) {
            return Templates.rewriteError(current, problem);
        }
        try {
            String rewritten = rewriteAnswer.rewrite(form.toProfile(), form.question.strip(), current,
                    form.toDirection());
            return Templates.modelAnswer(rewritten);
        } catch (PracticeLimitReachedException e) {
            return Templates.rewriteError(current, LIMIT_MESSAGE);
        } catch (RuntimeException e) {
            LOG.error("Rewrite failed", e);
            return Templates.rewriteError(current, "The rewrite failed. Wait a moment and try again.");
        }
    }

    private String validate(PracticeForm form, String text, String textName) {
        if (!accessCode.accepts(form.accessCode)) {
            return "Wrong or missing access code.";
        }
        if (isBlank(form.question)) {
            return "Write the interview question first.";
        }
        if (isBlank(text)) {
            return "Write " + textName + " first.";
        }
        if (form.question.length() > PracticeForm.MAX_QUESTION || text.length() > PracticeForm.MAX_ANSWER) {
            return "That's too long. Keep the question under " + PracticeForm.MAX_QUESTION
                    + " characters and the answer under " + PracticeForm.MAX_ANSWER + ".";
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
