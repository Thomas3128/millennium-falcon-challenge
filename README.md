# Millennium Falcon Challenge

Solution to the Dataiku technical assessment : work in progress...

## Content

- `examples/` : data given in the read me (example1 to example4).


## Assumptions

The statement leaves a few cases open. Each one below states what was chosen and why.
This list grows as the implementation progresses.

### `empire.json`

**A missing `bounty_hunters` key is rejected, an empty array is accepted.**
An empty array is the rebels asserting "no hunter anywhere", which is legitimate and
yields 100% odds. A missing key, on the other hand, means the file is incomplete — and
defaulting it to "no danger" would turn a truncated file into the most reassuring answer
possible. Failing loudly beats lying quietly.

**Day `0` is a valid day for bounty hunters.** The statement defines day 0 as the first
day of the mission, so hunters can be waiting on the departure planet from the start.

**Bounty hunters standing on the departure planet on day 0 count as an encounter.**
The ship is on that planet that day, which is exactly the situation the 10% capture risk
describes. None of the four examples covers this case.

**A duplicated bounty hunter entry counts once.** Two mentions of the same
planet-and-day describe one watch, not two dangers, so they must not square the risk.

**A `countdown` of 0 is accepted.** It is not negative, and it is meaningful: the
Death Star fires today. Only a departure planet equal to the arrival planet survives it.

### `universe.db`

**A route whose origin equals its destination is ignored.** It can only burn fuel and
days to end up where it started, so it never belongs to an optimal path.

**Several routes between the same two planets are all kept.** The shortest is not always
the best: a longer jump may land on a day when the bounty hunters have left. Filtering
them at load time would discard information the search needs.

**Planet names are matched exactly**, with no trimming and no case folding. `"hoth"` and
`"Hoth"` are two different planets. The databases and JSON files of the statement are
consistent, so normalising would only hide a genuine data error.

### Travel model

**Waiting and refuelling are modelled as a single action**: "stay one day, tank full".
The statement describes them separately, but both cost one day and expose the crew to
the same risk, while refuelling never leaves less fuel than waiting. Waiting is
therefore strictly dominated, and one transition covers both without losing any
reachable outcome.

## Use of AI tools

TODO
