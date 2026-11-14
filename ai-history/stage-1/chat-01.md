# AI chat history – Stage 1 – planning, repository setup, class diagram, report sections

- **Member:** Tomar Divyansh (Divyansh050505)
- **AI tool:** Claude (Anthropic), single continuous conversation
- **Format:** every prompt and every reply, in the original order. Attached files are named, and the AI's tool actions (reading files, running code) are shown as one-line notes.

---

## Exchange 1

### Prompt (Divyansh)

[attached file: SC2002_Restaurant_Rush_Group_Assignment.docx] [attached file: OODP_Lab_Manual.pdf] So what are we supposed to do for our group project? What do we start with right now and its 5 of us so tell us how to split the work.

### Reply (Claude)

I'll read the assignment brief and the lab manual, then give you a game plan and a 5-way split.

> *[Tool used: Bash — Extract assignment brief text]*

> *[Tool used: Read — 6520ce15-OODP_Lab_Manual.pdf]*

> *[Tool used: Bash — Scan lab manual for schedule and assignment notes]*

## What the project is

You're building a **turn-based restaurant management game in Java that runs in the command line**. The player is the manager. Each turn they decide what the Waiter does (seat someone, take an order, or serve a dish) and which order the Chef works on. The Cashier works automatically. The game always runs 18 turns. You win if you finish with **at least 4 paid customers, at least $45 revenue and average satisfaction of at least 60**.

You build it in **3 stages**, and each one must be a runnable version tagged in GitHub:

| Stage | Adds | Git tag |
|---|---|---|
| 1 | The full base game: Regular customers, Salad, Burger and Pasta, Waiter, Chef, Cashier, tables, queue, satisfaction, leaving, payment, win and lose | `stage-1` |
| 2 | VIP customers (lose 5 satisfaction per turn, 10% off) and Critic customers (lose 12, plus a special behaviour you invent and document) | `stage-2` |
| 3 | A Host (seats 1 extra customer per turn), ComboMeal (two items, 90% of the combined price), Happy Hour (20% off and +15 satisfaction, can be used once, lasts 2 turns) | `stage-3` |

There's **no live demo**. What you submit is one zip with the code, a README, a PDF report (UML class diagram, sequence diagram, design discussion), a roughly 15-minute group video, and declarations from every member.

**Look at where the marks are:**

| Part | Marks |
|---|---|
| UML class diagram | 25 |
| UML sequence diagram | 20 |
| Design discussion | 15 |
| **Code** | **20** |
| Process (stage tags, README change analysis, video, reflection) | 20 |

Only 20% is code and 45% is diagrams. So don't put all 5 people on coding. A big part of the grade is showing **how your design made Stage 2 and Stage 3 easy to add**.


## What to do right now

1. **Everyone reads Sections 2–4 of the brief.** Pay special attention to the turn-step table (3.2) and the Pasta example (3.3). Most bugs will come from getting the timing wrong.
2. **Have one call of about an hour and agree on the class design before anyone codes.** It's the most important thing you'll do all project, because Stage 2 and 3 should mostly mean *adding new classes* rather than rewriting old ones. That's exactly what the README "change analysis" and the design marks reward. Here's a design you can start from:
   - **Customer (abstract)** with methods like `getSatisfactionLoss()`, `applyDiscount(cents)` and `chooseItem(menu)`. Stage 1 only has `RegularCustomer`. In Stage 2 you add `VIPCustomer` and `CriticCustomer` as new files. The brief bans checks like "if customer is VIP..." in the controller, so this polymorphism isn't optional.
   - **MenuItem**, with Salad, Burger and Pasta as plain items. In Stage 3, `ComboMeal` *contains* two MenuItems. That's your composition example for the class diagram.
   - **Order** (item, prep units done, status, **price locked in cents**) and **Table**
   - **Enums:** `OrderStatus` (for example IN_PREP / READY / SERVED / PAID / CANCELLED) and a customer state (WAITING / SEATED / ORDERED / EATING / READY_TO_PAY / PAID / LEFT)
   - **Staff:** `Waiter`, `Chef`, `Cashier`. Stage 3 adds `Host`.
   - **GameController / TurnEngine**, which runs the 6 steps, plus **RestaurantState** (queue, tables, revenue, paid customers)
   - **ConsoleUI**, which *only* displays things and reads input. The brief says game rules must not live in the UI.
3. **Settle these decisions on the same call:**
   - **How customers pick food.** It has to be deterministic, for example cycling Burger → Pasta → Salad by arrival order. Check that a win is still possible with your rule. Four customers need to average $11.25 or more, so you can't have mostly Salads.
   - **What the Critic does.** Keep it simple and easy to document, for example "always orders Pasta" or "loses an extra 10 if their food waits more than 1 turn after READY".
4. **One person creates the GitHub repo** with the package folders and empty class skeletons, and adds everyone. Agree on a rule: everyone works on their own branch and merges through pull requests. Your commit history is marked evidence.

## How to split it among 5 people

