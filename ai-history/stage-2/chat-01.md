# AI chat history – Stage 2 – review of Stage 2 code, report task allocation, Stage 3 work split

- **Member:** Tomar Divyansh (Divyansh050505)
- **AI tool:** Claude (Anthropic), single continuous conversation
- **Format:** every prompt and every reply, in the original order. Attached files are named, and the AI's tool actions (reading files, running code) are shown as one-line notes.

---

## Exchange 1

### Prompt (Divyansh)

Alright, we're done with stage 2 and tagged it as well, we've also filled up the doc for stage 1. Please check if all are okay
I got two tasks for u
now tell us which headings to fill for stage 2, and which person does what.
and split the work for stage 3, across person 1 to 5 (follow the same person numbers)

### Reply (Claude)

I'll look for your report doc in Google Drive first, so I can check it.

> *[Tool used: ToolSearch — select:mcp__Google_Drive__search_files,mcp__Google_Drive__read_file_content,mcp__Google_Drive__get_file_metadata]*

> *[Tool used: Google_Drive: search_files — (fullText contains 'Restaurant Rush' or title contains 'SC2002') and mimeType = 'application/vnd.google-apps.document']*

> *[Tool used: Google_Drive: read_file_content]*

I read the whole report doc. I **can't check the Stage 2 code yet**, because the repo is private and I only have the Stage 1 ZIP. Send me the **`stage-2` tag ZIP** (same steps as before: Tags → `stage-2` → Code → Download ZIP) and I'll check it fully, including that it has no Stage 3 code.

Below: (A) the Stage 1 doc check, (B) Stage 2 sections and owners, (C) the Stage 3 split.

Persons: **P1 Divyansh · P2 Lei Yueer · P3 Shi Hong · P4 Jan Guirey · P5 Liu Yanxi**

# A. Stage 1 doc check: not finished yet

**✅ Done and correct:** 1.1, 1.2, 1.3, 1.4, 3.1, 4.1.1, 4.1.4, and the reflections from Divyansh and Jan.

**❌ Still empty (Stage 1 sections that need filling):**
| Section | Owner | Note |
|---|---|---|
| 3.2 BCE Structure | P5 | A ready draft already exists in the repo: `docs/person5-report-draft.md` |
| 4.1.2 Stage 1 Class Diagram | P3 | Paste the PNG from `docs/stage1_class_diagram.drawio` |
| 4.1.3 Key Design Decisions | P5 | Also already drafted in `person5-report-draft.md` |
| 5.1 Testing Approach | P4 | |
| 5.2.1 Brief's example (92 → 84 → 76 → 76, $18 paid on turn 6) | P4 | |
| 5.2.3 Victory Run / 5.2.4 Defeat Run | P2 | The content is in `test-runs/README.md`: strategy, choices table and final screens |
| 6.1 Reflections from **Lei Yueer** and **Liu Yanxi** | P2, P5 | |

**⚠️ Fixes needed:**
1. **Cover page:**
   - **Liu Yanxi is missing** from the members table (the 5th row is blank)
   - **Repository URL** is blank
   - "Shi Hong" (in 1.4) and "Eng Shi Hong" (cover): use the same name in both places
2. **Literal `**` asterisks** seem to be typed into the cover table and every row of the 3.1 table (e.g. `**RestaurantApp**`). If you can see them in the doc, delete them and use real bold (Ctrl/Cmd + B), or they'll show up in the PDF.
3. **Two headings are numbered "5.2.3".** The Defeat Run should be **5.2.4**.
4. **5.2.2 Edge-case tests:** it's prose only. Turn it into a **Test / Expected / Actual / Pass** table, and add the missing cases:
   - Chef cooking an already-READY order
   - typing letters or out-of-range numbers
   - a dish finished this turn can't be served this turn
   - a customer who becomes READY_TO_PAY this turn pays next turn
   - two READY_TO_PAY customers: the oldest pays first
   - the average shows N/A before the first payment
5. **Shi Hong's reflection** calls `RestaurantUI` *"a control class"*. It's the **boundary** class; `RestaurantController` is the control class. Graders will notice this, since BCE is lecture content.
6. **4.1.4** (optional): add the small "Source files at the stage-1 tag" table we discussed.

