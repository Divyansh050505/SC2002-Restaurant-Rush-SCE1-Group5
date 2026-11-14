# AI chat history – Stage 3 – implementation planning, review, video and submission planning

- **Member:** Tomar Divyansh (Divyansh050505)
- **AI tool:** Claude (Anthropic), single continuous conversation
- **Format:** every prompt and every reply, in the original order. Attached files are named, and the AI's tool actions (reading files, running code) are shown as one-line notes.

---

## Exchange 1

### Prompt (Divyansh)

What is section 3.2 Boundary control entity structure in our pdf report about?

### Reply (Claude)

Section 3.2 explains **how your program is split into three kinds of classes, and why**. It uses the **Boundary–Control–Entity (BCE)** idea from the "Modelling an OO Application" lectures (slides ~452–462).

## What BCE means
| Type | Job | Must NOT do | Your class(es) |
|---|---|---|---|
| **Boundary** | Talks to the user: prints the screens, reads input, checks the input's *format* (is it a number in range?) | Hold game rules | `RestaurantUI` |
| **Control** | Coordinates: runs the turn steps in order, owns the lists of customers, orders and tables, and asks entities to do things | Print anything, or hold rules that belong to an entity | `RestaurantController` (plus `CustomerArrivalPlan` in Stage 2) |
| **Entity** | The "real things" in the game: holds their data and **their own rules** | Print or read input | `Customer` (+ subtypes), `Order`, `Table`, `Menu`, `MenuItem`, `Staff`/`Waiter`/`Chef`/`Cashier`, `ActionResult`, the two enums |

The slides' rule of thumb is that the **UI holds a reference to the controller**, and the **controller holds the entities**. Information flows UI → Controller → Entities, and results come back the other way.

## What the section should say (3–4 short paragraphs)
1. **Boundary:** `RestaurantUI` only displays the state and reads numbered choices. It rejects letters and out-of-range numbers itself, because that's *format* checking. It never seats customers, cooks, changes satisfaction or takes payment. It just calls the controller and prints the `ActionResult` message it gets back.
2. **Control:** `RestaurantController` runs each turn in the required order (`startTurn()` → Waiter → Chef → `endTurn()`) and owns all the collections: customers, waiting queue, orders, tables, menu, staff and revenue. It finds the customer or order the player picked and hands the action to the right staff member.
3. **Entity:** each entity enforces its own rules. For example, `Waiter.serve()` refuses an order that isn't READY, `Order` becomes READY on its last prep unit, and `Customer` loses satisfaction and leaves at 0.
4. **Why it's worth it:** rules live in one place, the UI can be tested on its own, and changes stay small. In Stage 2, adding VIP and Critic only touched the entities, plus one line in the controller and display labels in the UI. That last point backs up your design-evolution story.

**Two things to avoid in this section:**
- Calling `RestaurantUI` a control class. It's boundary.
- Claiming the UI has no rules if you can't back it up. Yours really doesn't, so that claim is safe.

Person 5 already has a draft of exactly this section in the repo, `docs/person5-report-draft.md`. It just needs a quick update to mention Stage 2's `CustomerArrivalPlan` before it's pasted in.

---

## Exchange 2

### Prompt (Divyansh)

What is section 3.4 Trade-offs about

### Reply (Claude)

Section 3.4 covers the **downsides you knowingly accepted**. Every design choice gains something and costs something. The brief's Section 5 asks for *"Trade-offs, and one alternative design you considered"*, and this section shows you understood the costs of your own design, not only its benefits.

**How it differs from nearby sections:**
- **3.3 (SOLID)** says what's *good* about the design.
- **3.4 (Trade-offs)** says what each choice *cost*, and why you accepted that cost.
- **3.5 (Alternative design)** describes one *whole different approach* you considered and rejected.

## The format for each trade-off
**The choice → what you gained → what it cost → why the cost was acceptable.** A table works well for 3–5 of them.

## Real trade-offs in your code (Stages 1–2)
| Choice | Gained | Cost | Why acceptable |
|---|---|---|---|
| **`ActionResult` instead of throwing exceptions** for rejected staff actions | A rejected move is a normal outcome with a clear message; the UI just prints it and re-asks | An extra class, and every caller must remember to check `isSuccess()` | Invalid choices happen all the time in play, so they aren't really "exceptional" |
| **Controller owns all the collections** (customers, queue, orders, tables, staff) | One source of truth, and the turn steps are easy to follow | `RestaurantController` grows large and risks doing too much | Stage 2 moved customer creation out into `CustomerArrivalPlan` to keep it in check |
| **Money as `int` cents** | Exact totals with no floating-point errors | Needs conversion for display, plus an explicit rounding rule (half cents round up) | Correct money matters more than a little extra code |
| **`Customer` keeps `final` lifecycle methods, with subclasses only overriding small methods** (`getSatisfactionLoss()`, `getDiscountPercent()`, `selectItem()`) | A subclass can't break the shared rules (seating, leaving, paying), so every type behaves safely | Less freedom: a new type can only change what those small methods allow | That's exactly what VIP and Critic needed, and it keeps substitution safe (LSP) |
| **Seeded randomness** for food choices and customer types | Variety between games, yet any game can be replayed exactly from its seed | The seed is passed through a Java system property, a hidden link between the UI, `Customer` and the controller | Reproducible test runs were required. Be honest that a cleaner design would pass the seed in explicitly. |
| **Abstract `Staff` base class** | One shared one-task-per-turn rule for Waiter, Chef and Cashier | The base class is small and has no abstract methods | The shared rule is real and avoids repeating it three times |
| **Payment runs before eating in `endTurn()`** | "Ready to pay this turn, pay next turn" falls out naturally | The correctness depends on step order, which is easy to break by reordering | It's documented with a comment and covered by tests |

