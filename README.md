# KnightForge

KnightForge is a Java chess project focused on building a complete, interactive chess system with clear object-oriented design. The goal of this project is not only to make a playable chess game, but also to use chess as a structured way to practice software engineering fundamentals such as abstraction, inheritance, event-driven interaction, state management, and modular architecture.

## Project Purpose

This project is intended to deliver a desktop chess application that:

- implements a visual 8x8 chessboard and interactive piece movement;
- models chess pieces and board behavior through object-oriented programming;
- enforces legal move rules and turn-based gameplay;
- supports core game-state management such as selection, movement, capture, and status updates;
- lays the foundation for save/load, move history, replay, and future AI-based play.

In practical terms, KnightForge is designed to help the team learn how to turn a well-defined board game into a maintainable software system. Chess is a strong fit for this purpose because it has fixed rules, clear piece responsibilities, rich interactions between objects, and natural opportunities for progressive extension.

## Development Goals

The project is organized around several concrete goals:

- build a stable chessboard UI in Java;
- represent each piece with its own movement logic;
- manage turns, board updates, and player interaction correctly;
- store and restore game progress from files;
- support move recording and replay;
- prepare the codebase for future features such as AI search and stronger rule coverage.

## Scope of the Project

KnightForge aims to cover both software structure and user-facing behavior.

From the system side, the project emphasizes:

- clean class design for the board, pieces, controllers, and game state;
- reusable logic for legal moves and board updates;
- separation between model, view, and control responsibilities;
- incremental extension from a simple playable version to a more complete chess experience.

From the user side, the project aims to provide:

- a clear graphical interface;
- direct click-based interaction for selecting and moving pieces;
- visible turn flow and game feedback;
- persistent save data for continued play.

## Why This Project

KnightForge is meant to be both a functional game and a learning exercise. By implementing chess in Java, the project gives practical experience with:

- object-oriented design;
- GUI programming;
- event handling;
- file I/O;
- rule-based logic;
- step-by-step feature evolution in a real codebase.

## Current Direction

The current codebase already includes the basic board structure, square components, click handling, and rook movement logic. The broader project direction is to continue expanding this foundation into a fuller chess system under the KnightForge name.

## Project Structure

The project now follows a standard Gradle layout:

- `src/main/java` for application source code
- `src/main/resources` for images and bundled resource files
- `src/test/java` for automated tests
- `build.gradle` for build configuration

This structure makes the project easier to build in IntelliJ, easier to test, and easier for multiple teammates to work on consistently.

## Build and Run

After opening the project as a Gradle project, you can use the Gradle Wrapper commands:

- `./gradlew.bat build` on Windows to compile the project and run tests
- `./gradlew.bat test` on Windows to run the test suite
- `./gradlew.bat run` on Windows to launch the application
- `./gradlew build` on macOS or Linux

## For Midterm Review
See docs/midterm-design.md
