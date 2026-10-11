# Millennium Falcon Challenge

My solution to the Dataiku developer technical test: compute the odds that the Millennium
Falcon reaches Endor before the Death Star destroys it.

## Content

- `backend/`: the Java application — the command line interface, the web API and the
  web page.
- `examples/`: the four examples provided with the statement.
- `give-me-the-odds` and `give-me-the-odds.bat`: launchers for the command line interface.

## Build and run

Java 21 or later is the only prerequisite; the Maven wrapper takes care of the rest.

```sh
cd backend
./mvnw package
cd ..
```

In the Windows command prompt, use `mvnw.cmd` instead of `./mvnw`.

Both programs below take the path to a `millennium-falcon.json`. As the statement
specifies, a relative `routes_db` is resolved from the folder of that file, not from
where the command is run; an absolute path works too. In JSON, backslashes must be
doubled: write a Windows path as `C:\\data\\universe.db`, or simply `C:/data/universe.db`.

### Command line

From the root of the repository, replacing both placeholders with the paths to your
files:

```sh
./give-me-the-odds <path/to/millennium-falcon.json> <path/to/empire.json>
```

For example, with the statement's second example:

```sh
./give-me-the-odds examples/example2/millennium-falcon.json examples/example2/empire.json
```

```
81
```

Windows users can run `give-me-the-odds.bat` with the same arguments. The result is
printed on standard output as a whole percentage and nothing else; errors go to standard
error, with exit code `1` for a wrong invocation and `2` for a failed mission.

### Web application

From the root of the repository, start the server, replacing the placeholder with the
path to your `millennium-falcon.json`:

```sh
java -jar backend/target/falcon-0.0.1-SNAPSHOT.jar --falcon.config-file=<path/to/millennium-falcon.json>
```

For example, with the configuration of the statement's second example:

```sh
java -jar backend/target/falcon-0.0.1-SNAPSHOT.jar --falcon.config-file=examples/example2/millennium-falcon.json
```

The server reads the configuration and the routes once, at startup. Open
<http://localhost:8080/> and upload an `empire.json` to see the odds. If port 8080 is
already taken, add `--server.port=9090` (or any free port) to the command.

Behind the page, a single endpoint, `POST /api/odds`, answers with the odds as a fraction
between 0 and 1, such as `{"odds": 0.81}`: the same shape as the `answer.json` files of
the statement. The page turns it into the percentage it displays.

The same jar holds both programs: `java -jar` starts the web application, and the
`give-me-the-odds` script selects the command line one.

### Tests

```sh
cd backend
./mvnw test
```

## Assumptions

The statement leaves a few cases open; here is what was chosen and why.

### `empire.json`

- **A missing `bounty_hunters` key is rejected, an empty array is accepted.** An empty
  array means "no hunters" and gives 100%; a missing key means a truncated file, which
  should fail rather than look safe.
- **Day `0` is valid**: it is the first day of the mission.
- **Hunters on the departure planet on day 0 count as an encounter**: the ship is there
  that day.
- **A duplicated hunter entry counts once**: it is the same watch, not a second danger.
- **A `countdown` of 0 is accepted**: only a ship already on its target survives it.

### `universe.db`

- **A route from a planet to itself is ignored**: it can never belong to an optimal path.
- **Several routes between the same two planets are all kept**: a longer jump may dodge
  the hunters.
- **An unknown departure or arrival planet gives 0%**: no route reaches it.

### Travel model

- **Waiting and refuelling are a single action**, "stay one day, tank full": both cost a
  day and carry the same risk, and refuelling never leaves less fuel.

### Displayed result

- **The percentage is rounded to the nearest integer** (`0.729` gives `73%`), **but never
  down to `0%` when Endor is reachable**: the statement reserves `0%` for an unreachable
  target, so tiny odds display as `1%`.

## Use of AI tools

I used **Claude Code**, Anthropic's coding assistant, throughout the project.

**Why.** I had not built an application from scratch since my studies, and this one
relies on technologies I had little practice of: Spring Boot, JDBC and JavaScript. I used
the assistant as a tutor and a reviewer, to learn the basics quickly while keeping the
design decisions to myself. I believe AI coding assistants have become reliable enough
to help prevent mistakes and write code more efficiently.

**What I wrote.** Most of the backend: the domain code beneath the Spring layer, written
by me and reviewed by the assistant to keep it clean and readable. I wrote some of the
unit tests and relied on the assistant to find the edge cases. I made the design
decisions, including every choice listed under *Assumptions*. I also wrote some of the
basic JavaScript.

**What the assistant wrote.** Most of the test suite, the command line entry point and
its launch scripts, the build configuration and the HTTP controller. On the front end,
it wrote the web page and its styling following my feedback. It also helped me debug
when I ran into errors.

**How I used it.** I asked for an explanation of every piece I did not write myself, so
that I can account for all of the code. The assistant also reviewed what I wrote: it
helped keep the code clean, corrected me on Jackson best practices, and suggested
validating data at construction time and writing readable error messages. Claims about
library behaviour were verified against the versions actually in use rather than taken
from memory, which mattered — Jackson 3 changed several defaults compared to Jackson 2.