# B. Stage 2: which sections to fill, and who

**Rule:** Stage 2 sections describe Stage 2 only. No Host, Combo or Happy Hour.

| Section | What to write | Owner |
|---|---|---|
| **1.4** Team (add a **Stage 2** table) | Who wrote or changed which classes in Stage 2 | P1 |
| **3.1** Key Abstractions (add **"Stage 2 additions"** rows) | One row per new class (e.g. `VIPCustomer`, `CriticCustomer`) and any class whose responsibility changed | P1 |
| **4.2.1** New Requirements | VIP: loses 5 per turn, 10% off. Critic: loses 12 per turn, **plus your documented special behaviour (state exactly what it is)**. At least one VIP and one Critic in the 6 arrivals. Behaviour handled by the customer objects, not by type checks in the controller. | P2 |
| **4.2.2** New Files | List of new `.java` files and what each does | P1 |
| **4.2.3** Existing Files Changed or Deleted, with Reasons | For **each** changed file: what changed and **why that responsibility needed it** (slide 505: "Which existing class changed, and which responsibility made that change necessary?"). Explain any big rewrite and show Stage 1 behaviour still works. **This is the most heavily judged Stage 2 section.** | P5 (with input from whoever wrote each change) |
| **4.2.4** Stage 2 Class Diagram | Updated diagram matching the `stage-2` code. Commit it to `docs/stage2_class_diagram.drawio` too. | P3 |
| **4.2.5** Compare Link | `https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2` | P1 |
| **5.3** Stage 2 Testing Results | **(a)** Reproducible **victory and defeat runs that include a VIP and a Critic** (input files plus final screens) | P2 |
| | **(b)** Stage 2 edge-case table: VIP loses 5, Critic loses 12, VIP bill is 10% off (e.g. Burger $13.50), the Critic behaviour works, Regular still loses 8 | P4 |
| **5.5** Regression Testing (Stage 1 → 2 part) | Re-run the Stage 1 checks on the Stage 2 code: Regular customers, timing rules, invalid input. Record pass/fail. | P4 |
| **6.2** Stage 2 Reflections | 1 paragraph each | Everyone |

**Not in the doc, but required (P1):** add the **README "Stage 1 → Stage 2" section** (new files, changed files with reasons, compare link). The brief asks for this in the README itself.

# C. Stage 3 work split

**First, a 30-minute design call (P1 runs it)** to agree before coding:
- **Where Happy Hour lives:** the controller tracks whether it's on and how many turns are left; the discount is applied when the order is created.
- **Combo choice rule:** how a customer picks a combo, which must be documented.
- **Price locking:** right now `Cashier` charges `order.getItem().getPriceCents()`. Stage 3 needs the price **locked in the `Order` when it's taken** (VIP discount first, then Happy Hour), so `Order` and `Cashier` must change.

### Code
| Person | Classes / work | Must achieve |
|---|---|---|
| **P1 Divyansh** | `RestaurantController`: add the **Host step** and the **Happy Hour state** (activate once, at the start of a turn, lasts that turn and the next, remaining turns tracked; +15 satisfaction for waiting customers at turn start, max 100). Merge everything, **tag `stage-3`**. | Turn order still correct, all Stage 1 and 2 rules unchanged |
| **P2 Lei Yueer** | `Customer` (+ VIP/Critic if needed): `chooseItem()` can choose a **combo** using the documented rule; a method to **recover satisfaction** (+15, capped at 100, never for customers who have left); bill calculation applies **VIP 10% first, then Happy Hour 20%** | VIP Burger in Happy Hour = **$10.80** |
| **P3 Shi Hong** | New **`ComboMeal`** (made of two `MenuItem`s, the composition example); `Menu` offers combos; **`Order` stores a locked `priceCents`** set when the order is taken | Salad + Pasta = **$24.30, 3 units** (rounded to the nearest cent) |
| **P4 Jan Guirey** | New **`Host`** (extends `Staff`): seats at most one queued customer per turn, separately from the Waiter, who can still seat. **`Cashier`** charges the order's locked price. | Host plus Waiter can seat 2 customers in one turn |
| **P5 Liu Yanxi** | `RestaurantUI`: **Host menu**; **"Activate Happy Hour?"** prompt at the start of a turn (only if not used yet); show **Happy Hour turns remaining**; show combos and locked prices on orders | Invalid choices still re-prompt without using up a task |

