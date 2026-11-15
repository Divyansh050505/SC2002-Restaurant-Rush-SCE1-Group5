# Generative AI Usage Declaration — Prompt and Response Transcript

**Purpose:** Record of a prompt submitted to a generative AI assistant and the response received, concerning the Stage 3 `ComboMeal` implementation for a group programming assignment.

**Source:** Transcribed from the supplied screenshot. The text below records the AI exchange; claims about code inspection and testing are those made by the AI in its response, not independently verified by this transcript.

---

## 1. Prompt Submitted to the AI

Im currently doing a portion of "stage 3" of the attached group assignment (implementing code required for ComboMeal), and had these issues given from my previous code, from the feedback given for my pull request on github

> "It doesn't compile on Java 17 (our README target): super(...) must be the first statement in the constructor. You can compute the values inline inside the super(...) call.
>
> Please use integer rounding for the price, e.g. (sum * 90 + 50) / 100, instead of (int)(0.9 * sum).
>
> Math.random() in Menu ignores the game seed, so replays aren't reproducible. Please use a fixed set of combos (or the game seed), and write the combo choice rule in the README. The brief requires 'a rule you document'."

I have made changes to the project folder to address those problems, except for updating the README file. Can u check whether the attached zip file that contains the project file has my assigned part (Implementing ComboMeal) implemented correctly as required by the requirements stated in the .docx file

---

## 2. Response Received from the AI

*AI interface indicated: “Worked for 7m 2s”.*

### Code review verdict: Not yet ready to merge

I checked the uploaded project against Section 4.3 [Stage 3: ComboMeal] of your assignment brief and compiled the Java source using `javac --release 17`.

**Your revised `ComboMeal` constructor now compiles under Java 17, but there are still two critical problems affecting your implementation.** One causes incorrect combo prices, and the other prevents the game from launching normally.

### 1. Critical: ComboMeal prices are always $0.00

(Must fix) `src/entity/ComboMeal.java`, line 13

Your current code:

```java
(90/100 * (item1.getPriceCents() + item2.getPriceCents()))
```

In Java, `90 / 100` performs integer division, which produces `0`, not `0.9`. As a result, the combo price is always zero.

Replace that expression with:

```java
((item1.getPriceCents() + item2.getPriceCents()) * 90 + 50) / 100
```

That uses integer cents and rounds to the nearest cent, as your teammate requested.

### 2. Critical: The menu crashes when no seed property exists

(Must fix) `src/entity/Menu.java`, line 21

Your current code:

```java
Random random = new Random(Long.getLong("restaurant.rush.seed"));
```

`Long.getLong(...)` returns `null` when the system property has not been set. Passing that into the `Random` constructor causes a `NullPointerException` due to automatic unboxing.

I reproduced this by running the actual application without special JVM arguments. It crashed before displaying the main menu.

There's also an initialization-order problem: `RestaurantController` constructs `Menu` before the UI asks the player to choose a game seed. Even after fixing the null exception, simply generating a random fallback seed would not make replayed combo choices reproducible.

### 3. The zero-price bug also causes incorrect payment records

I tested a complete combo order through the actual controller, including preparation, serving, eating and payment.

With seed `5`, Customer #1 ordered a combo. On turn 7, the results were:

| Check | Actual result | Expected |
|---|---|---|
| Customer status | PAID | PAID |
| Order status | PAID | PAID |
| Paid customer count | 0 | 1 |
| Revenue | $0.00 | Positive combo bill |

This happens because `RestaurantController.java`, lines 217–222, only records a paid customer when the amount collected is greater than zero.

Correcting your combo pricing will fix this particular problem for normal combo orders, without needing to modify the Cashier or Controller.

### 4. Requirements checklist

### 5. Recommended fix for `Menu.java`

The simplest approach is to use a fixed combo rather than randomly selecting which combo to add.

You already have seeded customer food selection in `Customer.java`, lines 82–105. Therefore, you do not actually need randomness in `Menu.java`.

In `Menu.java`, replace the entire `Random random` section and its `if / else if` blocks (lines 21–40) with:

```java
// Fixed Stage 3 combo option
items.add(new ComboMeal(items.get(0), items.get(2)));
```

This gives you a permanent Salad + Pasta combo. Also remove the unused `java.util.Random` import.

With your corrected pricing formula:

| Combo | Price | Prep units |
|---|---:|---:|
| Salad + Burger | $21.60 | 2 |
| Burger + Pasta | $29.70 | 3 |
| Salad + Pasta | $24.30 | 3 |

You could alternatively register all three fixed combos. Your existing six-slot food-selection mechanism supports six menu options, although this would change the gameplay more substantially.

**README explanation you can add**

#### Stage 3: ComboMeal selection rule

The menu includes a fixed Salad + Pasta ComboMeal, priced at $24.30 with 3 preparation units.

Regular and VIP customers select food using the existing seeded six-slot menu plan in `Customer.selectItem()`. Each menu option appears at least once in the initial plan, the remaining slots are filled using seeded random choices, and the slots are shuffled using the game seed. Each customer's ID determines their assigned slot.

A Regular or VIP customer orders a ComboMeal whenever their assigned slot corresponds to the combo option. Critic customers retain their Stage 2 preference for Pasta.

The manager cannot select a customer's food. Given the same seed and menu, the food-selection rule produces the same choices regardless of order-taking priority.

*This wording assumes you adopt the fixed-combo change above.*

### 6. Results after testing the proposed fixes

I also tested both corrections in a separate copy of your project, without changing your uploaded ZIP.

- Java 17 compilation (Passed)
- Normal application startup (Passed)
- Combo price and preparation units (Passed)
- Same-seed food selection reproducibility (Passed)
- Combo payment and revenue recording (Passed)
- VIP discount on $24.30 combo ($21.87) (Passed)

The four existing customer, arrival-plan and UI test classes also passed in that corrected copy.

However, `Stage2IntegrationTest` still failed because the saved Stage 2 victory inputs no longer match the changed Stage 3 menu. The team should preserve Stage 2 evidence at its original tag and add separate Stage 3 test runs.

Also, the uploaded ZIP does not yet contain the Host or Happy Hour implementations. Those are separate Stage 3 responsibilities, so I have not treated their absence as a defect in your ComboMeal contribution.

### Overall assessment

Your approach to ComboMeal is fundamentally workable. It uses the existing `MenuItem`, `Order`, `Chef` and customer-selection mechanisms without requiring substantial changes to the game's architecture.

For your assigned portion, I would make the pricing correction, replace the problematic menu randomness with a fixed combo, document the choice rule, and add a dedicated ComboMeal regression test.

After those changes, the implementation should satisfy the functional ComboMeal requirements in Section 4.3, subject to final integration testing with the rest of your group's Stage 3 features.
