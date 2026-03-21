# KnightForge Midterm Design Notes

## Repository

- GitHub URL: `https://github.com/Hhy903/Knight-Forge`

## 1. Design Patterns

### Pattern 1: State Pattern

- Location:
  - `src/main/java/com/knightforge/controller/GamePhase.java`
  - `src/main/java/com/knightforge/controller/GameSession.java`
- How it is used:
  - `GamePhase` defines the major stages of the game:
    - `SELECTING_PIECE`
    - `SELECTING_TARGET`
    - `PROMOTION_PENDING`
    - `GAME_OVER`
    - `LOADING_GAME`
  - `GameSession` changes behavior based on the current phase.
  - For example, a board click is handled differently depending on whether the user is selecting a piece, selecting a target, or waiting for promotion.

### Pattern 2: Factory Pattern

- Location:
  - `src/main/java/com/knightforge/view/PieceComponentFactory.java`
  - used by `src/main/java/com/knightforge/view/Chessboard.java`
- How it is used:
  - The board repeatedly creates different square components depending on the board state:
    - `EmptySlotComponent`
    - `PieceChessComponent`
  - `PieceComponentFactory` centralizes this construction logic so the board does not directly decide which concrete component class to instantiate.
  - 
### Pattern 3: Observer Pattern

- Location:
  - `src/main/java/com/knightforge/controller/GameSessionListener.java`
  - `src/main/java/com/knightforge/controller/GameSessionEvent.java`
  - `src/main/java/com/knightforge/controller/GameSession.java`
  - `src/main/java/com/knightforge/view/Chessboard.java`
- How it is used:
  - `GameSession` acts as the subject.
  - `Chessboard` acts as an observer by implementing `GameSessionListener`.
  - Whenever the game phase or result changes, `GameSession` publishes a `GameSessionEvent`.
  - The board listens to these updates and refreshes the UI, status message, and game-over dialog.

### Pattern 4: Singleton Pattern

- Location:
  - `src/main/java/com/knightforge/view/SkinManager.java`
  - used by `src/main/java/com/knightforge/view/PieceChessComponent.java`
- How it is used:
  - `SkinManager` exposes a single shared instance through `getInstance()`.
  - All chess piece images are loaded and cached through this global manager.

## 2. Foundational Classes and Real Logic

The project already contains implemented core logic rather than empty skeleton classes.

### `BoardState`

- Stores the full board and current game metadata.
- Implements:
  - legal move validation
  - check detection
  - checkmate detection
  - stalemate detection
  - insufficient material draw
  - fifty-move rule
  - threefold repetition
  - castling
  - en passant
  - promotion support
  - save/load serialization

### `GameSession`

- Acts as the main game workflow controller.
- Manages:
  - current phase
  - selected square
  - pending promotion
  - game-over result
  - status messages
- Connects board rules with UI interaction flow.

### `Chessboard`

- Renders the board and piece components.
- Displays:
  - selected square
  - legal move hints
  - algebraic board coordinates
- Bridges user clicks to `GameSession`.

### `Move`

- Represents one reversible chess move.
- Stores enough historical data for undo and rule-sensitive restoration.

## 3. Object-Oriented Principles

### Coding to abstractions

- The system separates domain state from rendering.
- `GameSession` depends on `BoardState` instead of directly controlling Swing components.
- `GameController` delegates board operations instead of embedding chess rules in the frame.

### Polymorphism

- `ChessComponent` is an abstract superclass.
- Concrete subclasses:
  - `EmptySlotComponent`
  - `PieceChessComponent`
- The board treats them uniformly as `ChessComponent` while each subclass renders itself differently.

### Dependency Injection

- `GameSession` receives `BoardState` through its constructor.
- `GameController` receives `Chessboard` through its constructor.
- `ClickController` receives `Chessboard` through its constructor.
- This makes object responsibilities clearer and reduces hard-coded global state.

## 4. Meaningful Test Cases

The following test cases have been implemented in `src/test/java/com/knightforge/model/BoardStateTest.java`:

1. Promotion detection
   - Verifies that a pawn reaching the last rank triggers promotion handling.

2. En passant
   - Verifies that a legal en passant capture is allowed and correctly updates the board.

3. Castling legality
   - Verifies that castling moves both king and rook correctly.

4. Checkmate detection
   - Verifies that the checkmate test position triggers `GAME_OVER` after `Qh4#`.

5. Draw detection
   - Verifies that a king-versus-king position is recognized as insufficient material.

## 5. Current Midterm Status

At the midterm stage, the project already demonstrates:

- non-trivial architecture
- multiple design patterns
- implemented game logic
- clear OO decomposition
- support for major chess rules

The main remaining work is:

- skin switching
- AI interface and greedy AI extension
- additional UI polishing