| Person | Main job | Later stages |
|---|---|---|
| **A: Lead / game engine** | Repo setup, `GameController`, the 6-step turn loop, `RestaurantState`, merging, placing the tags | Stage 3 Happy Hour turn logic (+15 satisfaction at turn start, 2-turn duration, once per game) |
| **B: Domain objects** | `Customer` hierarchy, `Order`, `MenuItem`, `Table`, enums, satisfaction and leaving logic | **All of Stage 2** (VIP, Critic) |
| **C: Staff + CLI** | `Waiter`, `Chef` and `Cashier` actions and their validation, `ConsoleUI` (start-of-turn display, end-of-turn summary), input that rejects invalid choices and asks again | Stage 3 `Host` |
| **D: Pricing + testing** | Money helper (cents to "$xx.xx"), bill calculation, **reproducible test runs** (see tip below), test notes | Stage 3 `ComboMeal` and the discount order (VIP discount first, then Happy Hour) |
| **E: UML + report** | Class diagram updated after every stage, sequence diagram, design discussion (SRP/OCP/etc. only where it's real), README stage comparisons | Stitching the video together, collecting the declarations |

E isn't "the person who doesn't code". E's work is worth 45+ marks. E should also review other people's pull requests so the diagrams match the code exactly, since "match to code" is graded.

**Everyone** records their own video segment and must understand the part they explain.

## Things that will cost you marks if you miss them

- **Don't merge Stage 2 code until `stage-1` is tagged.** The `stage-1` commit must contain no Stage 2 or 3 features, and the code history has to show real stages. You can't build everything first and fake the history afterwards.
- **Timing rules:** the Waiter acts before the Chef. A dish that becomes READY this turn can only be served next turn. Eating takes one full turn. A customer who becomes READY_TO_PAY this turn pays next turn. The Cashier takes the oldest customer first.
- **An invalid choice must not use up the staff member's task.** Show an error and ask again.
- **The turn-16 customer can never pay.** That's intended, not a bug.
- **Tip for reproducible runs:** use a plain `Scanner(System.in)`. Then D can save each winning or losing run as a text file of inputs and replay it with `java Main < stage1_victory.txt`. The TA can rerun it exactly, and it makes the video's victory/defeat segment easy to record.
- **GAI declaration:** each member signs one, so be honest about any AI help you used, including this plan.

Next, I can write the Stage 1 class skeleton (fields and method signatures only) so person A can push it to the repo. I can also turn this plan into a shareable task board for your group.

---

## Exchange 2

### Prompt (Divyansh)

Do we code first or make class diagram first and then keep updating the class diagram as we code?

### Reply (Claude)

Do a **rough class diagram first**, then code, and keep updating the diagram as the code changes. Don't do either one fully before the other.

## Why not code first?

With 5 people coding at once and no shared design, everyone invents their own idea of what `Customer` or `Order` looks like. When you merge on, nothing fits together. Worse, you probably won't have the abstract `Customer` class or the `MenuItem` that Combo can build on. Then Stage 2 and 3 become rewrites instead of add-ons, and that costs you design marks and makes the README "change analysis" look bad.

## Why not a perfect diagram first?

You'll be wrong about details. You'll discover you need a `getRemainingUnits()` method, or that `Table` needs a reference to its customer. If you spend 3 days on a polished UML diagram, you'll redraw it anyway. The brief also only grades the **final** diagram, which has to match the **Stage 3 code** exactly.

## What to actually do

1. **Day 1 (your design call): sketch the "contract" diagram.** Use a whiteboard, paper or draw.io. Include only:
   - class names and which package each lives in
   - inheritance (`Customer` → `RegularCustomer`, `Staff` → `Waiter` / `Chef` / `Cashier`)
   - who holds what (`RestaurantState` has a queue and 2 `Table`s; an `Order` has a `MenuItem`)
   - the **public methods** other people will call, e.g. `customer.getSatisfactionLoss()`, `order.advanceOneUnit()`, `waiter.seat(customer, table)`

   Leave out private fields, getters and setters, and perfect notation. This sketch is what lets 5 people code in parallel without stepping on each other.
2. **Person A turns that sketch straight into empty Java classes** (method signatures that return dummy values) and pushes them. Now the code *is* the agreed design, and everyone fills in their own classes.
3. **Code Stage 1.** If someone needs to change a public method or add a class, they mention it in the group chat or the PR. Small private changes don't need discussion.
4. **At each tag (`stage-1`, `stage-2`, `stage-3`), person E updates the diagram to match the code.** Keep a snapshot of each version. They're useful in the report and video to show how the design grew, which is part of the "Thinking and development process" marks.
5. **After `stage-3`, make the final polished diagram** from the real code: full visibility (`+` / `-`), multiplicities (Restaurant 1 → 2 Tables), enums, and the annotations for encapsulation, polymorphism and composition. Double-check every class and method name against the code, because "match to code" is graded.

The **sequence diagram** goes the other way: do it **last**, after Stage 3 works. It has to show one real scenario (arrival → order → Waiter/Chef → READY → SERVED → eating → payment → result check, plus VIP pricing or Happy Hour), and it's much easier to draw by following what your code actually does.

Want me to draft that day-1 contract sketch (classes, relationships and key public methods for Stage 1) so you can take it straight into your call?

---

## Exchange 3

### Prompt (Divyansh)

Okay we have 5 people, can you split the work evenly across us, JUST FOR STAGE 1 BECAUSE THEY STRICTLY SAID WE MUST WORK IN ORDER OF STAGES

### Reply (Claude)

Here's an even split for Stage 1 only. Everyone writes real code **and** owns one non-code job, so the workload is roughly equal and everyone has something to explain in the video.

## Who does what

### Person 1: Game controller + repo lead
**Code:**
- `Main`
- `GameController`: the 18-turn loop and calling the 6 steps in the right order (arrivals → Waiter → Chef → eating/payment → waiting/leaving → summary), then the final result at turn 18
- The "keep asking until the choice is valid or Wait" loops for the Waiter and Chef (calls Person 5's UI and Person 4's / Person 3's actions)
- `ArrivalSchedule`: one customer on turns 1, 4, 7, 10, 13, 16

**Non-code:**
- Create the GitHub repo, push the empty skeleton, review and merge pull requests
- README build/run instructions
- Place the `stage-1` tag at the end

**Done when:** a full 18-turn game runs from start to VICTORY/DEFEAT without crashing.

### Person 2: Customers + satisfaction
**Code:**
- `Customer` (abstract), `RegularCustomer`, `CustomerStatus` enum
- The food choice rule in `chooseItem()` (e.g. cycling Burger → Pasta → Salad by customer number)
- `loseSatisfaction()` (−8, never below 0) and `isWaitingForService()` (true for WAITING / SEATED / ORDERED)
- **Step 5 (waiting and leaving):** at turn end, every waiting customer loses satisfaction. At 0 they leave: status LEFT, their order is cancelled, their table is freed, they're removed from the queue, and the departure count goes up.

**Non-code:**
- Find one **victory** and one **defeat** input sequence, save them as `stage1_victory.txt` and `stage1_defeat.txt`, and check they replay exactly with `java -cp out Main < stage1_victory.txt`

**Done when:** the brief's Pasta example satisfaction goes 92 → 84 → 76 → 76 and stops dropping once served.

### Person 3: Menu, orders, kitchen
**Code:**
- `MenuItem`, `Menu` (Salad 900 cents / 1 unit, Burger 1500 / 1, Pasta 1800 / 2)
- `Order` + `OrderStatus` enum: `advanceOneUnit()` (becomes READY on the last unit), `markServed()`, `markPaid()`, `cancel()`, and the price locked in cents when the order is created
- `Table`: `isFree()`, `assign()`, `release()`
- `Chef.prepare(order)` returning an `ActionResult` (fails if the order isn't PREPARING)

**Non-code:**
- Draw the **Stage 1 class diagram** in draw.io from the real code before the tag, and save it as the "Stage 1 snapshot" for the report

**Done when:** Pasta takes 2 Chef turns to become READY, Burger and Salad take 1, and cooking a READY or cancelled order is rejected with a message.

### Person 4: Waiter, Cashier, restaurant state
**Code:**
- `Restaurant`: waiting queue, 2 tables, all orders, paid customers list, `revenueCents`, departure count, `getFreeTable()`, `getAverageSatisfaction()` (as a `double`; N/A if nobody has paid), and `isVictory()` (≥4 paid, ≥4500 cents, average ≥60)
- `Staff` (abstract), `Waiter`: `seat()`, `takeOrder()`, `serve()`, each checking the rules and returning `ActionResult`
- `ActionResult` (success + message)
- `Cashier` + **Step 4:**
  - first pay the oldest customer who was *already* READY_TO_PAY (add revenue, record satisfaction, mark PAID, free the table)
  - *then* switch customers who were served on an earlier turn from EATING to READY_TO_PAY

**Non-code:**
- **Edge-case test notes:** a table of test, expected result and actual result. Cover seating with both tables full, serving a non-READY dish, taking an order twice, the turn-16 customer never paying, and a customer leaving while their order is half-cooked.

**Done when:** in the Pasta example the customer is served turn 4, becomes READY_TO_PAY turn 5, and pays $18.00 on turn 6.

### Person 5: Console UI
**Code:**
- `ConsoleUI` (prints and reads input only, no game rules):
  - **Start-of-turn screen:** turn, revenue, average satisfaction, queue, both tables, each customer's satisfaction and order status, available staff actions
  - **Numbered menus** for the Waiter (seat / take order / serve / wait) and the Chef (pick order / wait)
  - **Input safety:** letters, blanks or out-of-range numbers print an error and re-ask. It must never crash.
  - **End-of-turn summary:** served count, departures, revenue, average, turns used
  - **Final screen:** VICTORY/DEFEAT, plus which of the 3 targets passed or failed
- A small helper to format money: `formatCents(1830)` returns `"$18.30"`

**Non-code:**
- Keep the **design decisions log** (food rule, display order, why `Customer` is abstract, why money is in cents, etc.), which becomes the design discussion section later

**Done when:** a non-programmer can play a full game just from what's on screen.

## Who depends on whom

Everyone codes against the **skeleton Person 1 pushes on**, so nobody is blocked waiting for someone else's logic:
- P1 (controller) calls **everyone**: P5's UI, P4's Waiter/Cashier/Restaurant, P3's Chef, P2's leaving step
- P4's Waiter uses P3's `Order` and `Table` and P2's `Customer`
- P2's leaving step uses P3's `Order.cancel()` and `Table.release()`

If you need to change a **public** method someone else calls, say so in the group chat first.


| Day | What happens |
|---|---|

**Each person's PR should include comments** (class purpose + author at the top, a one-liner per method). The lab manual says documentation is part of the marks, and it'll help everyone explain their part in the video.

I can write the skeleton files (all 17 classes with fields, method signatures and comments, no logic) for Person 1 to push on if you'd like.

---

## Exchange 4

### Prompt (Divyansh)


### Reply (Claude)


## Key dates

| Date | What's done |
|---|---|

## Week by week

### Stage 1

### Stage 2, then start Stage 3
Stage 2 is small. It's mostly 2 new customer classes plus a change to the arrival schedule, so it should take about 4 days.

### Stage 3, then freeze the code
Stage 3 is the biggest stage, with 3 separate features, so split it across people:
- **Alongside all week:** the diagram owner keeps the class diagram in sync with the Stage 3 code as it's merged

### Report, video, submission
  - **Final class diagram** from the Stage 3 code: visibility, multiplicities, enums, annotations for encapsulation, polymorphism and composition
  - **Sequence diagram:** one full scenario (arrival → order → Waiter/Chef → READY → SERVED → eating → payment → result check) that includes VIP pricing or Happy Hour
  - **Design discussion:** responsibilities table, SOLID principles only where your code really shows them, trade-offs, one alternative design, how player choices change the result

## Ground rules to keep you on schedule

- **Never start merging the next stage's code before the previous tag is placed.** You can *plan* Stage 3 while Stage 2 is in progress, but its code doesn't go into `main` until `stage-2` is tagged.
- **Write the README stage section on the day you tag.** It's easy then, and hard 2 weeks later when nobody remembers what changed.
- **Do a quick weekly check-in** night, 15 minutes): what's done, what's slipping, who needs help. If Stage 1 slips past ut back on extras rather than squeezing the report week, since the report and diagrams are worth more marks than the code.
- **Use Lab 4 and Lab 5** to show your TA your class diagram early and ask whether your Critic behaviour and combo rule are acceptable.

---

## Exchange 5

### Prompt (Divyansh)

Okay so i've decided to take up the responsibility of Person 1. I need to start by creating the github for myself and our group. Tell me exactly what to do step by step, with all settings and stuff to set as well.

### Reply (Claude)

Here's the full setup, in order. Commands are written for **Git Bash**, which comes with Git on Windows. They work the same in the Mac or Linux terminal.

## Part 1: Your own machine (about 15 minutes)

### 1. Install the tools
- **Git:** download from git-scm.com and keep the default options. On Windows, this also installs **Git Bash**.
- **JDK:** install **JDK 17 or 21** (Temurin from adoptium.net is easy). Check it worked:
  ```bash
  java -version
  javac -version
  ```
- **IDE:** the labs use Eclipse, so stick with that unless your group prefers VS Code or IntelliJ. Any IDE works with the setup below.

### 2. Tell Git who you are
Use the **same email as your GitHub account**. Otherwise your commits won't link to your profile, and commit history is part of your process evidence.
```bash
git config --global user.name "Your Name"
git config --global user.email "the-email-on-your-github@example.com"
git config --global init.defaultBranch main
```

### 3. GitHub account (skip if you already have one)
Sign up at github.com. Then **apply for the GitHub Student Developer Pack** (education.github.com) with your NTU email. It gives you **GitHub Pro free**, which you need to protect the `main` branch on a private repo (Step 6). Approval can take a few days, so apply now. Until then, skip Step 6.

## Part 2: Create the repo on GitHub

### 4. New repository
Click **+** (top right) → **New repository**:

| Field | Set to |
|---|---|
| Owner | Your account |
| Repository name | `SC2002-Restaurant-Rush` (or include your lab group, e.g. `FCS1-Grp1-Restaurant-Rush`) |
| Description | "SC2002 group assignment: turn-based restaurant management game (Java CLI)" |
| Visibility | **Private** (see the note below) |
| Add a README | ✅ Yes |
| Add.gitignore | ✅ Template: **Java** |
| Choose a license | None |

Click **Create repository**.


### 5. Repository settings
Go to the **Settings** tab of the repo.

**General:**
- Default branch: `main`
- Features: ✅ **Issues** (you'll use these for tasks). ❌ Wiki (not needed). ✅ Projects (optional task board).
- Pull Requests section:
  - ✅ Allow merge commits
  - ✅ Allow squash merging
  - ❌ Allow rebase merging (avoid confusion)
  - ✅ **Automatically delete head branches**, which removes a branch after its PR is merged and keeps things tidy

**Collaborators** (left sidebar → **Collaborators** → **Add people**):
- Add all 4 teammates by GitHub username or email
- Each person must **accept the invite** from their email or github.com/notifications. Chase anyone who hasn't by.

### 6. Protect `main` (once you have Pro)
Settings → **Branches** → **Add classic branch protection rule**:
- Branch name pattern: `main`
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: **1**
  - ✅ Dismiss stale pull request approvals when new commits are pushed
- ❌ Leave "Do not allow bypassing the above settings" **unticked**, so you as admin can fix emergencies (like a broken build at midnight)
- Click **Create**

Now nobody can push straight to `main` by accident. Every change goes through a PR that someone else approves, which gives you clean, reviewable history for the "development process" marks.

If you don't have Pro yet: GitHub will show a warning that protection doesn't apply to private repos on a free account. Skip this step, tell everyone "never push to main directly", and come back to it when Pro is approved.

## Part 3: Set up the project folder

### 7. Clone to your computer
On the repo page: green **Code** button → **HTTPS** → copy the URL. Then:
```bash
cd ~/Documents            # or wherever you keep projects
git clone https://github.com/YOUR-USERNAME/SC2002-Restaurant-Rush.git
cd SC2002-Restaurant-Rush
```
The first push will open a browser window asking you to sign in to GitHub. Approve it once.

### 8. Create the folder structure
```
SC2002-Restaurant-Rush/
├── src/
│   ├── Main.java
│   ├── boundary/        ConsoleUI
│   ├── control/         GameController, ArrivalSchedule
│   └── entity/          Customer, Order, Menu, Table, Staff,...
├── test-runs/           victory/defeat input files (Person 2)
├── docs/                diagrams, design log, test notes
├──.gitignore
└── README.md
```
```bash
mkdir -p src/boundary src/control src/entity test-runs docs
```
Git ignores empty folders, so put a placeholder in the ones that will stay empty for now:
```bash
touch test-runs/.gitkeep docs/.gitkeep
```

### 9. Extend the `.gitignore`
Open `.gitignore` (GitHub's Java template already ignores `*.class`). **Add** these lines at the bottom so IDE junk doesn't cause merge conflicts between teammates on different IDEs:
```
# Build output
out/
bin/
sources.txt

# Eclipse.project.classpath.settings/

# IntelliJ.idea/
*.iml

# VS Code.vscode/

# OS junk.DS_Store
Thumbs.db
```

### 10. Write the first README
Replace the README contents with:
````markdown
# SC2002 Restaurant Rush – Group [Lab Group / Group No.]

Turn-based restaurant management game (Java, command line).

## Team
| Member | Stage 1 responsibility |
|---|---|
| Ironeye | Game controller, repo lead |
| (name) | Customers + satisfaction |
| (name) | Menu, orders, kitchen |
| (name) | Waiter, Cashier, restaurant state |
| (name) | Console UI |

## Requirements
JDK 17 or later. No external libraries, works offline.

## Build and run
**Windows (Command Prompt):**
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out Main
```
**Mac / Linux / Git Bash:**
```
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out Main
```

**Replay a recorded run:** `java -cp out Main < test-runs/stage1_victory.txt`

## Stages
| Tag | Contents |
|---|---|
| stage-1 | (to be filled) |
| stage-2 | (to be filled) |
| stage-3 | (to be filled) |

## Stage comparisons
(Filled in when each stage is tagged.)

## Video timestamps
(To be filled in.)
````

### 11. First commit and push
This one commit goes straight to `main`, since there's nothing to review yet. If you already set up branch protection, you're allowed to bypass it as admin.
```bash
git add.
git commit -m "Set up project structure, gitignore and README"
git push origin main
```
Refresh GitHub and check that the folders and README are there.

## Part 4: Organise the work on GitHub (makes your process marks easy)

### 12. Milestones
Go to **Issues** → **Milestones** → **New milestone**:

### 13. Labels
Go to **Issues** → **Labels** and add `stage-1`, `stage-2`, `stage-3`, `report`, `bug`.

### 14. One issue per Stage 1 task
**Issues** → **New issue**, one per person's job. Assign the person, set milestone `Stage 1`, label `stage-1`. For example:
- "Implement GameController turn loop + ArrivalSchedule" → you
- "Implement Customer, RegularCustomer, satisfaction + leaving step" → Person 2
- "Implement MenuItem, Menu, Order, Table, Chef" → Person 3
- "Implement Restaurant, Waiter, Cashier, ActionResult + payment step" → Person 4
- "Implement ConsoleUI + input validation" → Person 5
- Plus the non-code ones (victory/defeat runs, Stage 1 diagram, edge-case tests, design log, README)

When someone opens a PR, they write **"Closes #3"** in the description and the issue closes automatically when it's merged. That's free evidence of who did what.

## Part 5: The skeleton (after your design call)

### 15. Push the agreed class skeleton
After the call where you agree on class and method names:
```bash
git checkout -b setup/skeleton
# add all the empty classes with signatures + comments
git add.
git commit -m "Add Stage 1 class skeleton from agreed design"
git push -u origin setup/skeleton
```
On GitHub, open a **Pull Request** from `setup/skeleton` into `main`. Have one teammate approve it, then **Merge**. Check it compiles first using the build commands in the README.

## Part 6: Paste this into your group chat

> **Git workflow for the SC2002 project** (please follow exactly)
>
> **One-time setup**
> 1. Accept the GitHub collaborator invite (check your email).
> 2. Install Git + JDK 17/21. Run:
>    `git config --global user.name "Your Name"`
>    `git config --global user.email "your-github-email"`
> 3. `git clone https://github.com/YOUR-USERNAME/SC2002-Restaurant-Rush.git`
> 4. Eclipse: File → New → Java Project → untick "Use default location" → browse to the cloned folder. **Don't commit** `.project`, `.classpath` or `bin/` (already in.gitignore).
>
> **Every time you start working**
> ```
> git checkout main
> git pull
> git checkout -b feature/your-task-name     (e.g. feature/order-chef)
> ```
> **While working**, commit small and often:
> ```
> git add.
> git commit -m "Add Order.advanceOneUnit and READY status change"
> git push -u origin feature/your-task-name
> ```
> **When your part compiles and works:**
> - On GitHub, click "Compare & pull request" → base: `main` → describe what you did → write "Closes #<issue number>" → assign 1 reviewer.
> - Reviewer: read it, pull the branch, check it compiles, approve. Then merge.
>
> **Rules**
> - ❌ Never push directly to `main`
> - ❌ No Stage 2/3 code (VIP, Critic, Host, Combo, Happy Hour) until I tag `stage-1`
> - ✅ Don't change a public method someone else uses without telling the group first
> - ✅ Comment every class (purpose + author) and every method (1 line)
> - ✅ Run `git pull` on main before starting new work, to avoid conflicts

## For later: tagging a stage
When Stage 1 is finished and checked:
```bash
git checkout main
git pull
git tag -a stage-1 -m "Stage 1: basic restaurant operation"
git push origin stage-1
```
It then appears under **Releases/Tags** on GitHub, and you can make compare links like `.../compare/stage-1...stage-2` for the README.

I can write the skeleton classes for Step 15 next, so all you have to do after the call is copy them in and push.

---

## Exchange 6

### Prompt (Divyansh)

[attached file: excerpt_from_previous_claude_message.txt] which one do i run, note i already have a github account

### Reply (Claude)

You run **all three**, one at a time, in Git Bash (or Terminal on Mac). Having a GitHub account already doesn't change anything. These lines don't create or log into an account. They just tell Git on *your computer* which name and email to stamp on every commit you make, so GitHub can match those commits to your profile.

## What to type

**1. Your name**, with your real name inside the quotes:
```bash
git config --global user.name "Ironeye Tomar"
```
Use whatever name your teammates and TA will recognise. It shows up on every commit.

**2. Your email.** This must match an email on your GitHub account:
```bash
git config --global user.email "your-github-email@example.com"
```
To find the right one:
1. Go to github.com → your profile picture (top right) → **Settings** → **Emails**
2. Look at the email listed there.
   - If **"Keep my email addresses private"** is ✅ ticked, GitHub shows an address like `12345678+yourusername@users.noreply.github.com`. **Use that one**, otherwise GitHub may block your push.
   - If it's unticked, use your normal email shown there (e.g. the Gmail you signed up with).

**3. Default branch name.** Copy this exactly. Your excerpt got cut off: it should end in `main`, not `ma`.
```bash
git config --global init.defaultBranch main
```

## Check it worked

---

## Exchange 7

### Prompt (Divyansh)

[attached screenshot]

[attached file: excerpt_from_previous_claude_message.txt] this is what i got after clicking create

### Reply (Claude)

That's expected, and it's fine. **Leave the rule as it is** and carry on.

## What the warning means
GitHub's free plan doesn't enforce branch protection on **private** repos. Your rule for `main` is saved, but it shows **"Not enforced"**. For now, nothing technically stops someone pushing straight to `main`.

## How to get it enforced (free)
The warning mentions Team/Enterprise, but **GitHub Pro also enforces it**, and students get Pro free:

1. Go to **education.github.com/pack** → **Sign up for Student Developer Pack** (or "Get student benefits")
2. Add and verify your **NTU email** (`@e.ntu.edu.sg`) on your GitHub account if it asks
3. Upload proof of enrolment when asked. A photo of your matric card or a screenshot of your NTU student portal works.
4. Wait for approval. It's often within a day, sometimes up to a week.

Once you're on Pro, the rule you just made switches to **enforced** automatically. Come back to this page to check that "Not enforced" is gone.

## Don't do these instead
- **Don't make the repo public** to get enforcement. Other groups could copy your code, and the lab manual says both sides get penalised.
- **Don't click "Upgrade"**. That's the paid plan, and you don't need it.

## Until it's enforced
The team just follows the rule. It's already in the group chat message: **never push directly to `main`, always open a PR**. Five people can manage that. If someone slips up, you can still review and fix it.

## One change to my earlier steps
Your repo is named **`SC2002-Restaurant-Rush-SCE1-Group5`**, so use that name everywhere instead of the shorter one:

**Clone (Step 5):**
```bash
cd ~/Documents
git clone https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5.git
cd SC2002-Restaurant-Rush-SCE1-Group5
code.
```

**README (Step 8):** change the top two lines to:
```markdown
# SC2002 Restaurant Rush – SCE1 Group 5

**Repository:** https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5
```
The submission zip will also need your lab group name, probably `SCE1_Grp5.zip`.

Next, carry on with **Step 3 (add the 4 collaborators)** if you haven't yet, then Steps 5–9: clone, create the folders, update `.gitignore` and the README, and make the first push.

---

## Exchange 8

### Prompt (Divyansh)

Last login: on ttys000
divyanshtomar@Divyanshs-MacBook-Air ~ % cd ~/Documents
git clone https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5.git
cd SC2002-Restaurant-Rush-SCE1-Group5
code.
Cloning into 'SC2002-Restaurant-Rush-SCE1-Group5'...
remote: Invalid username or token. Password authentication is not supported for Git operations.
fatal: Authentication failed for 'https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5.git/'
cd: no such file or directory: SC2002-Restaurant-Rush-SCE1-Group5
zsh: command not found: code
divyanshtomar@Divyanshs-MacBook-Air Documents %

### Reply (Claude)

Two separate problems, both quick to fix. Nothing is broken.

## Problem 1: "Authentication failed"
GitHub no longer accepts your account password from the terminal. Your Mac's keychain had an old saved login for github.com, so Git tried that and got rejected. The easy fix is to let **VS Code** handle the sign-in.

### Step 1: Clear the old saved login
Paste this into Terminal exactly as written and press Enter. It prints nothing if it works.
```bash
printf "protocol=https\nhost=github.com\n\n" | git credential-osxkeychain erase
```

### Step 2: Clone through VS Code instead
1. Open **VS Code** from Applications or Spotlight
2. Press `Cmd + Shift + P`, type **Git: Clone**, press Enter
3. Click **Clone from GitHub**
4. A popup says *"The extension 'GitHub' wants to sign in using GitHub"*. Click **Allow**.
5. Your browser opens. Click **Continue** / **Authorize Visual-Studio-Code**, and allow it to open VS Code again if asked.
6. Back in VS Code, a list of your repos appears. Pick **Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5**.
7. Choose your **Documents** folder as the destination → **Select as Repository Destination**
8. When it asks *"Would you like to open the cloned repository?"*, click **Open**

From now on, `git push` and `git pull` work from **VS Code's built-in terminal** (**View → Terminal**, or `` Ctrl + ` ``), because VS Code passes your GitHub login to Git. **Use the VS Code terminal for this project, not the separate Terminal app.**

## Problem 2: "command not found: code"
The `code` shortcut isn't installed yet. You don't need it now that you're cloning through VS Code, but it's handy for later:
1. In VS Code, press `Cmd + Shift + P`
2. Type **Shell Command: Install 'code' command in PATH** and press Enter
3. Enter your Mac password if asked
4. Close and reopen Terminal. `code.` now opens the current folder in VS Code.

If that command doesn't appear in the list, VS Code is probably still in your Downloads folder. Drag **Visual Studio Code** into **Applications**, reopen it and try again.

## Check it worked
In VS Code's terminal (it should already be inside the project folder), run:
```bash
pwd
git status
```
You should see something like:
```
/Users/divyanshtomar/Documents/SC2002-Restaurant-Rush-SCE1-Group5
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
```

Once that matches, continue from **Step 6** (creating the folders) in the VS Code terminal. Paste me the output if the sign-in or `git push` gives another error.

---

## Exchange 9

### Prompt (Divyansh)

[attached screenshot]

[attached screenshot]

Is everything set up correctly now?

### Reply (Claude)

Yes, it's all set up correctly:
- The repo is cloned into `Documents/SC2002-Restaurant-Rush-SCE1-Group5` ✅
- You're on `main` and in sync with GitHub (`up to date with 'origin/main'`) ✅
- `.gitignore` and `README.md` came down from GitHub ✅
- The second screenshot has no "Restricted Mode" bar, so you trusted the folder ✅
- The bottom-left shows `main` with a sync icon, so VS Code's Git is connected ✅

One thing I can't see is the **Java extension**. There's a Python icon in your sidebar but no Java one. Press `Cmd + Shift + X`, search **Extension Pack for Java** (by Microsoft) and install it if it isn't already there.

## Now do Steps 6–9 (about 5 minutes)

Run all of these in the **VS Code terminal** you already have open.

### Step 6: Folders
```bash
mkdir -p src/boundary src/control src/entity test-runs docs
touch src/boundary/.gitkeep src/control/.gitkeep src/entity/.gitkeep test-runs/.gitkeep docs/.gitkeep
```
The new folders should appear in the Explorer on the left.

### Step 7: `.gitignore`
Click `.gitignore` in the Explorer, scroll to the very bottom, and paste:
```
# Build output
out/
bin/
sources.txt

# Eclipse.project.classpath.settings/

# IntelliJ.idea/
*.iml

# VS Code.vscode/

# macOS / Windows junk.DS_Store
Thumbs.db
```
Save with `Cmd + S`.

### Step 8: README
Click `README.md`, select everything (`Cmd + A`), delete it, then paste the README from my earlier message, using the **updated top two lines** with `SCE1 Group 5` and the full repo name. Save with `Cmd + S`.

### Step 9: Commit and push
```bash
git add.
git status
```
`git status` should list `.gitignore`, `README.md` and the 5 `.gitkeep` files in green. Then:
```bash
git commit -m "Set up project structure, gitignore and README"
git push origin main
```

If VS Code pops up asking to sign in to GitHub again, click **Allow**.

### Check
Refresh the repo page on github.com. You should see:
```
docs/
src/
test-runs/.gitignore
README.md
```
with your README rendered underneath.

After that, the repo is fully set up. What's left on your list:
1. **Add the 4 collaborators** (Settings → Collaborators) if you haven't yet
2. **Milestones and labels** (Step 10)
3. **Apply for the Student Developer Pack** so branch protection gets enforced
4. **Run the design call**, then push the skeleton

Paste me the output of `git push` if anything looks off.

---

## Exchange 10

### Prompt (Divyansh)

[attached file: excerpt_from_previous_claude_message.txt] before i run these, REMEMBER TO STRICTLY KEEP TO STAGE 1 ONLY, any indication that we tocuhed upon stage 2 or 3 will be penalised in grading

### Reply (Claude)

Good call. I checked Steps 6–9 against that rule:

- **Step 6 (folders):** ✅ safe. `boundary` / `control` / `entity` / `test-runs` / `docs` are generic.
- **Step 7 (`.gitignore`):** ✅ safe. It only lists build and IDE files.
- **Step 8 (README):** ❌ **the template I gave you mentions `stage-2` and `stage-3`** in the Stages table and the "Stage comparisons" section. They're only placeholders, but to be strict, **use the Stage 1-only README below instead**.
- **Step 10 (milestones and labels):** ❌ I told you to create `Stage 2` / `Stage 3` milestones and labels. **Skip those for now.** Only create the `Stage 1` milestone and the `stage-1` label, and add the later ones after `stage-1` is tagged.

## Stage 1-only README (use this for Step 8)
````markdown
# SC2002 Restaurant Rush – SCE1 Group 5

Turn-based restaurant management game in Java (command-line).

**Repository:** https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5

## Team
| Member | Responsibility |
|---|---|
| Divyansh (Divyansh050505) | Game controller, repo lead |
| (name) | Customers + satisfaction |
| (name) | Menu, orders, kitchen |
| (name) | Waiter, Cashier, restaurant state |
| (name) | Console UI |

## Requirements
JDK 17 or later. No external libraries; works fully offline.

## Build and run
**Mac / Linux:**
```
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
java -cp out Main
```
**Windows (Command Prompt):**
```
dir /s /b src\*.java > sources.txt
javac --release 17 -d out @sources.txt
java -cp out Main
```

## Game settings
- 18 turns, 2 tables
- Staff: 1 Waiter, 1 Chef, 1 Cashier
- Customers arrive at the start of turns 1, 4, 7, 10, 13 and 16
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units)
- Victory: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60
````

I've also changed the team column heading from "Stage 1 responsibility" to just "Responsibility".

## What I'll stick to from now on
Until you tell me `stage-1` is tagged, everything I give you (skeleton classes, comments, README text, issues, diagrams, commit messages) will contain **only** Stage 1 content:
- **No** VIP, Critic, Host, ComboMeal or Happy Hour, not even in comments like "for future use" or "extend later"
- **No** classes, enum values, fields or methods that only exist for later stages
- `Customer` stays abstract with `RegularCustomer`, because the brief lists Regular customers as part of Stage 1 and an abstract class is ordinary OO design for Stage 1 on its own terms. But its comments and methods will only describe Regular behaviour.
- `MenuItem` will only have Salad, Burger and Pasta, with nothing about combos

You can go ahead and run Steps 6, 7 and 9 as written, using this README for Step 8.

---

## Exchange 11

### Prompt (Divyansh)

So wait was that it for person 1 job?
What do i tell my teammates now, who are doing person 2 and 3? Note that we all are very inexperienced with github so please explain to me as to how i shouild instruct each person for the work they have

### Reply (Claude)

Not quite. Setting up the repo was only the **admin** part of Person 1. You still have the most central code to write.

## What's left for you (Person 1)

| When | Task |
|---|---|
| | Run the design call. Push the **skeleton** (empty classes everyone fills in). Create one GitHub **issue** per person and assign it. |

**The skeleton is the most important thing.** Your teammates can set up Git, but they can't start coding until the skeleton is merged into `main`. Otherwise everyone invents different class names.

## What to send your teammates

Send 3 messages:
1. **A Git setup + workflow guide**, the same for everyone
2. **Person 2's task**
3. **Person 3's task**

They're written for complete Git beginners, so you can copy-paste them straight into WhatsApp.

### Message 1: Git guide (send to the whole group)

```
📌 SC2002 PROJECT – GIT SETUP

A few words you'll see:
• Repo = our shared project folder on GitHub
• Clone = download the repo to your laptop (only once)
• Branch = your own private copy to work on, so you don't break anyone else's code
• Commit = save a checkpoint of your work on your laptop
• Push = upload your commits to GitHub
• Pull Request (PR) = asking for your branch to be added into the main project
• Merge = your work gets added into main
• Pull = download the latest changes from GitHub

━━━━━━━━━━━━━━━━━━
PART A: ONE-TIME SETUP
━━━━━━━━━━━━━━━━━━
1. Accept the GitHub invite I sent (check your email or github.com/notifications)

2. Install:
   • Git: git-scm.com (Windows: keep all defaults)
   • JDK 17 or 21: adoptium.net
   • VS Code + the "Extension Pack for Java" extension
   (Eclipse is fine too if you prefer it)

3. Open a terminal (VS Code: View → Terminal) and run these 3 lines,
   using YOUR GitHub username and the email on your GitHub account:
   git config --global user.name "YourGitHubUsername"
   git config --global user.email "your-github-email"
   git config --global init.defaultBranch main

4. Clone the repo in VS Code:
   Cmd/Ctrl + Shift + P → type "Git: Clone" → "Clone from GitHub"
   → Allow sign in → pick SC2002-Restaurant-Rush-SCE1-Group5
   → save it in Documents → click "Open"

5. Reply "done" here once you can see the src, docs and test-runs folders in VS Code ✅

━━━━━━━━━━━━━━━━━━
PART B: EVERY TIME YOU WORK
━━━━━━━━━━━━━━━━━━
(Use the terminal INSIDE VS Code, in the project folder)

① Get the latest code first:
   git checkout main
   git pull

② Create your own branch (only the first time for each task):
   git checkout -b feature/your-task-name
   e.g. feature/customer or feature/order-menu
   (Already made it? Use: git checkout feature/your-task-name)

③ Write your code. Save often.

④ Save a checkpoint (commit) and upload it (push):
   git add.
   git commit -m "Short description of what you did"
   git push -u origin feature/your-task-name

   Good message: "Add loseSatisfaction to Customer"
   Bad message:  "update" / "stuff" / "asdf"
   Commit small and often, e.g. after each method works.

⑤ When your part compiles and works → open a Pull Request:
   • Go to the repo on github.com
   • Click the yellow "Compare & pull request" button
   • Base: main ← Compare: your branch
   • Title: what you did
   • Description: write "Closes #<your issue number>"
   • Reviewers (right side): add 1 teammate
   • Click "Create pull request"
   • Tell the group chat so someone reviews it

⑥ If someone asks YOU to review:
   • Open the PR → "Files changed" tab → read through
   • If it looks fine → "Review changes" → Approve
   • If something's wrong → leave a comment on that line

━━━━━━━━━━━━━━━━━━
RULES (important for grading)
━━━━━━━━━━━━━━━━━━
❌ NEVER push directly to main. Always use your own branch + PR
❌ ONLY edit YOUR OWN files. Need a change in someone else's file? Ask them
❌ Don't rename or change any PUBLIC method in the skeleton without telling the group
❌ STAGE 1 ONLY. No VIP, Critic, Host, Combo or Happy Hour code, not even comments.
   We'll lose marks if Stage 1 contains any of it.
✅ Comment every class at the top (what it's for + your name)
✅ Comment every method with 1 line saying what it does
✅ Before opening a PR, check the whole project compiles:
   Mac:     find src -name "*.java" > sources.txt && javac --release 17 -d out @sources.txt
   Windows: dir /s /b src\*.java > sources.txt  then  javac --release 17 -d out @sources.txt

🆘 If you get ANY error you don't understand, especially
"merge conflict", "rejected" or "detached HEAD":
STOP, don't try random commands, screenshot it and send it to me.
```

### Message 2: Person 2's task

```
👤 PERSON 2 – CUSTOMERS + SATISFACTION (Stage 1)

⏳ WAIT for my message saying "skeleton is merged" before coding.
Then run: git checkout main → git pull → git checkout -b feature/customer

📁 YOUR FILES (only edit these):
• src/entity/Customer.java          (abstract class)
• src/entity/RegularCustomer.java
• src/entity/CustomerStatus.java    (enum)

🧠 WHAT TO CODE

1. CustomerStatus enum:
   WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT
   WAITING = in the queue
   SEATED = at a table, no order yet
   ORDERED = order taken, food not served yet
   EATING = food served
   READY_TO_PAY = finished eating
   PAID = paid and gone
   LEFT = gave up and left without paying

2. Customer (abstract):
   • Fields (all private): id, satisfaction (starts at 100), status,
     table, order, servedTurn
   • getSatisfactionLoss() → abstract method
   • loseSatisfaction() → satisfaction minus getSatisfactionLoss(),
     never below 0
   • isWaitingForService() → true only if status is WAITING, SEATED or ORDERED
   • chooseItem(Menu menu) → the food choice rule we agreed in the call
     (e.g. customer 1 = Burger, 2 = Pasta, 3 = Salad, then repeat)
   • calculateBill(int priceCents) → for Stage 1, just returns the full price
   • leave() → status becomes LEFT; if they have an order, call order.cancel();
     if they have a table, call table.release()
   • getters/setters for what other people need

3. RegularCustomer extends Customer:
   • getSatisfactionLoss() returns 8

📏 RULES TO GET RIGHT
• Only customers who are WAITING, SEATED or ORDERED lose satisfaction at turn end
• Once served (EATING, READY_TO_PAY) they lose nothing
• At 0, they leave (call leave())

✅ DONE WHEN
Brief section 3.3 example: a customer who arrives turn 1 and orders Pasta
has satisfaction 92 → 84 → 76 → 76 at the end of turns 1–4.

📝 NON-CODE JOB (once the full game works)
Find 1 winning and 1 losing set of inputs.
Save each as a text file with one input per line:
• test-runs/stage1_victory.txt
• test-runs/stage1_defeat.txt  (easiest: choose "Wait" every turn)
Check they replay exactly:
java -cp out Main < test-runs/stage1_victory.txt

```

### Message 3: Person 3's task

```
👤 PERSON 3 – MENU, ORDERS, TABLES, CHEF (Stage 1)

⏳ WAIT for my message saying "skeleton is merged" before coding.
Then run: git checkout main → git pull → git checkout -b feature/order-menu

📁 YOUR FILES (only edit these):
• src/entity/MenuItem.java
• src/entity/Menu.java
• src/entity/Order.java
• src/entity/OrderStatus.java   (enum)
• src/entity/Table.java
• src/entity/Chef.java

🧠 WHAT TO CODE

1. MenuItem: private name, priceCents, prepUnits + getters
   (money is ALWAYS int cents: $15.00 = 1500)

2. Menu: an ArrayList of exactly 3 items:
   Salad  900 cents, 1 prep unit
   Burger 1500 cents, 1 prep unit
   Pasta  1800 cents, 2 prep units
   + getItem(int index), getItems()

3. OrderStatus enum: PREPARING, READY, SERVED, PAID, CANCELLED

4. Order:
   • Fields (private): customer, item, unitsDone (starts 0),
     priceCents (saved when the order is created), status (starts PREPARING)
   • advanceOneUnit() → unitsDone + 1; when unitsDone equals the item's
     prepUnits, status becomes READY
   • isReady(), markServed(), markPaid(), cancel()
   • getRemainingUnits() → prepUnits minus unitsDone

5. Table:
   • Fields (private): tableId, customer (null when empty)
   • isFree(), assign(Customer c), release()

6. Chef extends Staff:
   • prepare(Order o) → returns an ActionResult
     - if the order is not PREPARING → fail with a clear message,
       e.g. "Order #2 is already READY"
     - otherwise call o.advanceOneUnit() → success

✅ DONE WHEN
• Pasta needs 2 Chef turns to become READY; Burger and Salad need 1
• Cooking an order that's READY, SERVED or CANCELLED is rejected
  with a message (not a crash)

📝 NON-CODE JOB (after everything is merged)
Draw the Stage 1 class diagram in draw.io (app.diagrams.net)
FROM THE FINAL CODE. Every class, field and method name must match exactly.
Show: classes, private (-) / public (+), inheritance arrows,
associations with numbers (e.g. Restaurant has 2 Tables), the 2 enums.
Save BOTH into docs/:
• docs/stage1_class_diagram.drawio
• docs/stage1_class_diagram.png

```

## How this fits together

- **Everyone edits only their own files.** That's the main thing that prevents merge conflicts, which are the scariest part of Git for beginners.
- **Person 2's `leave()` calls Person 3's `order.cancel()` and `table.release()`.** That works because both method names are fixed in the skeleton, so Person 2 can write the calls even before Person 3 has finished.
- **You're the only person allowed to merge into `main`,** after one teammate approves. If a teammate hits an error, they screenshot it to you instead of trying random commands. Most beginner Git disasters come from `--force` or deleting folders.

## Your next steps in order
1. Send **Message 1** now, so everyone does the setup
2. Do the **design call**, and confirm who's Person 2–5 and the food choice rule
3. Push the skeleton, merge it, and post **"skeleton is merged ✅"** in the group chat
4. Create the GitHub issues (one per person, milestone `Stage 1`, label `stage-1`) and send Messages 2 and 3, with Persons 4 and 5's equivalents

I can write the matching messages for Person 4 and Person 5, and then the skeleton files, so the method names in these messages match the code exactly.

---

## Exchange 12

### Prompt (Divyansh)

[attached screenshot]

Quick question, so we will always have ONE pdf which will contain all this, and these will keep changing/being updated with each stage?

### Reply (Claude)

Yes, there's **one PDF report**, but you only **submit it once**, at the very end inside the zip. You don't hand in a new PDF at each stage.

## How it works over time
- **The PDF describes the final version (Stage 3).** The brief says the class diagram must *"match the Stage 3 code"*, and the sequence diagram must include *"one Stage 2 or 3 behaviour"*. So neither can be finished until Stage 3 is done.
- **Each stage is captured in GitHub, not in a new PDF.** The `stage-1`, `stage-2` and `stage-3` tags, plus the README sections for 1 → 2 and 2 → 3, are the record of each stage.
- **Your working files do get updated each stage.** The `.puml` class diagram and the design notes keep changing as the code changes. At each tag you **save a snapshot** of them. The final PDF is built from the last version, plus those snapshots.

## Keep the stage snapshots for the report
The slides (p. 508) say: *"A final Stage 3 UML diagram shows the destination; stage comparisons show how the design evolved."* The **"Thinking and development process"** criterion (20 marks) covers stage snapshots and change analysis. So at each tag, commit a copy into `docs/`:
- `stage-1` → `docs/stage1_class_diagram.png` (plus the `.puml`)
- `stage-2` → `docs/stage2_class_diagram.png`
- `stage-3` → `docs/stage3_class_diagram.png`, the final one that goes in the PDF

The earlier snapshots then go in the PDF's design evolution section to show what changed and why.

## What the one PDF will contain
1. **Class diagram:** final Stage 3 version, with notes pointing out examples of encapsulation, polymorphism and composition
2. **Sequence diagram:** one full scenario, including a VIP or Happy Hour behaviour
3. **Design discussion:** responsibilities table, SOLID principles only where your code really shows them, trade-offs, one alternative design, how player choices change the result
4. **Design evolution:** Stage 1 → 2 → 3 snapshots, what changed and why (this matches the README)
5. **Functional evidence:** test notes, victory and defeat runs, screenshots
6. **Reflections**

The GitHub evidence lives mainly in the **repo and README**, and the PDF just references it with the repo link and compare links.

## What to do right now for Stage 1
Commit the Stage 1 class diagram to `docs/` and keep it matching the code until you tag `stage-1`. Also keep a running design notes file (`docs/design_log.md`): what you decided and why. Writing the design discussion later is much easier when you can copy from notes made at the time.

---

## Exchange 13

### Prompt (Divyansh)

so i cant have 3 pdf's one for each stage?

### Reply (Claude)

The brief asks for **one** report. In Section 6.1 the zip contents list *"PDF report"* (singular), and the marking is based on that report. Three separate report PDFs could confuse the grader, or they might only read one of them.

But you can get the same benefit inside the rules:

**Option 1: one PDF with a section per stage (recommended).** Same idea as three PDFs, in one file:
```
1. Final design (Stage 3): class diagram, sequence diagram, design discussion
2. Design evolution
   2.1 Stage 1: class diagram snapshot + key decisions
   2.2 Stage 1 → 2: what changed and why + Stage 2 snapshot
   2.3 Stage 2 → 3: what changed and why + Stage 3 snapshot
3. Functional evidence (test notes, victory/defeat runs per stage)
4. Reflections
```
You can even **write it stage by stage**:
- After `stage-1`, write section 2.1
- After `stage-2`, write section 2.2
- After `stage-3`, write 2.3 and section 1

Then export it all as one PDF.

**Option 2: one report PDF plus per-stage files in the repo.** Keep `docs/stage1_class_diagram.png` (or `.pdf`), `docs/stage2_...` and `docs/stage3_...` in GitHub as supporting evidence. The single report links to them. These are extra evidence, not three reports.

**Avoid:** submitting three report PDFs in the zip instead of one.

If you really want three, ask your TA at Lab 5 or by email first. Without their OK, stick to one PDF with stage sections.

---

## Exchange 14

### Prompt (Divyansh)

so for now for stage 1, do we start documenting anyhting in the pdf? also what does 'tag' mean in git

### Reply (Claude)

## 1. Should you start documenting for Stage 1?

**Yes, start now.** Write it in a **working document** rather than the final PDF. Use a shared Google Doc or Word file that all 5 of you can edit, and only export it to PDF at the end.

### What to write now (Stage 1 only)
Make a heading **"2.1 Stage 1: Basic Restaurant Operation"** and fill these in as the week goes:

| What | When | Who |
|---|---|---|
| **Class diagram snapshot:** paste the Stage 1 PNG | After the design call, then update before tagging | Person 3 |
| **Key design decisions and why:** e.g. why there are boundary, control and entity classes, why money is in cents, why `Staff` is abstract, why invalid choices throw an `Exception`, the food choice rule, arrivals happening before the start-of-turn screen | During the week, as decisions are made | Person 5 (design log) |
| **Responsibilities table (Stage 1 version):** one row per class with what it's responsible for | Once the skeleton is agreed | You |

**Don't write anything about Stage 2 or 3 in this section yet**, not even "this will make VIP easier later". Write it as if Stage 1 is the whole product. The slides frame each stage as a release where the later requirements haven't arrived yet. When you reach Stage 2, *that* section explains how the design coped with the new requirements.

### Why bother now?
Later nobody will remember why you made each choice. Notes written at the time make the "design discussion" and "development process" sections (35 marks combined) much easier to write, and they'll be more honest too.

## 2. What is a "tag" in Git?

A **tag is a permanent label stuck on one specific commit**, like a bookmark saying *"this exact version is Stage 1"*.

### Analogy
Your commit history is a long line of save points:
```
commit A ── commit B ── commit C ── commit D ── commit E ──...
"setup"     "skeleton"  "add Order" "fix bug"   "Stage 1 done"
                                                    ▲
                                                 stage-1   ← the tag
```
Later you keep working on Stage 2 and add more commits, but the `stage-1` tag **stays stuck on commit E forever**. So anyone, including your grader, can jump back to exactly that version and run it.

### Tag vs branch
| | Branch | Tag |
|---|---|---|
| Moves? | Yes, moves forward every time you commit | **No**, always points to the same commit |
| Used for | Ongoing work (`feature/customer`) | Marking a finished version (`stage-1`) |

### Why the brief cares
- The grader checks out `stage-1`, `stage-2` and `stage-3` and runs each one. **Each must compile and run on its own.**
- GitHub can show **exactly what changed between two tags**, which is the "compare link" the README needs:
  `https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2`

### How to make one
In the VS Code terminal:
```bash
git checkout main          # go to the main branch
git pull                   # make sure you have everyone's latest merged work
git tag -a stage-1 -m "Stage 1: basic restaurant operation"
git push origin stage-1    # upload the tag to GitHub (normal push does NOT send tags)
```
- `-a` makes an "annotated" tag, which records who made it, when, and a message. That's better evidence than a plain tag.
- After pushing, go to the repo page on GitHub → click **Tags** (next to the branch dropdown, or under **Releases** on the right) → you'll see `stage-1`.

### Checking a tag works (what the grader does)
```bash
git checkout stage-1       # jump to that exact version
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
java -cp out Main
git checkout main          # jump back to the latest version
```
(Use `RestaurantApp` instead of `Main` if that's what the main class ends up being called.) Git may say *"You are in 'detached HEAD' state"* when you check out a tag. That's normal: you're viewing an old version. `git checkout main` takes you back.

### If you tag the wrong commit
Only fix it **before** anyone relies on it, and tell the group first:
```bash
git tag -d stage-1                 # delete it locally
git push origin --delete stage-1   # delete it on GitHub
# then create it again on the correct commit
```

Only you (Person 1) create tags, and only after the whole group has checked that Stage 1 runs and contains no Stage 2/3 code.

---

## Exchange 15

### Prompt (Divyansh)

Okay so for stage 1 summarise again what exactly we have to do and what exactly we DONT

### Reply (Claude)

Here's the full Stage 1 checklist, based on the brief (Sections 2–4) and the Restaurant Rush slides (pp. 501–503).

# ✅ What Stage 1 MUST have

## Game setup (fixed base setting)
- **18 turns**. The game always runs all 18; it never ends early.
- **2 tables**
- **Staff:** 1 Waiter (1 task per turn), 1 Chef (1 prep unit per turn), 1 Cashier (1 payment per turn)
- **Arrivals:** one customer at the start of turns **1, 4, 7, 10, 13, 16** (6 in total), each starting at satisfaction **100**
- **Menu:** Salad $9.00 / 1 unit, Burger $15.00 / 1 unit, Pasta $18.00 / 2 units
- **Regular customers only**, losing **8** satisfaction per waiting turn (never below 0)
- **Victory** after turn 18 if: **≥4 paid customers AND revenue ≥ $45.00 AND average satisfaction of paid customers ≥ 60**. Otherwise **Defeat**.

## General rules
- **Customers choose their own food** with a documented, deterministic rule, and the choice is shown when the order is placed. The manager never chooses food.
- **Game rules stay out of the UI.** `RestaurantUI` only displays things and collects choices. Rules live in the controller and entity classes.
- **Works offline**, with no network access needed.

## Start of every turn, show:
Turn number, revenue, average satisfaction of paid customers (**N/A** if nobody has paid), the waiting queue, occupied tables, each customer's satisfaction, order status, and the staff tasks available.

## The 6 steps of every turn, in this order
1. **Arrivals:** the scheduled customer joins the queue at 100
2. **Manager choices:**
   - **Waiter:** one of seat a queued customer at a free table / take a seated customer's order / serve a READY order / Wait
   - **Chef:** advance one order by 1 prep unit, or Wait. The Chef *can* cook an order the Waiter took this turn.
3. **Kitchen and serving:**
   - The Waiter acts **before** the Chef.
   - An order becomes READY when its last unit is done.
   - A dish that became READY **this turn** can only be served **next turn**.
   - Serving sets the order to SERVED.
4. **Eating and payment:**
   - A customer served on an earlier turn finishes eating and becomes READY_TO_PAY.
   - The Cashier automatically handles **1** READY_TO_PAY customer per turn, **oldest first**. Someone who became READY_TO_PAY this turn pays **next turn** at the earliest.
   - Payment adds the bill to revenue, marks the customer and order PAID, records their satisfaction, and frees the table.
5. **Waiting and abandonment:**
   - Every waiting or seated customer who **hasn't been served** loses 8.
   - At 0 they **leave without paying**: cancel any unfinished order and free their table.
   - Customers who have been served, are eating or are ready to pay lose nothing.
6. **Result:** after turn 18, Victory or Defeat

## Rules that always apply
- The Waiter does at most 1 task per turn.
- A table stays occupied until its customer pays or leaves.
- No eating before being served. No charging before READY_TO_PAY.
- **An invalid choice must explain why it failed and let the manager choose again, without using up that staff member's task.**
- Revenue never includes unpaid or abandoned orders.
- The turn-16 customer can't finish paying. That's intended, not a bug.

## End of each turn, show:
Served count, unhappy departures, revenue, average satisfaction of paid customers, turns used.

## Check it against the brief's Pasta example (Section 3.3)
Customer arrives turn 1 → satisfaction **92, 84, 76, 76**. Served turn 4 → READY_TO_PAY turn 5 → **pays $18.00 on turn 6**.

## Deliverables at the `stage-1` tag
- [ ] Code compiles and runs **on its own** from the `stage-1` tag
- [ ] **One reproducible victory run** and **one reproducible defeat run** (input files in `test-runs/` + test notes)
- [ ] Stage 1 class diagram snapshot in `docs/`, matching the code
- [ ] README with build/run instructions
- [ ] Every class and method commented (purpose + author)
- [ ] Stage 1 notes in the working report doc (decisions, responsibilities, tests, reflection)
- [ ] Annotated tag `stage-1` pushed to GitHub

# ❌ What Stage 1 must NOT have

## No Stage 2 or 3 features, anywhere
- ❌ **No VIP or Critic customers**: no classes, no enum values like `VIP`, no discount code
- ❌ **No Host**
- ❌ **No ComboMeal**
- ❌ **No Happy Hour**: no on/off flag, no 20% discount, no +15 satisfaction

## No placeholders or hints about later stages
The slides say this directly (p. 501): *"No placeholders for these later features."*
- ❌ No empty classes "for later" (p. 503 marks this as the **wrong** answer)
- ❌ No comments like `// TODO: VIP discount here` or `// for Stage 2`
- ❌ No unused parameters or fields that only make sense later (e.g. a `discountRate` field, a "locked price" field, a `customerType` field)
- ❌ No `RegularCustomer` subclass. Stage 1 only has one kind of customer, so `Customer` is a normal class.
- ❌ No mention of later stages in the README, commits, issues, milestones, labels or the Stage 1 report section

## Other "don'ts"
- ❌ No game rules inside `RestaurantUI` (e.g. the UI must not decide whether a dish can be served)
- ❌ No `double` for money (use `int` cents)
- ❌ No early ending before turn 18
- ❌ No crashing on bad input (letters, blanks, out-of-range numbers)
- ❌ No manager choosing the food
- ❌ No pushing straight to `main`, and no merging anything else into `main` until `stage-1` is tagged
- ❌ No code outside what the course has taught: no ternary `?:`, no lambdas or streams. Stick to classes, inheritance, abstract classes, `ArrayList`, `Scanner` and try/catch.

The rule to remember from the slides: **"Design Stage 1 well on its own terms."** Build it as if Stage 1 were the whole game, and only think about Stage 2 once `stage-1` is tagged.

---

## Exchange 16

### Prompt (Divyansh)

okay for my doc give me all headings, i will prepare the doc now

### Reply (Claude)

Here's the full heading structure for the report. It follows the brief's Section 5 (design evidence), Section 6.1 (what the PDF must contain) and Section 7 (marking criteria), so each section lines up with something that gets marked.

The tags show when to fill each section:
- 🟢 **Fill during Stage 1**
- ⚪ **Leave empty for now**, filled at a later stage or at the end

This doc stays in Google Docs/Word, **not in the GitHub repo**, so having the later headings in it doesn't break the Stage 1-only rule for the `stage-1` tag.

# Report structure

**Cover page** 🟢
- Title: *SC2002 Object-Oriented Design & Programming – Group Assignment: Restaurant Rush*
- Lab group: SCE1, Group 5
- Members: name, matric number, GitHub username
- Repository URL
- Submission date

**Table of Contents** ⚪ (generate automatically at the end)

---

## 1. Introduction 🟢
### 1.1 Project Overview
*(2–3 sentences: what the game is, CLI, turn-based, the player is the manager)*
### 1.2 Base Game Settings
*(turns, tables, staff, arrivals, menu, victory conditions)*
### 1.3 How to Build and Run
*(same commands as the README)*
### 1.4 Team Members and Responsibilities
*(table: member / classes owned / non-code role)*

---

## 2. Final Design (Stage 3) ⚪
### 2.1 UML Class Diagram ⚪
#### 2.1.1 Class Diagram
#### 2.1.2 Annotations: Encapsulation
#### 2.1.3 Annotations: Polymorphism
#### 2.1.4 Annotations: Composition
### 2.2 UML Sequence Diagram ⚪
#### 2.2.1 Scenario Description
*(which manager choice changes which outcome)*
#### 2.2.2 Sequence Diagram
#### 2.2.3 Walkthrough of Key Messages, Guards and Loops

---

## 3. Design Considerations
### 3.1 Key Abstractions and Responsibilities 🟢 *(start a Stage 1 version of the table now)*
*(table: class / stereotype (boundary/control/entity) / responsibility)*
### 3.2 Boundary–Control–Entity Structure 🟢
*(why RestaurantUI, RestaurantController and the entity classes are split this way)*
### 3.3 Design Principles Applied ⚪
#### 3.3.1 Single Responsibility Principle (SRP)
#### 3.3.2 Open-Closed Principle (OCP)
#### 3.3.3 Liskov Substitution Principle (LSP)
#### 3.3.4 Interface Segregation Principle (ISP)
#### 3.3.5 Dependency Inversion Principle (DIP)
*(only keep the ones your code really shows, and delete the rest at the end)*
### 3.4 Trade-offs ⚪
### 3.5 Alternative Design Considered ⚪
### 3.6 How the Player's Choices Change the Result ⚪

---

## 4. Design Evolution Across Stages
### 4.1 Stage 1: Basic Restaurant Operation 🟢
#### 4.1.1 Scope of Stage 1
*(what Stage 1 includes)*
#### 4.1.2 Stage 1 Class Diagram
#### 4.1.3 Key Design Decisions and Reasons
*(money in cents, abstract `Staff`, exceptions for invalid choices, controller owns the lists, food choice rule, arrivals before the display)*
#### 4.1.4 Stage 1 Git Evidence
*(`stage-1` tag link, list of files)*
### 4.2 Stage 1 → Stage 2 ⚪
#### 4.2.1 New Requirements
#### 4.2.2 New Files
#### 4.2.3 Existing Files Changed or Deleted, with Reasons
#### 4.2.4 Stage 2 Class Diagram
#### 4.2.5 Compare Link
### 4.3 Stage 2 → Stage 3 ⚪
#### 4.3.1 New Requirements
#### 4.3.2 New Files
#### 4.3.3 Existing Files Changed or Deleted, with Reasons
#### 4.3.4 Compare Link

---

## 5. Functional Evidence
### 5.1 Testing Approach 🟢
*(how you tested: replay input files, edge-case table)*
### 5.2 Stage 1 Test Results 🟢
#### 5.2.1 Reproducing the Brief's Example (Section 3.3)
#### 5.2.2 Edge-Case Tests
*(table: test / expected / actual / pass)*
#### 5.2.3 Reproducible Victory Run
*(input file name + screenshot of final screen)*
#### 5.2.4 Reproducible Defeat Run
### 5.3 Stage 2 Test Results ⚪
### 5.4 Stage 3 Test Results ⚪
### 5.5 Regression Testing ⚪
*(earlier behaviour still works after each stage)*

---

## 6. Reflections
### 6.1 Stage 1 Reflections 🟢
*(2–3 lines per member)*
### 6.2 Stage 2 Reflections ⚪
### 6.3 Stage 3 Reflections ⚪
### 6.4 Overall Lessons Learnt ⚪

---

## 7. References ⚪
*(course slides, any other sources)*

## Appendix ⚪
### A. Video Timestamps
### B. Declarations
*(signed Declaration of Original Work + each member's GAI declaration. These may be separate files in the zip, so check the course forms)*

# Formatting tips for setting it up now
- Use real **Heading 1 / Heading 2 / Heading 3** styles (not just bold text), so the Table of Contents builds itself and the PDF gets clickable bookmarks.
- Turn on **page numbers** in the footer.
- Add a short **"Owner:"** line under each 🟢 section, e.g. *4.1.3 Owner: Person 5*, so everyone knows what to fill.
- In section 4.1, write as if Stage 1 is the whole product: **no mention of later stages**.

Once your design call settles the classes, I can draft the first content for sections 3.1, 3.2 and 4.1.3 from the Stage 1 diagram.

---

## Exchange 17

### Prompt (Divyansh)

Okay thanks, now tell me exactly which heading and sub headings have to be filled for JUST stage 1

### Reply (Claude)

These are the only sections to fill during Stage 1. Leave everything else empty.

## Cover page
- Title, lab group (SCE1 Group 5), members (name, matric no., GitHub username), repository URL
- **Owner:** you

## 1. Introduction
| Sub-heading | What to write | Owner | When |
|---|---|---|---|
| **1.1 Project Overview** | 2–3 sentences: Java CLI turn-based restaurant game, the player is the manager and prioritises Waiter and Chef tasks | You | |
| **1.2 Base Game Settings** | 18 turns, 2 tables, 1 Waiter / 1 Chef / 1 Cashier, arrivals on turns 1, 4, 7, 10, 13, 16, menu with prices and prep units, the 3 victory conditions | You | |
| **1.4 Team Members and Responsibilities** | Table: member / classes owned / non-code role | You | After the design call |

## 3. Design Considerations (only these two sub-headings)
| Sub-heading | What to write | Owner | When |
|---|---|---|---|
| **3.1 Key Abstractions and Responsibilities** | Table with one row per Stage 1 class: `RestaurantApp`, `RestaurantUI`, `RestaurantController`, `Staff`, `Waiter`, `Chef`, `Cashier`, `Customer`, `Table`, `Order`, `Menu`, `MenuItem`, `CustomerStatus`, `OrderStatus`. Columns: class / stereotype / responsibility. | You | After the design call |

## 4. Design Evolution Across Stages (only 4.1)
| Sub-heading | What to write | Owner | When |
|---|---|---|---|
| **4.1.1 Scope of Stage 1** | Bullet list of what Stage 1 includes: Regular customers, 3 menu items, Waiter/Chef/Cashier, tables, orders, queue, full turn sequence, satisfaction, leaving, payment, victory/defeat | You | |

## 5. Functional Evidence (only 5.1 and 5.2)
| Sub-heading | What to write | Owner | When |
|---|---|---|---|

## 6. Reflections (only 6.1)
| Sub-heading | What to write | Owner | When |
|---|---|---|---|

## Summary
**Fill now (Stage 1):**
- Cover page
- 1.1, 1.2, 1.3, 1.4
- 3.1, 3.2
- 4.1.1, 4.1.2, 4.1.3, 4.1.4
- 5.1, 5.2.1, 5.2.2, 5.2.3, 5.2.4
- 6.1

**Leave empty:** Table of Contents, all of Section 2, 3.3–3.6, 4.2, 4.3, 5.3–5.5, 6.2–6.4, 7, and the Appendix.

**Reminder:** none of the Stage 1 sections should mention VIP, Critic, Host, ComboMeal, Happy Hour, or "later stages". Write them as if Stage 1 is the finished product.

---

## Exchange 18

### Prompt (Divyansh)

[attached file: stage1_class_diagram__1___1_.svg] [attached file: stage1_class_diagram.drawio] Can you open these files and show me

### Reply (Claude)

I'll render both files and show them to you.

> *[Tool used: Bash — Copy uploads and inspect both files]*

> *[Tool used: Bash — Render the SVG to PNG with headless Chromium]*

> *[Tool used: Bash — Show the render error output]*

> *[Tool used: Bash — Render SVG to PNG with cairosvg]*

> *[Tool used: Read — svg.png]*

That's the SVG. Now checking whether the `.drawio` file is the same diagram or a different version.

> *[Tool used: Bash — Compare drawio file with SVG's embedded diagram]*

> *[Tool used: Bash — Check whether static members are underlined]*

> *[Tool used: SendUserFile]*

Here's your diagram rendered as a PNG. The `.svg` and `.drawio` files are the **same diagram**: the SVG is a draw.io export with the `.drawio` file built into it, and I confirmed all 281 elements match.

**Quick check against the Stage 1 design:**
- It has the same 14 classes, attributes, methods and relationships as the Stage 1 design we agreed. Nothing from Stage 2 or 3 is in it.
- The static members (`MAX_TURNS`, `NUM_TABLES`, `ARRIVAL_TURNS`, `SATISFACTION_LOSS`, `main`) **are underlined** in the draw.io file, as the slides require. The preview I made here doesn't show underlines, but they're there when you open it in draw.io.
- The visibility symbols, `«boundary»/«control»/«entity»` labels, italic abstract `Staff`, `<<enumeration>>` boxes with dashed arrows, composition and aggregation diamonds, multiplicities, role names, association names and `{ordered}` constraints all follow the slides.

**One layout point:** the dashed dependency lines from `Waiter` cross a lot of other lines (to `Customer`, `Table`, `Order` and `Menu`), which makes the middle busy. Moving `Waiter` lower, nearer `Customer` and `Order`, would cut most of those crossings. It still reads fine as it is.

---

## Exchange 19

### Prompt (Divyansh)

Okay my teammate made this. is it good to go for stage 1 submission? Check everything and be sure.
Also read the group assignment instructions again, where are we to submit this? in the doc? the github?

### Reply (Claude)

I've checked the diagram against the brief, the slides' notation and marking scheme, and the Stage 1-only rule. Short answer: **the design is right, there are 2 small notation fixes, and there's one check you can't do until the code exists.**

# Part 1: Is the diagram good for Stage 1?

## ✅ What's correct
**Content (brief Sections 2–4, Stage 1 scope)**
- Every Stage 1 element is there: Regular customers, the 3 menu items, Waiter, Chef, Cashier, Table, Order, the waiting queue, the turn sequence, satisfaction, leaving, payment, and victory/defeat.
- **No Stage 2 or 3 content at all.** No VIP, Critic, Host, Combo or Happy Hour, no placeholders, and no hints in names, notes or comments. This follows slides pp. 501 and 503.
- The design supports every Stage 1 rule:
  - the payment queue gives oldest-first payment
  - `servedTurn` + `finishEating()` give "eat for one full turn"
  - `isWaitingForService()` makes only unserved customers lose satisfaction
  - `leave()` reaches the customer's order and table to cancel and free them
  - the arrival turns are a constant

**Notation (slides Ch. 6 and the p. 430 marking scheme)**
- Attributes are written `-name: type = initial value` and all are **private** (the marking scheme takes a mark off otherwise)
- Methods are written `+name(param: type): returnType`, with `+ - #` visibility
- Static members are **underlined** (I checked the file: all 5 are)
- Abstract `Staff` is in italics, which slide 168 accepts
- The `«boundary» / «control» / «entity»` labels and `xxxUI` / `xxxController` names follow slides 452 and 462
- `<<enumeration>>` boxes are linked with **dashed arrows**, as in the WTA answer on p. 430
- Composition ◆ is used where the controller *creates* the part, and aggregation ◇ for the queues and orders, which already exist and are just added to a list (slides 389–390)
- Two-way associations (Customer–Table, Customer–Order) are plain lines; the one-way Order → MenuItem has an arrow
- Every association has multiplicities, role names, verb association names with ▸ direction markers, and `{ordered}` constraints
- The constraint note on `Staff` and the menu note are attached with dashed lines

## ⚠️ Two small fixes before it goes into the repo
1. **The UI → Controller label mixes the association name and the role name.** It currently reads "sends choices to controller" as one label. Per slide 543, split it into:
   - the **association name** `sends choices to ▸` in the middle of the line
   - the **role name** `controller` at the **RestaurantController end**, next to its `1`

   *(This was my mistake in the PlantUML version, and your teammate copied it across.)*
2. **The dependency `Customer ⇢ MenuItem` is missing.** `chooseItem(menu: Menu): MenuItem` *returns* a `MenuItem`, and slide 354 says a return type is a dependency. Add a dashed arrow from `Customer` to `MenuItem`.

## ⚠️ One check that has to wait for the code
Slide 445 calls the class diagram the "blueprint", and the brief grades "**match to code**". A diagram can only be confirmed once the code exists. Person 3 should check it line by line against the merged code before you tag `stage-1`:
- every class, attribute and method name is spelt **exactly** as in the code
- every parameter and return type matches
- **if `RestaurantUI` ends up using `Customer`, `Table` or `Order` objects directly** (e.g. looping over `getWaitingQueue()` to print customers), add dashed dependency arrows from `RestaurantUI` to those classes, because slide 354 counts local variables and return types as dependencies
- anything added during coding (an extra helper method, say) gets added to the diagram, and anything removed gets taken off

## Optional
- Add `{abstract}` next to the `Staff` name as well as the italics. Italics can be hard to spot in a printed PDF, and slide 168 allows either.
- Move `Waiter` lower, closer to `Customer` and `Order`, to cut down the crossing dashed lines.

# Part 2: Where does this get submitted?


**There's no separate Stage 1 submission.** Nothing is uploaded anywhere. The Stage 1 diagram goes in **two places**:

| Where | What | When |
|---|---|---|

**The only actual submission is at the end:** one zip, uploaded through the **assignment link on NTULearn**, named after your lab group (probably `SCE1_Grp5.zip`, but check the exact format on NTULearn), containing:
1. Source code with build/run instructions
2. README (repo URL, stage comparisons, video timestamps)
3. **The one PDF report** (the doc exported, with the Stage 1 diagram in Section 4.1.2 and the final Stage 3 diagram in Section 2)
4. The video, or a download link
5. Declarations (Declaration of Original Work + each member's GAI declaration)

The graders also look at **the GitHub repo itself** (tags, commits, README), so make sure the TA can access it, since it's private. Ask your TA whether to add them as a collaborator.

Want me to make the two fixes in the `.drawio` file and send it back, so your teammate can commit it straight away?

---

## Exchange 20

### Prompt (Divyansh)

how do i pull the latest updated folder for my project using git onto my vs code

### Reply (Claude)

Here's how, step by step on your Mac in VS Code.

## 1. Open the project
VS Code → **File → Open Folder** → `Documents/SC2002-Restaurant-Rush-SCE1-Group5`, or pick it from **File → Open Recent**.

## 2. Open the terminal inside VS Code
**View → Terminal**, or press `` Ctrl + ` ``. Check you're in the right folder; the prompt should end in `SC2002-Restaurant-Rush-SCE1-Group5 %`.

## 3. Check you don't have unsaved work
```bash
git status
```
- If it says **"nothing to commit, working tree clean"**, go to step 4.
- If it lists **modified or new files** you made, either:
  - **keep them** by committing them on your own branch (`git checkout -b feature/controller`, then `git add.`, then `git commit -m "WIP controller"`), or
  - **set them aside for now** with `git stash`, and bring them back after pulling with `git stash pop`.

## 4. Switch to `main` and pull
```bash
git checkout main
git pull
```
`git pull` downloads everything teammates have **merged into `main`** on GitHub and updates your folder. You'll see a list of changed files, or "Already up to date."

**Check it worked:**
```bash
git log --oneline -5
```
This shows the 5 most recent commits, which should include your teammates' merged work. The new files also appear in the Explorer on the left.

## 5. Your teammates' unmerged work (open PRs)
`git pull` on `main` only gets **merged** work. To look at a teammate's branch before approving their PR:
```bash
git fetch                          # downloads info about all branches on GitHub
git branch -r                      # lists them, e.g. origin/feature/customer
git checkout feature/customer      # switches your folder to their branch
```
When you're done, go back with `git checkout main`.

## 6. Then go back to your own branch
If you were working on your own branch, bring the latest `main` into it so you're not working on old code:
```bash
git checkout feature/controller    # your branch name
git merge main
```

## The button way (no typing)
1. Bottom-left of VS Code, click the **branch name** (e.g. `main`) → pick `main` from the list
2. Click the **sync icon** (🔄 circling arrows) next to it, or open the **Source Control** tab (`Ctrl + Shift + G`) → **⋯** menu → **Pull**

## If you see an error
| Message | What it means | Fix |
|---|---|---|
| *"Your local changes would be overwritten"* | You have uncommitted edits | Step 3: commit or `git stash` first |
| *"CONFLICT … Merge conflict in …"* | You and a teammate edited the same lines | **Stop** and screenshot it to me. Don't guess. |
| *"Authentication failed"* | Login expired | Click **Allow** on the VS Code GitHub sign-in popup, then try again |
| *"There is no tracking information"* | The branch was never pushed | Use `git pull origin main` |

---

## Exchange 21

### Prompt (Divyansh)

Okay its time to fill up the document. I am person 1, i believe i must fill in 1.1, 1.2, 1.3, 1.4, 3.1, 4.1.1, 4.1.4 right? 
G
can you tell me what to fill in for each? remember to go through our current stage-1 (it is tagged on github already, and NOT stage 2 or 3), and make sure what we fill in is in accordance with our work
Github repo link: https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5.git

### Reply (Claude)

I'll pull the repo at the `stage-1` tag and read the actual code before drafting your sections.

> *[Tool used: claude-code-remote: add_repo]*

> *[Tool used: claude-code-remote: list_repos — SC2002]*

> *[Tool used: Bash — Try cloning the repo directly]*

Yes, those are yours. You also own the **Cover page**, so it's 1.1, 1.2, 1.3, 1.4, 3.1, 4.1.1, 4.1.4 and the cover page.

**But I can't see your code yet.** The repo is private, and this session's GitHub access doesn't cover it. Both the direct clone and the GitHub connection were refused. I don't want to write sections like 3.1 (one row per class) or 4.1.4 (files and PRs at the tag) from the plan we made two weeks ago, because the real code has almost certainly drifted: renamed methods, extra helpers, a different food rule. That's exactly what graders check for.

**Easiest fix (about 1 minute), a ZIP of the `stage-1` tag:**
1. Open your repo on github.com
2. Click the **branch dropdown** (it says `main`) → switch to the **Tags** tab → click **`stage-1`**
3. Green **Code** button → **Download ZIP**
4. Attach that ZIP here

That gives me the exact Stage 1 code, README, `docs/` and `test-runs/`, and nothing from Stage 2 or 3.

**For 4.1.4 (Git evidence), also send:**
- a screenshot of the repo's **Pull requests → Closed** page (filter to PRs merged before the tag), **or** just tell me how many PRs were merged into `main` before you tagged
- confirm the tag link works: `https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/stage-1`

**For the cover page and 1.4, also send:**
- all 5 members' **names, matric numbers and GitHub usernames**
- who ended up being Persons 2–5, and if anything changed from the plan, who actually wrote which classes

**Alternative:** give this session GitHub access to the repo. Reconnect GitHub under **claude.ai Settings → Connectors** and allow this repository. The ZIP is quicker, though, and keeps the repo private.

Once I have the ZIP, I'll read every file at the tag and give you ready-to-paste text for each of your 8 sections, matched to the actual code.

---

## Exchange 22

### Prompt (Divyansh)

[attached file: excerpt_from_previous_claude_message.txt] do i need to put these as well in the doc?

### Reply (Claude)

It's **not required**. The brief never asks for a file list for Stage 1 on its own. But a **short version is worth keeping**, because:

- The brief's change analysis (Section 5) asks, for each transition, for *"new files; existing files changed or deleted, with reasons"*. In section 4.2 you'll write "Stage 1 → Stage 2: added X, changed Y". The Stage 1 file list is the **starting point** that makes that comparison clear to the grader.
- It's also quick proof that Stage 1 contains no Stage 2/3 classes.

## Keep only the source code
The `docs/`, `tests/` and `test-runs/` rows add length without helping the change analysis, and file names like `person5-handoff.md` look like internal notes in a formal report. Use this smaller table instead:

**Source files at the `stage-1` tag**

| Package | Classes |
|---|---|
| (default) | `RestaurantApp` |
| `boundary` | `RestaurantUI` |
| `control` | `RestaurantController` |
| `entity` | `ActionResult`, `Cashier`, `Chef`, `Customer`, `CustomerStatus`, `Menu`, `MenuItem`, `Order`, `OrderStatus`, `Staff`, `Table`, `Waiter` |

Then one sentence under it:
> The recorded victory and defeat runs are in `test-runs/`, and the Stage 1 class diagram is in `docs/`.

So **4.1.4** ends up as: the tag link + the small details table (date, commits, PRs) + the paragraph + this source-files table + that one sentence.

---

## Exchange 23

### Prompt (Divyansh)

is this reflection thingy a requirement by the assignment?

### Reply (Claude)

**Yes, but the per-stage, per-person format is our choice, not the brief's.**

**What the brief says:**
- **Section 6.1:** the PDF report must contain *"Final diagrams, design discussion, functional evidence and **reflections**"*.
- **Section 7:** the 20-mark **"Thinking and development process"** criterion covers *"Stage snapshots; change analysis; report and **reflection**; recorded video"*.

So reflections are **required** and are **part of a marked criterion**.

**What it doesn't say:** how to structure them. Nothing in the brief asks for a reflection **per stage** or **per member**. That's how we chose to set up the report (6.1–6.4), because:
- writing a few lines after each tag is easier than remembering everything at the end
- per-stage reflections back up the "development process" story the slides ask for (p. 508: *"Show the evolution, not just the final code"*)
- per-member lines show everyone contributed

**If you want less work:** one combined group reflection at the end would technically meet the requirement. But since Stage 1 is fresh, writing yours now costs 10 minutes and gives you material for the final version either way. I'd keep it.

---