### Stage 3 report sections (same pattern as Stage 2)
| Section | Owner |
|---|---|
| 1.4 / 3.1 Stage 3 additions, 4.3.2 New Files, 4.3.4 Compare Link, README "Stage 2 → 3" | P1 |
| 4.3.1 New Requirements (Host, ComboMeal, Happy Hour, combo rule) + 5.4 Stage 3 **victory/defeat runs** | P2 |
| **2.1 Final class diagram** (must match the Stage 3 code) + annotations for encapsulation, polymorphism and composition | P3 |
| 5.4 Stage 3 edge-case table + 5.5 full regression (Stages 1 and 2 still work) | P4 |
| 4.3.3 Existing Files Changed, with Reasons + **2.2 Sequence diagram** (one full scenario with VIP pricing or Happy Hour, done together with P1) | P5 |
| 6.3 Reflections | Everyone |

### After `stage-3` is tagged
- 3.3–3.6: SOLID, trade-offs, alternative design, player choices. Split between P1 and P5.
- Video: everyone records their own segment.
- Declarations: everyone.
- Final README, PDF export and zip: P1.

| Date | Milestone |
|---|---|

Send the `stage-2` ZIP and I'll check the code, then draft your Stage 2 sections (1.4, 3.1, 4.2.2, 4.2.5 and the README section) from it.

---

## Exchange 2

### Prompt (Divyansh)

[attached file: SC2002-Restaurant-Rush-SCE1-Group5-stage-1_1.zip]

### Reply (Claude)

> *[Tool used: Bash — Unzip new archive and compare with stage-1 code]*

That ZIP is the **`stage-1` code again**. It's byte-for-byte identical to the one you sent earlier: no `VIPCustomer`, no `CriticCustomer`, and the folder is even named `...-stage-1`. So I still can't check Stage 2.

That means one of three things happened. Here's how to tell which:

**1. Check the `stage-2` tag exists on GitHub**
Repo page → branch dropdown (`main`) → **Tags** tab.
- **If `stage-2` is in the list**, you just downloaded the wrong one. Click **`stage-2`**, then green **Code** → **Download ZIP**. The file should be named `SC2002-Restaurant-Rush-SCE1-Group5-stage-2.zip`.
- **If only `stage-1` is there**, the tag was created on someone's laptop but **never pushed**. A normal `git push` doesn't send tags. Whoever made it needs to run:
  ```bash
  git push origin stage-2
  ```

**2. If `stage-2` exists but its ZIP has no VIP/Critic code**
The tag was put on the wrong commit, probably before the Stage 2 pull requests were merged. Check `main` has the Stage 2 code (look for `VIPCustomer.java` in `src/entity/` on GitHub). If it does, fix the tag. Tell the group first, then:
```bash
git checkout main
git pull
git tag -d stage-2
git push origin --delete stage-2
git tag -a stage-2 -m "Stage 2: VIP and Critic customers"
git push origin stage-2
```

**3. If `main` doesn't have VIP/Critic code at all**
The Stage 2 work is still sitting on feature branches. It needs to be merged through PRs first, and only then tagged.

Send me the ZIP named `...-stage-2.zip` once you have it, and I'll check it against the brief and draft your Stage 2 sections.

---

## Exchange 3

### Prompt (Divyansh)

[attached file: SC2002-Restaurant-Rush-SCE1-Group5-stage-2.zip]

### Reply (Claude)

> *[Tool used: Bash — Unzip stage-2 and diff against stage-1]*

This is the real Stage 2, and there's no Host, Combo or Happy Hour code in it. Reading the new files and every change.

> *[Tool used: Bash — Read new Stage 2 classes]*

