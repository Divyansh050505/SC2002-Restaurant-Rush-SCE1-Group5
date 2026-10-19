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