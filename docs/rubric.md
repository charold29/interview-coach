# Scoring rubric

Four axes, each scored 0-10. The overall score is their average. This is the
rubric the model is instructed to follow (see `InterviewCoach.java`); the
average and the level are computed in code, not by the model.

## Technical accuracy

Is what you said true?

| Score | Meaning |
|---|---|
| 0-3 | Something is factually wrong that an interviewer would correct. |
| 4-6 | Correct but shallow; confuses nearby concepts. |
| 7-8 | Correct and precise. |
| 9-10 | Correct, precise, and distinguishes the general case from edge cases. |

**Imperfect English never lowers this axis.**

## Depth

Does it sound like someone who built it, or someone who read about it?

| Score | Meaning |
|---|---|
| 0-3 | Repeats the definition. |
| 4-6 | Defines it well but mentions no trade-offs or costs. |
| 7-8 | Mentions a trade-off, or a number, or a failure mode. |
| 9-10 | All three, and connects them to a real architecture decision. |

Signals of depth: orders of magnitude (`~1ms` vs `~50ms`), percentiles
(`p99`, not "average"), pattern names, what happens when it fails, which metric
you'd watch.

## Terminology

Do you use the word a native practitioner would use?

| Score | Meaning |
|---|---|
| 0-3 | Describes the concept without naming it ("the thing that saves data"). |
| 4-6 | Names the basics, invents the rest. |
| 7-8 | Correct, consistent vocabulary. |
| 9-10 | Correct and natural, including the idioms of the trade. |

## Clarity

Is it understood without effort? This is where English counts.

| Score | Meaning |
|---|---|
| 0-3 | You have to re-read it to understand it. |
| 4-6 | Understandable, but it rambles or has no structure. |
| 7-8 | Clear structure: direct answer first, detail after. |
| 9-10 | Also concise. It ends and leaves room for the follow-up. |

## Levels

| Average | Level | Reading |
|---|---|---|
| < 5 | Junior | Doesn't clear the technical bar. |
| 5-6.9 | Mid | Passes if the rest of the interview compensates. |
| 7-8.4 | Senior | Passes comfortably. |
| ≥ 8.5 | Staff | The interviewer takes notes on this answer. |

## The shape of a good answer

1. **Direct answer in one sentence.** Don't open with "well, it depends".
2. **The mechanism.** Why it works that way.
3. **The trade-off or the number.** This is what separates mid from senior.
4. **Stop.** Don't pad. The silence after a good answer belongs to the
   interviewer, not to you.
