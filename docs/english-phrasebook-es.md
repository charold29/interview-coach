# English phrasebook para entrevistas técnicas

Frases que suenan naturales y que te compran tiempo mientras piensas.
Pensado para hispanohablantes.

## Arrancar una respuesta

| Frase | Cuándo |
|---|---|
| "The short answer is X, and the reason is..." | Cuando sabes la respuesta. Úsala siempre que puedas. |
| "There are two cases here — let me take them one at a time." | Cuando la respuesta depende. |
| "I'd want to know X before answering — can I assume Y?" | System design. Preguntar está bien visto. |
| "I haven't used that directly, but the way I'd reason about it is..." | Cuando no sabes. Mucho mejor que inventar. |

**No digas** "it depends" solo. Di de qué depende.

## Ganar tiempo sin sonar perdido

- "Let me think about that for a second."
- "Let me walk through it out loud."
- "I want to make sure I understood — are you asking about X or Y?"

Un silencio corto es mejor que "ehh... so... basically...".

## Vocabulario que separa mid de senior

| En vez de | Di |
|---|---|
| ask the other service | call downstream / fall back to the origin |
| the real data | the source of truth |
| it's slow sometimes | it shows up in the tail / it affects p99 |
| the app breaks | it fails open / it fails closed |
| too many requests at once | under high concurrency / a thundering herd |
| make it faster | reduce the round trips / cut the tail latency |
| it saves the data | it persists the data / it writes through |
| when there's an error | on the failure path |
| handle the error | degrade gracefully / surface it to the caller |
| a lot of data | high cardinality / high volume |
| the servers | the instances / the replicas |
| check if it works | validate / assert the invariant |
| I did it | I shipped it / I rolled it out |

## Verbos de ingeniería

`roll out`, `roll back`, `fan out`, `fall through`, `fall back`, `back off`,
`warm up` (a cache), `spin up` (an instance), `tear down`, `drain` (traffic),
`cut over`, `shard`, `throttle`, `batch`, `debounce`, `hydrate`, `propagate`.

## Errores comunes de hispanohablantes

| Mal | Bien | Por qué |
|---|---|---|
| retrieve correctly the data | correctly retrieve the data | el adverbio no va entre verbo y objeto |
| I have 5 years working | I've been working for 5 years | present perfect continuous |
| actually | currently / right now | "actually" significa "en realidad", no "actualmente" |
| I will explain you | I'll explain it to you | `explain` necesita `to` |
| informations, feedbacks, advices | information, feedback, advice | son incontables |
| depends of | depends on | preposición fija |
| in the other hand | on the other hand | preposición fija |
| I am agree | I agree | `agree` ya es verbo |
| the most easy | the easiest | superlativo corto |
| assist to a meeting | attend a meeting | `assist` es "ayudar" |
| realize a test | run a test / perform a test | `realize` es "darse cuenta" |
| support a load | handle a load | `support` en inglés es "dar soporte" |

## Cerrar una respuesta

- "...so that's the trade-off I'd weigh."
- "...but I'd want to measure it before committing to that."
- "Happy to go deeper on any part of that."

Y después **cállate**. Dejar aire es señal de seguridad.

## Pedir que repitan

- "Sorry, could you repeat the last part?"
- "Could you rephrase that? I want to make sure I answer the right question."

Pedir que repitan no resta puntos. Responder otra cosa sí.
