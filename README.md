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
Run these from the repository root (the folder containing `src`).

**Mac / Linux:**
```
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
java -cp out RestaurantApp
```
**Windows (Command Prompt):**
```
dir /s /b src\*.java > sources.txt
javac --release 17 -d out @sources.txt
java -cp out RestaurantApp
```

## How to play
Each turn the game shows the restaurant state, then asks for one Waiter task and one Chef task by number.
Customers and orders are chosen by the number shown next to them; `0` goes back or makes the staff member wait.
An invalid choice explains why it failed and asks again without using up the staff task.

## Replaying the test runs
The recorded runs in `test-runs/` can be replayed by redirecting them into the game (after building as above):
```
java -cp out RestaurantApp < test-runs/stage1_victory.txt
java -cp out RestaurantApp < test-runs/stage1_defeat.txt
```

## Source layout
| Package | Role |
|---|---|
| `RestaurantApp` (default package) | Entry point: creates the controller and UI |
| `boundary` | `RestaurantUI`: console input and output only |
| `control` | `RestaurantController`: turn sequence and game state |
| `entity` | Customers, staff, menu, orders, tables, enums |

## Game settings
- 18 turns, 2 tables
- Staff: 1 Waiter, 1 Chef, 1 Cashier
- Customers arrive at the start of turns 1, 4, 7, 10, 13 and 16
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units)
- Victory: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60