**Don't use all 7.** Pick the **3–5 strongest**, and add 1–2 from Stage 3 once it's built. A likely one: *locking the price in `Order` when it's taken* means Happy Hour pricing stays correct, at the cost of storing price data twice (the menu price and the locked price).

**Tip:** the seeded-randomness one, with its honest "a cleaner design would pass the seed explicitly", is worth including. Graders reward spotting a real weakness more than a list of only positives, and it naturally sets up **3.5 Alternative Design**.

---

## Exchange 3

### Prompt (Divyansh)

[attached file: excerpt_from_previous_claude_message.txt] [attached file: SC2002_Group_Project_Briefing_1.pdf] Alright, we are following this split of work. Also, there were group assignment briefing slides which i didnt realise existed before hand, here they are.
We had tagged stage 2 already. 
I need you to help me do my part of stage 3. Give me the steps to do it first, dont do it urself.

### Reply (Claude)

I'll read the briefing slides first, since they may change what your part needs.

> *[Tool used: Bash — Extract text of the 12-page briefing slides]*

The briefing slides add requirements we didn't know about. Checking the exact Stage 3 wording in the brief before writing your steps.

> *[Tool used: Bash — Extract Stage 3 requirements from the brief]*

Here are the steps for your Stage 3 part, written as instructions for you to follow. First, though, the briefing slides add **requirements we didn't know about**, and some of them land on you as repo lead.

