# Interview Coach

Practice technical interview answers the way you'd actually say them, and get
scored like a staff engineer would score you.

You set what you're practicing for (role, target level, years, stack), paste a
question, and answer it without notes. You get back:

- **A score on four axes:** technical accuracy, depth, terminology and clarity,
  averaged into a level (Junior, Mid, Senior, Staff) and compared with the level
  you're aiming for.
- **What worked**, quoting your own words.
- **What a senior would have added:** trade-offs, numbers, failure modes,
  pattern names.
- **A senior-level version of your answer**, written to be read out loud, that
  you can make shorter or longer.
- **English fixes** for the mistakes an interviewer would notice.
- **The follow-up question** the interviewer would ask next.

It was built for non-native speakers whose technical knowledge is solid but whose
spoken English slows them down. Imperfect English lowers *clarity*, never
*technical accuracy*. The full rubric is in [docs/rubric.md](docs/rubric.md).

## How the scoring works

There is no database of "correct answers". A language model (Claude, via the
Anthropic API) scores each answer against the [rubric](docs/rubric.md),
calibrated to your target level. The model only fills in the four axis scores
and the feedback; the average and the level are computed in code so they always
follow the rubric's bands. Treat the result as a demanding sparring partner, not
a verdict.

## Stack

- **Java 21 + Quarkus 3.33** (LTS)
- **[Quarkus LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/)**
  with the Anthropic provider: a declarative AI service returning a typed
  `Evaluation` record
- **Qute + [htmx](https://htmx.org)**: server-rendered HTML, no frontend build step
- No database: your practice target and recent questions stay in your browser

## Run it locally

You need Java 21 and Maven 3.9+, or just Docker.

```bash
cp .env.example .env        # then put your ANTHROPIC_API_KEY in .env
mvn quarkus:dev
```

Open http://localhost:8080.

**No API key?** Run in mock mode. Every answer gets the same sample evaluation and
nothing is sent to the API:

```bash
COACH_MOCK=true mvn quarkus:dev
```

With Docker:

```bash
docker build -t interview-coach .
docker run -p 8080:8080 -e ANTHROPIC_API_KEY=sk-ant-... interview-coach
```

## Configuration

| Variable | Default | What it does |
|---|---|---|
| `ANTHROPIC_API_KEY` | | API key from the Claude Console. Required unless mock mode is on. |
| `COACH_MODEL` | `claude-sonnet-5-5` | Model used for scoring and rewrites. |
| `COACH_ACCESS_CODE` | empty | If set, people must enter this code before practicing. |
| `COACH_DAILY_LIMIT` | `100` | Max model calls per day for the whole instance. |
| `COACH_MOCK` | `false` | Canned responses, no API calls. |
| `PORT` | `8080` | HTTP port (set automatically by most hosting platforms). |

### Sharing your instance

If you deploy it with your own key, anyone with the URL spends your credits.
Set `COACH_ACCESS_CODE` and share the code only with people you trust, keep
`COACH_DAILY_LIMIT` low, and set a spending limit in the Claude Console.

## Tests

```bash
mvn verify
```

Tests run in mock mode, so they need no key and spend no tokens. CI runs them on
every push.

## Roadmap

- [x] Manual practice: question + answer, scored against the rubric
- [x] Senior-level answer, shorter or longer
- [x] Recent questions in the browser
- [ ] Generate interview questions for a role and level
- [ ] Progress per axis over time, export to Markdown
- [ ] Voice: answer out loud, transcribed in the browser, scored with the same rubric

## Extras

- [docs/english-phrasebook-es.md](docs/english-phrasebook-es.md): phrases and
  common mistakes for Spanish speakers in technical interviews (in Spanish).

## License

[MIT](LICENSE)
