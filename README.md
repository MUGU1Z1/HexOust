# HexOust - A Strategic Hexagonal Board Game


## 🎯 Introduction
HexOust is a two-player strategy board game played on a **hexagonal 7x7 grid**. Players (Red and Blue) take turns placing pieces, aiming to capture and eliminate all opponent pieces. The game follows **capture and non-capture movement rules**.

## 📜 Game Rules
### **1. Board and Pieces**
- The board consists of **127 hexagonal cells**, with **7 cells per side**.
- The board starts **empty**.
- Two players use distinct pieces: **Red (RED) and Blue (BLUE)**.

### **2. Turn-based Play**
- **RED** player always moves first.
- Players take turns placing pieces **only on empty hexagonal cells**.

### **3. Objective**
- A player wins by **removing all opponent pieces** or **forcing the opponent into a position with no legal moves**.

### **4. Two Types of Moves**
#### **① Non-Capturing Placement (NCP)**
- A piece is placed **independently** on the board.
- The placed piece **must not connect to the player's own pieces**.
- The piece **may be adjacent only to opponent’s pieces**.
- The turn ends after an NCP move.

#### **② Capturing Placement (CP)**
- A placed piece **must connect to the player’s existing pieces**, forming a **new, larger group**.
- The **new group must be adjacent to at least one opponent’s piece**.
- If the **new group is larger** than all connected opponent groups, **all those opponent groups are removed**.
- If additional captures are possible, the player **may continue moving** until no further captures can be made.

### **5. Consecutive Moves**
- If a CP move **successfully removes** opponent groups, the player **may continue playing**.
- A turn **only ends when an NCP move is made**.

### **6. Situations Where a Player Cannot Move**
- If a player has **no legal move**, they **must pass their turn**.
- The game ends **if neither player can move**.

### **7. Victory Conditions**
- A player wins by **eliminating all opponent pieces**.
- If the opponent **has no legal moves left**, the player also wins.

## 🛠️ Technologies Used
- **Programming Language**: Java
- **GUI Framework**: JavaFX / Swing
- **Data Storage**: JSON for saving game states
- **Testing Framework**: JUnit

## 🚀 Getting Started
### **1. Prerequisites**
Ensure you have **Java (JDK 17+)** installed on your system.

### **2. Installation & Running**
```sh
# Clone the repository
git clone https://github.com/L3xcy/HexOust.git
cd HexOust

# Compile the project
javac -d bin src/org/example/*.java src/org/example/controller/*.java src/org/example/model/*.java src/org/example/view/*.java

# Run the game
java -cp bin org.example.HexOustGame
```

### **3. Running the Executable JAR**
```sh
java -jar HexOust.jar
```

## 📌 Project Structure
```
HexOust/
│── src/
│   ├── org/example/
│   │   ├── HexOustGame.java       # Main entry point
│   │   ├── controller/            # Game logic & rules
│   │   ├── model/                 # Board, Cells, and Players
│   │   ├── view/                  # JavaFX GUI components
│── assets/                         # Images, icons, and UI elements
│── bin/                            # Compiled Java class files
│── README.md                       # Project documentation
│── HexOust.jar                     # Executable JAR file
```

## 🛠 Contribution
We welcome contributions! To contribute:
1. **Fork the repository**
2. **Create a new branch**: `git checkout -b feature-branch`
3. **Commit changes**: `git commit -m 'Added new feature'`
4. **Push to GitHub**: `git push origin feature-branch`
5. **Open a Pull Request**

## 📜 License
This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🏆 Acknowledgments
- Developed as part of the **COMP20050-SEP2-2024/25** course project.
- Special thanks to all contributors!

---
**Team MUGUIZI** | HexOust Development Team