> *[Tool used: Bash — Diff every changed Stage 1 file]*

> *[Tool used: Bash — See Customer hooks and other file diffs]*

Compiling it and running the Stage 2 replays and the team's own tests.

> *[Tool used: Bash — Compile stage-2, replay runs, run test suites]*

> *[Tool used: Bash — Check type-check rule, diagram coverage, README sections]*

> *[Tool used: Bash — Read README team table and Stage 2 rules]*

Stage 2 is in good shape. I compiled the `stage-2` tag, replayed both runs and ran all 5 of your team's test suites. Everything passes.

# Stage 2 code check

**✅ Meets the brief (Section 4.2) and the slides (pp. 504–505):**
| Requirement | What your code does |
|---|---|
| VIP loses 5 per turn, 10% off | `VIPCustomer`: `getSatisfactionLoss()` returns 5, `getDiscountPercent()` returns 10. VIP Pasta paid **$16.20** in the replay. |
| Critic loses 12, with a distinct, documented behaviour | `CriticCustomer`: loses 12 and **always orders Pasta** (written up in the README) |
| At least 1 VIP and 1 Critic, still 6 arrivals | `CustomerArrivalPlan`: exactly 4 Regular + 1 VIP + 1 Critic, with seeded positions |
| Same order and service flow for all types | All service rules stay in `Customer`. Subclasses only override small methods. |
| **No type checks in the controller** | I searched: **zero** `instanceof` or type-label checks. All differences go through overridden methods, which is real polymorphism. |
| Stage 2 playable on its own, same win/lose rules | VICTORY: 4 paid, $58.20, average 74.00. DEFEAT: 0 paid, 2 departures. |
| No Stage 3 code | No Host, Combo or Happy Hour anywhere |
| Changes kept small (slide 505) | Controller: one new field + one line changed in arrivals. Cashier: one line. UI: display only. |
| Evidence | `docs/stage2_class_diagram` added (Stage 1's kept unchanged). The README already has the **Stage 1 → 2 changes table and compare link**. |

Tests: **480 + 195 + 3824 + 64 + 334 checks, all pass.**

**⚠️ Small things:**
1. The **README team table still says "(name)"** for 4 members. Fix it on `main`.
2. `CustomerArrivalPlan` uses `BiFunction` and `VIPCustomer::new` (method references). The Modern Java lecture covers lambdas, so it's defensible, but whoever wrote it should be ready to explain it in the video.

# Stage 1 doc: still to fix
Everything from my last message still stands. **Empty sections:** 3.2, 4.1.2, 4.1.3, 5.1, 5.2.1, 5.2.3/5.2.4, plus reflections from Lei Yueer and Liu Yanxi. **Fixes:** Liu Yanxi missing from the cover, blank repo URL, literal `**` asterisks, the duplicate "5.2.3", the 5.2.2 table, and "RestaurantUI is a control class" in Shi Hong's reflection.

# Task 1: Stage 2 sections and owners (updated from your actual code)
| Section | What to write | Owner |
|---|---|---|
| **1.4** Stage 2 table | Who wrote `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `CustomerArrivalPlan` and the changes to `Customer`, `Cashier`, `Controller`, `UI` (check the commit history) | P1 |
| **3.1** "Stage 2 additions" rows | `Customer` (now **abstract**), `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `CustomerArrivalPlan` (Control) | P1 |
| **4.2.1** New Requirements | Copy the README's **"Stage 2 customer rules"** table (loss, bill, food choice) plus: the 4/1/1 seeded arrival plan, half-cent rounding up, VIP bills $8.10 / $13.50 / $16.20 | P2 |
| **4.2.2** New Files | `RegularCustomer.java`, `VIPCustomer.java`, `CriticCustomer.java`, `CustomerArrivalPlan.java`, the 3 new test files, `test-runs/stage2_*`, `docs/stage2_class_diagram.*` | P1 |
| **4.2.3** Changed Files, with Reasons | Start from the README table, but **expand each reason** using the question slide 505 asks: *"which responsibility made the change necessary?"* The key point: `Customer` went from concrete to **abstract**, because Stage 1 had one kind of customer and Stage 2 needed different behaviour per type. That's a small, explained change, which is what the slides want. | P5 |
| **4.2.4** Stage 2 Class Diagram | Paste the PNG of `docs/stage2_class_diagram` | P3 |
| **4.2.5** Compare Link | `https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2` | P1 |
| **5.3(a)** Stage 2 victory/defeat runs | Seed **482193**: victory 4 paid / $58.20 / 74.00 (includes the VIP and the Critic), defeat 0 paid. Strategies are in `test-runs/README.md`. | P2 |
| **5.3(b)** Stage 2 edge cases | Table: VIP loses 5, Critic loses 12, Regular still loses 8, VIP bills, Critic always gets Pasta, every game has exactly 1 VIP + 1 Critic | P4 |
| **5.5** Regression (1 → 2) | Stage 1 rules still pass on Stage 2 code. Cite `CustomerTest` (480 checks) and `Stage2IntegrationTest` (334). | P4 |
| **6.2** Reflections | 1 paragraph each | Everyone |

# Task 2: Stage 3 split (adjusted to your actual code)
Your code already has a good starting point: `Customer.calculateBillCents()` handles the VIP discount. Stage 3 has to **lock the price when the order is taken** (brief: *"The price is locked when the order is taken"*). The `Cashier` currently calculates it at payment time, so that has to change.

| Person | Stage 3 work | Must achieve |
|---|---|---|
| **P1 Divyansh** | `RestaurantController`: **Host step** in the turn; **Happy Hour state**: `activateHappyHour()` (once per game, at the start of a turn, lasts that turn and the next), turns remaining, **+15 satisfaction at turn start** for customers waiting for a table or food (max 100). Merge, **tag `stage-3`**, README "Stage 2 → 3". | Stage 1 and 2 rules unchanged |
| **P2 Lei Yueer** | `Customer`: extend pricing so **VIP 10% is applied first, then Happy Hour 20%**; `recoverSatisfaction(int)` (capped at 100, no effect once left or served); **combo choice rule** in `selectItem()` (decide whether the Critic still prefers Pasta) | VIP Burger in Happy Hour = **$10.80** |
| **P3 Shi Hong** | New **`ComboMeal extends MenuItem`** holding two `MenuItem`s (the composition example): price = 90% of the two prices combined, rounded to the nearest cent; units = the sum. `Menu` adds combos. **`Order` gets a locked `priceCents`**, set when it's created. | Salad + Pasta = **$24.30, 3 units** |
| **P4 Jan Guirey** | New **`Host extends Staff`**: `seat()` with the same checks as the Waiter, at most 1 per turn. **`Cashier` charges `order.getPriceCents()`** (the locked price). | Host and Waiter can each seat someone in the same turn |
| **P5 Liu Yanxi** | `RestaurantUI`: a Host task menu; an **"Activate Happy Hour?"** prompt at turn start (only while unused); **Happy Hour turns remaining** on screen; combos shown in orders and bills | Invalid choices still re-prompt without using up a task |

**Report work for Stage 3** (same owners as before):
- **P1:** 1.4 / 3.1 Stage 3 rows, 4.3.2, 4.3.4
- **P2:** 4.3.1 + Stage 3 victory/defeat runs
- **P3:** **2.1 final class diagram** with notes on encapsulation, polymorphism and composition (`ComboMeal` is the obvious composition example)
- **P4:** 5.4 Stage 3 edge cases + 5.5 full regression
- **P5:** 4.3.3 + **2.2 sequence diagram** (with P1), using VIP pricing or Happy Hour as the Stage 2/3 behaviour it must show
- **Everyone:** 6.3

**Before coding: a 30-minute design call** to settle 3 things:
1. **Locked price.** Calculate the bill once in `Waiter.takeOrder()` and store it in `Order`.
2. **Rounding.** Round after each discount, or once at the end? VIP Burger: 1500 → 1350 → 1080 gives $10.80 either way, but other items may differ by a cent. Pick one and document it.
3. **Combo rule.** For example: "a customer orders a combo if their seeded slot says so". Write it in the README.


---

## Exchange 4

### Prompt (Divyansh)

okay how about, since there are 3 features in stage 3, we assign one person to each feature, and then out of the last two one can work on sequence diagram, one can work on the docs, is that viable or not?

### Reply (Claude)

**Yes, it's viable, and in some ways better** than splitting by class, because each person owns a whole feature from start to finish. It needs 3 tweaks to work, though. The 3 features are **different sizes** and they **touch the same files**.

## The two problems to plan around

**1. The features cut across the same files.**
| File | Host | ComboMeal | Happy Hour |
|---|---|---|---|
| `RestaurantController` | ✏️ Host step | | ✏️ activation, duration, +15 recovery |
| `RestaurantUI` | ✏️ Host menu | ✏️ show combos | ✏️ prompt, turns remaining |
| `Customer` | | ✏️ combo choice rule | ✏️ VIP first, then 20% off |
| `Order` | | ✏️ locked price | ✏️ locked price |
| `Cashier` | | ✏️ charge locked price | ✏️ charge locked price |

Three people editing `RestaurantController` and `RestaurantUI` at once means **merge conflicts**, which are the hardest thing for Git beginners.

**2. The features aren't the same size.** Host is small (a `Staff` subclass plus one turn step). ComboMeal is medium. **Happy Hour is the biggest**: activation rules, duration, recovery, discount order and price locking.

## The 3 tweaks
1. **Do the shared base first, as one small PR on day 1:** `Order` gets a locked `priceCents` set when the order is taken, and `Cashier` charges it. Both ComboMeal and Happy Hour build on this, so it can't be done twice in different ways.
2. **Merge in a fixed order: Host → ComboMeal → Happy Hour**, smallest first. Before opening a PR, each person runs `git checkout main`, `git pull`, then `git merge main` on their branch, so they deal with conflicts against already-merged work, one at a time.
3. **Give Happy Hour to the strongest coder**, who should also own the controller (that's you).

## Suggested assignment
| Person | Job | Why them |
|---|---|---|
| **P1 Divyansh** | **Happy Hour** + the day-1 locked-price PR + merges + `stage-3` tag | Biggest feature, lives mostly in the controller, which you wrote |
| **P4 Jan Guirey** | **Host** (`Host extends Staff`, Host step, Host menu in the UI) | Wrote `Staff`, `Waiter` and `Cashier`, so Host is basically another Waiter |
| **P3 Shi Hong** | **ComboMeal** (`ComboMeal extends MenuItem` holding 2 items, `Menu`, the combo choice rule, showing combos) | Wrote `Menu`, `MenuItem` and `Order` |
| **P2 Lei Yueer** | **Sequence diagram** + **Stage 3 victory/defeat runs** + **Stage 3 edge-case tests and full regression** | Knows `Customer`, which is the centre of the sequence diagram scenario |
| **P5 Liu Yanxi** | **Docs**: finish the Stage 1 gaps, all Stage 2 sections, 4.3 (Stage 2 → 3 changes), and drafts of 3.3–3.6. Also **reviews every PR that touches `RestaurantUI`**. | Wrote the design log and report drafts, and owns the UI |

## Making the two non-coding roles work
- **Sequence diagram (P2):** the brief says it must show *"one complete scenario… one Stage 2 or 3 behaviour"* and must match the final code. P2 can **draft it now** from the agreed design (e.g. a VIP customer ordering during Happy Hour: arrival → seat → order → Chef → READY → SERVED → eating → payment at $10.80 → result check), then **fix it after the `stage-3` tag**. The diagram alone isn't a week's work, which is why P2 also does the tests and runs.
- **Docs (P5):** there's plenty: 6+ empty Stage 1 sections, all of Stage 2, and the design discussion. Reviewing UI PRs also keeps P5's file from getting broken by three different people.
- **The video:** the brief says *"each member explains a part they understand."* P2 can present the sequence diagram and P5 the stage comparisons and diagrams, which are video segment 7. Everyone still gets a meaningful part.

**Bottom line:** go with your plan, plus the day-1 locked-price PR, a fixed merge order, and P5 reviewing UI changes.

---
