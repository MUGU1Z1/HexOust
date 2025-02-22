# HexOust - A Strategic Hexagonal Board Game

![HexOust Banner](https://your-image-url.com/banner.png)

## 🎯 Introduction
HexOust is a two-player strategy board game played on a **hexagonal 7x7 grid**. Players (Red and Blue) take turns placing pieces, aiming to capture and eliminate all opponent pieces. The game follows **capture and non-capture movement rules**.

## 📜 Features
- **Turn-based Gameplay**: Players take turns strategically placing pieces.
- **Hexagonal Board System**: Unique 127-cell grid design.
- **Capture & Non-Capture Mechanics**: Includes different movement strategies.
- **Intuitive GUI**: Built with **JavaFX** for interactive play.
- **Game State Management**: Supports saving and loading via JSON.
- **Unit Testing**: Uses **JUnit** for reliability.

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

## 🎮 How to Play
1. **Objective**: Capture all opponent pieces to win.
2. **Players**: Red & Blue take turns placing pieces.
3. **Valid Moves**: Pieces can only be placed in **unoccupied hexes**.
4. **Winning Condition**: A player wins when the opponent has no remaining pieces.

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