# ⚠️ New requirements from the briefing slides
| What | Detail | Effect on us |
|---|---|---|
| **AI chat history (slides 7–8)** | Every member uploads **all project-related prompts and AI replies, in order, in full** (a summary isn't enough) to **their own branch**, e.g. `ai-history/stage-1/chat-01.md`, `stage-2/…`, `stage-3/…`. The **README must link every stage and every member's AI history.** | **New task for everyone, including you.** This conversation counts, so export it. You add the README links. |
| **3 class diagrams (slide 5)** | One per stage, each matching **that stage's code** | Stages 1 and 2 are done. Stage 3 needs its own (P3). |
| **Sequence diagram (slides 5–6)** | From Stage 2 **or** 3. State the stage, starting conditions and outcome; include guards, alternatives or loops; explain how the interactions implement the use case. | P2's job. They need your Happy Hour method names early. |
| **Reflection format (slide 10)** | 4 parts: **what you learned, how you applied it, what challenged you, how you solved it**, with examples from your own work | Rework all the 6.x reflections into these 4 parts, including yours in 6.1 |
| **Video (slide 11)** | 2 min overview, 4 min gameplay, 4 min class diagrams + stage changes, 3 min sequence diagram, 2 min contributions | Plan segments around this |
| **Slide 3** | *"General support for future extensions is allowed"* | Stage 3 code can be designed to extend, but still no Stage 4 features |

# Your Stage 3 part
You own: **(A)** the locked-price base PR on day 1, **(B)** the whole Happy Hour feature, **(C)** merges and the `stage-3` tag.

## Step 0: Before any code (30 minutes, with the group)
1. On your machine: `git checkout main` → `git pull`. Confirm `stage-2` shows under Tags.
2. Re-read **brief §4.3, Happy Hour** and note every rule:
   - can be activated **once per game**, **at the start of a turn**
   - lasts **that turn and the next**, and its remaining duration is displayed
   - orders **taken while it's active** get **20% off**
   - each active turn, customers **waiting for a table or food** recover **+15 at turn start**, max 100
   - **VIP discount first, then Happy Hour.** The price is **locked when the order is taken**. VIP Burger = **$10.80**.
   - doesn't change orders already placed, and doesn't affect customers who have left
3. **Decide and write down** (these go in the README and report):
   - **Who counts as "waiting for a table or food":** I'd suggest the existing `isWaitingForService()` (WAITING, SEATED, ORDERED), so it reuses a Stage 1 rule.
   - **Rounding:** round after the VIP step, then again after Happy Hour, both half-up, using the same rule as Stage 2.
   - **Turn flow:** `startTurn()` → *(UI asks "Activate Happy Hour?" if unused)* → start-of-turn display → Waiter → Chef → `endTurn()`.
   - **Activating on turn 18:** allowed, and it only covers that one turn. Document it.
4. Tell **P3 (ComboMeal)** and **P2 (sequence diagram)** the class and method names you'll use, so they can build around them.

## Step A: Locked-price base PR (day 1, merge it first)
**Why first:** ComboMeal and Happy Hour both depend on `Order` storing its own price. Right now `Cashier` works out the bill **at payment time** (`customer.calculateBillCents(item price)`), but the brief says the price must be **locked when the order is taken**.

1. Branch: `git checkout -b feature/locked-order-price`
2. **`Order`:** add a private `priceCents` field, set **once** when the order is created, plus a getter `getPriceCents()`. Never change it after that.
3. **Where the price gets set:** in `Waiter.takeOrder()`, when it creates the `Order`, set the price to `customer.calculateBillCents(item.getPriceCents())`. That's still just the VIP discount, since Happy Hour comes in step B.
4. **`Cashier.collectPayment()`:** charge `order.getPriceCents()` instead of recalculating.
5. **`RestaurantUI`:** where it shows "Bill" and "paid $X", use `order.getPriceCents()`, so the screen always shows the locked price. **Ask P5 to review this part.**
6. **Check nothing changed in behaviour:** compile, then replay `test-runs/stage2_victory.txt` and `stage2_defeat.txt`. You should still get **$58.20 / 74.00** and **DEFEAT**. Run the existing test files, which should all still pass.
7. Commit (`"Lock order price when the order is taken"`) → push → PR → one approval → **merge**. Then post in the group chat: *"Locked price is merged, pull main before starting your feature."*

## Step B: Happy Hour
1. `git checkout main` → `git pull` → `git checkout -b feature/happy-hour`
2. **New class `HappyHour`** in the `control` package. The brief calls it *"a pricing policy"*, so a small class keeps it out of the already-large controller, which is good for SRP. Its job:
   - **State:** whether it has been used, and turns remaining (0, 1 or 2)
   - **Operations:** `canActivate()`, `activate()` (sets used and turns remaining = 2), `isActive()`, `getTurnsRemaining()`, `endTurn()` (counts down by 1), and `applyDiscount(cents)` (80%, rounded half-up)
   - Plain fields and `if` statements only. Keep it to course concepts: no ternaries, lambdas or streams.
3. **`Customer`:** add `recoverSatisfaction(int amount)`. It only works if `isWaitingForService()` is true (which skips customers who left, are eating or have paid), and caps at 100. Since `Customer` is P2's file from Stage 2, tell them you're adding it.
4. **`RestaurantController`:**
   - holds one `HappyHour` (created in the constructor, which makes it composition on the class diagram)
   - new public methods: `canActivateHappyHour()`, `activateHappyHour()` (returns an `ActionResult`, which is a failure if it's already been used), `isHappyHourActive()`, `getHappyHourTurnsRemaining()`
   - `activateHappyHour()` gives **+15 right away** to every customer still waiting, which covers the activation turn
   - in `startTurn()`: if Happy Hour is **still active** from last turn, give the +15 there, which covers the second turn
   - in `endTurn()`: call `happyHour.endTurn()` at the **end**, after payment, eating and waiting
5. **Locking the Happy Hour price:** when an order is taken while Happy Hour is active, apply the 20% **after** the VIP price and **before** storing it in `Order`. Two clean ways to do it:
   - **(a)** the controller passes the `HappyHour` into `waiter.takeOrder(...)`, which applies it when creating the order, **or**
   - **(b)** `Order` gets a constructor that takes the final price, and the controller works it out.

   Pick one. Either way you're **changing a Stage 2 method signature**, so note the reason for 4.3.3: *"the price must now be locked at order time, using a policy the Waiter didn't know about before."*
6. **`RestaurantUI` (coordinate with P5):**
   - right after `startTurn()`, if `canActivateHappyHour()`: ask *"Activate Happy Hour? 1 = Yes, 0 = No"*, using the existing `readChoice(0, 1)` so bad input re-prompts
   - on the start-of-turn screen: *"Happy Hour: ACTIVE, 2 turns remaining (this turn and next)"* / *"1 turn remaining (this turn)"* / *"available"* / *"used"*
   - order lines show the locked bill
7. **Test it yourself** (give the results to P4 for 5.4):
   - VIP Burger ordered during Happy Hour → **$10.80**. Regular Burger → $12.00. VIP Pasta → $12.96.
   - an order taken **before** activation keeps its old price
   - activate on turn T: active on T and T+1, gone on T+2. A second activation is **rejected** with a message.
   - waiting customers get +15 on both turns, capped at 100. Eating, paid and left customers are unchanged.
   - after Happy Hour ends, new orders are full price (VIP still gets 10% off)
   - activating on turn 18 still lets the game finish normally
   - the Stage 2 replays still match if Happy Hour is never activated. This is your regression check.
8. Commit in small steps → `git merge main` (pick up the Host and ComboMeal work if already merged) → fix any conflicts → PR → **P5 reviews the UI parts** → merge.

## Step C: Merges and tagging
1. **Merge order:** locked price (day 1) → **Host** → **ComboMeal** → **Happy Hour**. Before each PR, the author runs `git merge main` on their branch.
2. After everything is merged: fresh clone → compile → play one full game using **all three features** → replay the Stage 2 files with Happy Hour off → run all the test files.
3. Check: **Stage 3 class diagram** committed by P3 to `docs/stage3_class_diagram.*`, and **Stage 3 victory/defeat runs** committed by P2 to `test-runs/stage3_*`. Both have to be **in** the tagged version.
4. `git tag -a stage-3 -m "Stage 3: Host, ComboMeal, Happy Hour"` → `git push origin stage-3`
5. README: a **"Stage 2 → Stage 3"** section (new files, changed files with reasons, compare link `.../compare/stage-2...stage-3`), the Happy Hour rules and decisions from Step 0, and **links to every member's `ai-history` branch folder**.

## Your AI history (new requirement)
On your own branch (e.g. `ai-history/divyansh`), create `ai-history/stage-1/`, `stage-2/` and `stage-3/` and save this conversation's exchanges, **in order and in full**, under the stage each one belongs to. Tell the other 4 to do the same with their own chats.

Want me to explain any step in more detail before you start? Step A is the one to do first.

---

## Exchange 4

### Prompt (Divyansh)

Okay, as per the work split we followed, my group and I are done with the stage 3 coding. There should be 3 pull requests made now.
Here is the github repo: https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5.git

It is locally on my macbook documents, project folder named "SC2002-Restaurant-Rush-SCE1-Group5"

We have to merge the pull requests and tag stage 3, first I need you to do a thorough check of all the pull requests and see if everyone has done their part correctly, if EVERYTHING passes and everything is good, then you may merge, after the merge, test again and make sure EVREYTHING works and everything is good as per the project guidelines, brief and grading criteria, if it is, then you can tag stage 3.

### Reply (Claude)

I'll first check whether this session can reach your repo now, since it was blocked last time.

> *[Tool used: claude-code-remote: add_repo]*

> *[Tool used: Bash — Check gh auth and anonymous repo access]*

I can't do this from here yet: this session has **no access to your repo and isn't linked to your Mac**.
- GitHub refused the connection again ("you don't have access to divyansh050505/sc2002-restaurant-rush-sce1-group5"), and the repo is private.
- No tools for your computer are present, so I can't reach the project in your Documents folder either.

I can't merge or tag anything until one of these is set up. Pick whichever is easiest:

**Option 1: Link this chat to your Mac (best, I can do everything)**
1. Open this chat in the **Claude desktop app** on your MacBook
2. Choose **"Link to this computer"** (if it isn't offered for this task, start a new task from the desktop app with your computer selected)
3. Give it access to the `Documents/SC2002-Restaurant-Rush-SCE1-Group5` folder
4. Send me a message here

I'd then work in your local repo using your own Git login: fetch the 3 PR branches, review and test each, merge them in order (Host → ComboMeal → Happy Hour), push, re-test, and tag `stage-3`.

**Option 2: Give this session GitHub access**
In **claude.ai → Settings → Connectors → GitHub**, connect your account and allow the repository `SC2002-Restaurant-Rush-SCE1-Group5`. Then tell me, and I'll do the same review, merge and tag through GitHub.

**Option 3: I review, you merge**
For each of the 3 PR branches: on GitHub, switch to that branch → **Code → Download ZIP**, and attach all 3 here, plus the PR numbers and which feature each one is. I'll review and test every branch, check they work merged together, and tell you exactly what passes and what needs fixing. You then merge and tag yourself with the commands I give you.

Whichever you choose, I'll check each PR against the brief §4.3 (Host, ComboMeal with $24.30 / 3 units, Happy Hour with VIP Burger $10.80, locked prices, once per game, 2-turn duration, +15 recovery), confirm Stage 1 and 2 behaviour still works, run all the test suites, and only then merge and tag.

---

## Exchange 5

### Prompt (Divyansh)

after she sends her runs, and if evreything works, we can tag stage 3 right?

### Reply (Claude)

Yes. Once her runs are in and everything checks out, you tag `stage-3`. There's one catch: her runs have to be **recorded on the final merged code** and **committed before the tag**, because the tag freezes whatever is in `main` at that moment.

## Order of steps
1. **All 3 feature PRs are merged into `main`** (Host, ComboMeal, Happy Hour)
2. **She pulls the merged `main`** (`git checkout main` → `git pull`) and records the runs **on that code**. Runs recorded on her own branch, before the others' work is merged, may not replay on the final version.
3. **Her runs go in through a PR**, then get merged:
   - `test-runs/stage3_victory.txt` + its transcript
   - `test-runs/stage3_defeat.txt` + its transcript
   - a short note of the seed and the strategy (in `test-runs/README.md`)
4. **Final check on your Mac** (below)
5. **Tag**

## Final check before tagging (on the latest `main`)
```bash
git checkout main
git pull
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
java -cp out RestaurantApp < test-runs/stage3_victory.txt
java -cp out RestaurantApp < test-runs/stage3_defeat.txt
```
- [ ] It compiles with no errors
- [ ] The victory replay ends in **VICTORY** and the defeat replay in **DEFEAT**, matching her transcripts
- [ ] Her victory run **actually uses Host, a ComboMeal and Happy Hour** (the brief says Stage 3 must be independently playable, and the runs are your evidence)
- [ ] Spot-check the brief's two examples during a game: **Salad + Pasta combo = $24.30, 3 units**, and **VIP Burger during Happy Hour = $10.80**
- [ ] Any test files in `tests/` still pass, or the ones made outdated by Stage 3's new prompts have been updated
- [ ] **`docs/stage3_class_diagram`** is committed and matches the code. The briefing slides require **one class diagram per stage**, each matching its stage's code, so it belongs in the tagged version.
- [ ] The README has the **"Stage 2 → Stage 3"** section: new files, changed files with reasons, and the compare link `.../compare/stage-2...stage-3`

## Then tag
```bash
git tag -a stage-3 -m "Stage 3: Host, ComboMeal, Happy Hour"
git push origin stage-3
```
Check it shows under **Tags** on GitHub.

**Doesn't need to be in the tag:**
- the **sequence diagram**
- the **report**
- the **video**
- **everyone's AI history**, which goes on each person's own branch, as the briefing says


If you send me the merged `main` as a ZIP (branch dropdown → `main` → **Code → Download ZIP**) once her runs are in, I'll go through that whole checklist on the real code before you tag.

---

## Exchange 6

### Prompt (Divyansh)

[attached file: SC2002_Group_Project_Briefing_1.pdf] [attached file: SC2002_Restaurant_Rush_Group_Assignment.pdf] Okay until we wait for her, what other stuff are we still left to do? also, should my github repo look at the end when we submit? like what folders and what goes into what

### Reply (Claude)

Checking this PDF version of the brief for anything that changed since the Word version.

> *[Tool used: Bash — Extract submission sections of the PDF brief and compare briefing versions]*

The PDF brief is the same as the Word version, and the briefing slides are identical to the ones you sent before, so nothing new there. Two requirements from them shape the plan below:
- **Diagrams:** the brief wants a **final Stage 3 class diagram** with notes on encapsulation, polymorphism and composition, plus one sequence diagram. The briefing slides add **one class diagram per stage**, so you need **3 class diagrams + 1 sequence diagram**.
- **Links:** every link in the submission must be accessible to graders.


## 🚨 Do first: repo access for graders
Your repo is **private**. The README, the report's compare links and the AI history links are all useless to graders who can't open it, and the briefing says *"Check that all links are accessible."* Either:
- **message your TA/lab instructor** and ask for their GitHub username(s), then add them as collaborators (Settings → Collaborators), **or**

## Code and GitHub
| Task | Who | Status |
|---|---|---|
| 3 Stage 3 PRs reviewed and merged | P1 | Waiting |
| Stage 3 victory/defeat runs (recorded on the **merged** `main`) | P2 | Waiting |
| `docs/stage3_class_diagram` (matches the Stage 3 code) | P3 | ? |
| Final check, then **tag `stage-3`** | P1 | After the above |
| README: **Stage 2 → 3** section, real member names in the team table, links to all 3 diagrams + the sequence diagram, links to **every member's AI history**, **video timestamps** | P1 | |
| **AI history:** each member's own branch with `ai-history/stage-1/`, `stage-2/`, `stage-3/` chat files, complete and in order | **Everyone** | |

## Report (Google Doc)
| Section | Who |
|---|---|
| Stage 1 gaps still open: 3.2, 4.1.2, 4.1.3, 5.1, 5.2.1, 5.2.3/5.2.4, plus the cover, `**`, numbering and edge-case table fixes | P5 + owners |
| Stage 2 sections (the doc I made you) | As assigned |
| **4.3** Stage 2 → 3 (new requirements, new files, changed files with reasons, compare link) | P1, P2, P5 |
| **5.4** Stage 3 runs + edge cases, **5.5** full regression | P2, P4 |
| **2.1** Final class diagram + the 3 annotations | P3 |
| **2.2** Sequence diagram + scenario (stage, starting conditions, outcome) + walkthrough of guards and loops | P2 |
| **3.3–3.6** SOLID (only what's really in the code), trade-offs, alternative design, how player choices change the result | P1 + P5 |
| **6.1–6.3 reflections rewritten in the briefing's 4 parts:** what you learned, how you applied it, what challenged you, how you solved it. **Each member gets their own section under their own name**, so 5 sections, plus 6.4. | Everyone |
| 7 References, Appendix A video timestamps | P5 |
| Export to PDF, check the table of contents and images | P5 |

## Video (~15 min)
Cover all **7 segments from brief §6.2**, using the briefing's timing as a guide:
1. Launching the game from the submitted project
2. Meaningful manager choices, including a decision where staff are constrained
3. A dish going READY → SERVED → payment
4. A VIP or Critic behaviour
5. A Stage 3 feature
6. One victory and one defeat
7. The tag comparisons and the diagrams

**Every member speaks.** Upload it to a link that **stays accessible** (e.g. Google Drive with "anyone with the link"), then put the timestamps in the README.

## Declarations
- **Declaration of Original Work:** signed by all 5
- **Declaration on Use of GAI Assistance:** **one per member**, on the course forms. Be honest: you used AI, and your chat history is in the repo.

# Part 2: What the repo should look like at submission

## Branches and tags
```
main                      ← final Stage 3 code + docs (what graders read first)
ai-history/divyansh       ← your AI chats
ai-history/<member2> … <member5>   ← one branch per member
Tags: stage-1, stage-2, stage-3
```
Don't merge the `ai-history/*` branches into `main`. The briefing says each member's chats go on **their own branch**, and the README just links to them.

## `main` at the end
```
SC2002-Restaurant-Rush-SCE1-Group5/
├── README.md
├──.gitignore
├── src/
│   ├── RestaurantApp.java
│   ├── boundary/   RestaurantUI.java
│   ├── control/    RestaurantController.java, CustomerArrivalPlan.java
│   │               (+ any Stage 3 control class, e.g. HappyHour)
│   └── entity/     Staff, Waiter, Chef, Cashier, Host,
│                   Customer, RegularCustomer, VIPCustomer, CriticCustomer,
│                   Menu, MenuItem, ComboMeal, Order, Table, ActionResult,
│                   CustomerStatus, OrderStatus
├── tests/          all test files (updated so they pass on Stage 3)
├── test-runs/
│   ├── README.md                       ← seed + strategy for each run
│   ├── stage1_victory.txt / _transcript.txt
│   ├── stage1_defeat.txt  / _transcript.txt
│   ├── stage2_victory.txt / _transcript.txt
│   ├── stage2_defeat.txt  / _transcript.txt
│   ├── stage3_victory.txt / _transcript.txt
│   └── stage3_defeat.txt  / _transcript.txt
└── docs/
    ├── stage1_class_diagram.drawio +.svg/.png
    ├── stage2_class_diagram.drawio +.svg/.png
    ├── stage3_class_diagram.drawio +.svg/.png
    ├── sequence_diagram.drawio +.png
    └── (design notes, optional)
```
Keep the Stage 1 and 2 run files and diagrams in `main`. They're evidence, and their own tags still contain the matching code.

## Each member's AI history branch
```
ai-history/
├── stage-1/chat-01.md, chat-02.md …
├── stage-2/chat-01.md …
└── stage-3/chat-01.md …
```
Each file holds the **full conversation in order**: every prompt and every reply, not a summary. **For you, that includes this whole chat.** Copy each exchange into the stage it belongs to.

## README sections at the end
1. Title, SCE1 Group 5, **repository URL**
2. Team: real names and what each person did
3. Requirements + build/run commands
4. How to play (launch menu, seed replay, Waiter/Chef/Host choices, activating Happy Hour)
5. Game rules: base settings, **customer types** (Regular/VIP/Critic + the Critic rule), **combo rule**, **Happy Hour rules**, rounding
6. Stages table with **links to the 3 tags**
7. **Stage 1 → 2** and **Stage 2 → 3**: new files, changed or deleted files **with reasons**, **compare links**
8. Reproducible runs: replay commands for all 6 run files
9. Diagrams: links to the 3 class diagrams + the sequence diagram
10. **AI history:** links to every member's branch, per stage (5 members × 3 stages)
11. **Video:** link + timestamps for the 7 segments

## The zip you upload (`SCE1_Grp5.zip`, matching the brief's `FCS1_Grp1.zip` format)
```
SCE1_Grp5.zip
├── source/                 ← the repo at the stage-3 tag (src, tests, test-runs, docs, README.md)
├── README.md               ← a copy at the top level, so it's easy to find
├── SCE1_Grp5_Report.pdf
├── Video_Link.txt          ← or the.mp4 if it's small enough
└── Declarations/
    ├── Declaration_of_Original_Work.pdf
    └── GAI_Declaration_<Name>.pdf  × 5
```
Before uploading, **unzip it in a fresh folder, compile and run it, and click every link** in the README and PDF.


---

## Exchange 7

### Prompt (Divyansh)

the sequence diagram goes where? in the doc? or in the repo too?

### Reply (Claude)

**Both.** Brief §5 (Design evidence) says *"Include these in your PDF report and repository"*, and that covers the sequence diagram as well as the class diagrams.

| Where | What exactly |
|---|---|
| **Report (Google Doc)** | Section **2.2**: 2.2.1 the scenario (which stage, starting conditions, outcome), 2.2.2 the diagram image, 2.2.3 a walkthrough of the key messages, guards and loops |
| **Repo** | `docs/sequence_diagram.drawio` (the editable source) + `docs/sequence_diagram.png` (or `.svg`) on `main`, with a link to it from the README's **Diagrams** section |

**Notes:**
- **It doesn't have to be in the `stage-3` tag.** The tag only needs runnable Stage 3 code. You can commit the sequence diagram to `main` after tagging, through a normal PR. The same goes for the report and the README links.
- **It must match the code of the stage it describes** (briefing slide 5). If the scenario uses Happy Hour, check every method name in the diagram against the Stage 3 code. If it uses VIP pricing from Stage 2, say *"Stage 2 scenario"* in 2.2.1 and match it to the `stage-2` code.
- **The brief asks for one complete scenario** covering:
  - customer arrival and order
  - Waiter and Chef assignments
  - READY then SERVED
  - eating
  - payment
  - the result check
  - one Stage 2 or 3 behaviour

  It also needs guards and loops, and messages between the CLI, controller, staff and domain objects. A VIP ordering a Burger during Happy Hour and paying **$10.80** covers all of that in one scenario.

---

## Exchange 8

### Prompt (Divyansh)

Okay lets start planning the video, please read the instructions and the presentation brief, i believe a format and structure has already been provided.
who should record and film what segment?

### Reply (Claude)

Both documents give you a structure, and they fit together:
- **Brief §6.2:** the **7 segments that must appear**, with their timestamps listed in the README
- **Briefing slide 11:** the **5 time blocks** (2 + 4 + 4 + 3 + 2 = 15 min), plus *"Each member explains part of the work"*

So use the **5 time blocks as the running order**, and put the **7 required segments inside them**. I've assigned each part to the person who built or knows it best, so everyone can explain what they show.

# Video plan (~15 minutes)

| # | Time block (briefing) | Required segment (brief §6.2) | Presenter | What to show |
|---|---|---|---|---|
| **A1** | Overview | — | **P1 Divyansh** | What the game is, the 3 stages (base game → VIP/Critic → Host/Combo/Happy Hour), the team's roles |
| **A2** | Overview | **Seg 1:** game launching from the submitted project | **P5 Liu Yanxi** | Fresh clone at `stage-3` → compile → run; launch menu (new game / seed replay); quick tour of the start-of-turn screen |
| **B1** | Gameplay | **Seg 2:** meaningful choices, staff constrained · **Seg 3:** READY → SERVED → payment · **Seg 5:** Stage 3 feature (**Host**) | **P4 Jan Guirey** | A turn where the Waiter can only do one thing (seat vs serve); the Host seating a second customer; a dish going READY, served the next turn, eaten, paid; a rejected invalid choice re-prompting |
| **B2** | Gameplay | **Seg 4:** VIP or Critic behaviour | **P2 Lei Yueer** | VIP losing 5 vs Regular 8 vs Critic 12; Critic always ordering Pasta; a VIP bill 10% off |
| **B3** | Gameplay | **Seg 5:** Stage 3 feature (**ComboMeal**) | **P3 Shi Hong** | A combo ordered: Salad + Pasta = **$24.30, 3 units** |
| **B4** | Gameplay | **Seg 5:** Stage 3 feature (**Happy Hour**) | **P1 Divyansh** | Activating it, turns remaining shown, +15 recovery, **VIP Burger $10.80**, a second activation rejected |
| **B5** | Gameplay | **Seg 6:** one victory and one defeat | **P4 Jan Guirey** | Replay `stage3_victory.txt` and `stage3_defeat.txt` (input redirection, so it's an accelerated replay); point at the final screens and the 3 targets |
| **C1** | Diagrams | **Seg 7:** the diagrams | **P3 Shi Hong** | The 3 class diagrams in order; what each stage added; the encapsulation, polymorphism and composition notes on the Stage 3 one |
| **C2** | Diagrams | **Seg 7:** the stage tag comparisons | **P5 Liu Yanxi** | GitHub Tags page; compare links `stage-1...stage-2` and `stage-2...stage-3`; which files changed and why (e.g. `Customer` became abstract) |
| **D** | Sequence diagram | — | **P2 Lei Yueer** | The scenario (stage, starting conditions, outcome); walking through the messages UI → controller → staff → domain objects; the guards and loops |
| **E** | Contributions | — | **Everyone** (~20–25 s each) | What I built + one specific thing I learned. Same order as above. |

**Approximate speaking time:** P1 ~2.5 min, P2 ~4 min, P3 ~3.5 min, P4 ~3.5 min, P5 ~3 min. P2 gets the sequence diagram because they drew it, which is the hardest part to explain.

# How to record (simplest way)
**One Zoom or Teams call, recorded, with each person sharing their screen in turn.** You get one continuous video with no editing or stitching.
1. Everyone joins the call; P1 starts the **cloud or local recording**.
2. Each presenter shares their screen when it's their turn and talks.
3. Someone notes the **start time of each section** as you go. That gives you the README timestamps.
4. Download the recording → upload to **Google Drive** → set sharing to **"Anyone with the link"** → put the link in the README and in `Video_Link.txt` in the zip.

If you'd rather record separately: Mac **Cmd + Shift + 5**, Windows **Win + G**. Then P1 joins the clips in order (iMovie or CapCut).

# Prep checklist (before recording)
- [ ] **`stage-3` is tagged**, and everyone demos from a **fresh clone of the tag**, which is what "the submitted project" means
- [ ] **Readable screen:** terminal font **18–20 pt**, dark text on a light background or the reverse, browser zoomed to 125%
- [ ] **Scripted demos:** for B1–B4, prepare a **seed + input file** for each, so the right customer (VIP, combo, etc.) appears on cue. Replay with `java -cp out RestaurantApp < demo_happyhour.txt`, or type live from a written list of choices.
- [ ] **Diagrams** open in full-screen PNG; GitHub **compare pages** pre-loaded in tabs
- [ ] Each presenter writes **3–5 bullet points**, not a script to read word for word
- [ ] Quiet room, earphones with a mic, notifications off
- [ ] **Do a 1-minute test recording** first to check the audio and readability

# After recording
- [ ] Add the **timestamps for all 7 brief segments** to the README (from the table above), adjusted to the actual video
- [ ] Add the same to the report's **Appendix A**
- [ ] Open the Drive link in a private browser window to **check it works for someone not logged in**

---

## Exchange 9

### Prompt (Divyansh)

*[attached screenshot]*

*[attached screenshot]*

New announcement has come in, take note.
Also Why does my github say Divyansh050505 and claude? can you remove any mention of claude or whatever, even i made you generate and commit content, i want it to come from Divyansh050505

### Reply (Claude)

I'll get the exact Stage 1 → 2 class counts the announcement asks for from the code you sent me.

> *[Tool used: Bash — Count added, modified and unchanged classes Stage 1 to 2]*

# 1. The new announcement: what changes

**What it says:**
- The brief's 7 segments are the **required content**. The briefing's 5-block timing is **only a sample**, so you can arrange the video however you like.
- The **main emphasis** is **your system design and how you applied OODP concepts and SOLID**: explaining *how you applied them*, not just naming them.
- You must show a **change map** for **Stage 1 → 2** and **Stage 2 → 3**: how many classes were **added**, how many existing classes were **modified**, and how many **stayed unchanged**. The point is to show the design took on new requirements with **only essential changes**.

**Updated video plan:** keep the same presenters, but **shorten the gameplay** (still covering brief segments 2–6, just faster) and **give more time to design**:

| Part | Time | Presenter |
|---|---|---|
| Overview + launching the game (Seg 1) | ~1.5 min | P1 + P5 |
| Gameplay, compressed: choices with constrained staff, READY → SERVED → payment, VIP/Critic, Stage 3 features, victory + defeat replays (Segs 2–6) | ~4 min | P4, P2, P3, P1 (as before, each shorter) |
| **Design + OODP/SOLID:** the 3 class diagrams, *how* encapsulation, inheritance, polymorphism and composition appear in the code, and SRP/OCP/LSP with real examples | ~3.5 min | P3 + P1 |
| **Change maps 1 → 2 and 2 → 3** with counts + tag compare links (Seg 7) | ~2.5 min | P5 |
| Sequence diagram | ~2.5 min | P2 |
| Contributions + learning | ~1.5 min | Everyone |

**Stage 1 → 2 change map**, worked out from your actual `stage-1` and `stage-2` code:

| | Count | Classes |
|---|---|---|
| **Added** | **4** | `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `CustomerArrivalPlan` |
| **Modified** | **4** | `Customer` (became abstract, behaviour per type), `RestaurantController` (arrivals now use the plan), `Cashier` (charges the customer's bill), `RestaurantUI` (shows type labels and bills) |
| **Unchanged** | **11** | `RestaurantApp`, `Staff`, `Waiter`, `Chef`, `ActionResult`, `Table`, `Order`, `Menu`, `MenuItem`, `CustomerStatus`, `OrderStatus` |
| **Deleted** | 0 | — |

That's 15 classes → 19, with 11 of the original 15 untouched, which is a strong extensibility story. Put this table in the video, in the report (4.2) and in the README.

For the **Stage 2 → 3** map, I need the `stage-3` code. Send its ZIP once it's tagged and I'll produce the same table.

**Also from your screenshot:** the README still says *"The current version implements Stage 2"*, even though `main` now has Stage 3 code. Update it before tagging.

# 2. "Divyansh050505 and claude" on GitHub
**Why it shows:** that commit was made through a Claude coding session, which adds a `Co-Authored-By: Claude` line to the commit message. **You are still the commit's author.** GitHub just lists Claude as a co-author.

**I'd recommend leaving it as it is, and I won't help strip it out:**
- **The course allows AI, but requires you to disclose it.** The briefing says you must upload **all** your AI chats to your branch, and each member signs a GAI declaration. The co-author line matches what you're declaring anyway. Hiding it while the chat history shows the same work could look like you're misrepresenting authorship, which is a bigger risk than the line itself.

**Going forward:** commits you write and make yourself (`git commit` on your Mac) show only Divyansh050505. That's the honest way to keep authorship clear. Where AI helped, make sure that chat goes into your `ai-history` folder and onto your GAI declaration form.

---
