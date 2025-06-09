import java.util.Scanner;

enum Symbol {
    CROSS,
    CIRCLE,
    EMPTY
}

class Player {
    String id;
    Symbol symbol;

    public Player(String id, Symbol symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public Symbol getSymbol(){
        return symbol;
    }
}

class Board {
    Symbol[][] grid;

    public Board() {
        this.grid = new Symbol[3][3];
        // Assign all cells as empty initially
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid[i][j] = Symbol.EMPTY;
            }
        }
    }

    public String getSymbol(Symbol symbol){
        switch(symbol){
            case Symbol.CROSS:
                return "X";
            case Symbol.CIRCLE:
                return "O";
            default:
                return "-";
        }
    }

    public void displayBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(getSymbol(grid[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == Symbol.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean placeSymbol(int row, int col, Symbol symbol) {
        // Check if row and col are within valid range
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            System.out.println("Invalid cell coordinates!");
            return false;
        }

        if (grid[row][col] != Symbol.EMPTY) {
            System.out.println("Symbol is already placed, Can't place it in this cell!");
            return false;
        }
        grid[row][col] = symbol;
        return true;
    }

    public Symbol checkHorizontalWin() {
        for (int i = 0; i < 3; i++) {
            if (grid[i][0] != Symbol.EMPTY &&
                    grid[i][0] == grid[i][1] &&
                    grid[i][1] == grid[i][2]) {
                return grid[i][0]; // Return the winning symbol
            }
        }
        return Symbol.EMPTY; // No winner horizontally
    }
    
    public Symbol checkVerticalWin() {
        for (int j = 0; j < 3; j++) {
            if (grid[0][j] != Symbol.EMPTY &&
                    grid[0][j] == grid[1][j] &&
                    grid[1][j] == grid[2][j]) {
                return grid[0][j]; // The winning symbol
            }
        }
        return Symbol.EMPTY; // No vertical winner
    }
    
    public Symbol checkDiagonalWin() {
        // Top-left to bottom-right diagonal
        if (grid[0][0] != Symbol.EMPTY &&
            grid[0][0] == grid[1][1] &&
            grid[1][1] == grid[2][2]) {
            return grid[0][0];
        }

        // Top-right to bottom-left diagonal
        if (grid[0][2] != Symbol.EMPTY &&
            grid[0][2] == grid[1][1] &&
            grid[1][1] == grid[2][0]) {
            return grid[0][2];
        }

        return Symbol.EMPTY; // No diagonal winner
    }


    public Symbol checkWinner() {
        Symbol winner;

        winner = checkHorizontalWin();
        if (winner != Symbol.EMPTY) return winner;

        winner = checkVerticalWin();
        if (winner != Symbol.EMPTY) return winner;

        winner = checkDiagonalWin();
        if (winner != Symbol.EMPTY) return winner;

        return Symbol.EMPTY; // No winner
    }

}

class TicTacToeController {
    Board board;
    Player player1;
    Player player2;
    Player currentPlayer;
    private Scanner scanner = new Scanner(System.in);

    public TicTacToeController(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    private int getRowInput(Player player) {
        System.out.print("Player " + player.getId() + " (" + player.getSymbol() + "), enter row (0-2): ");
        return scanner.nextInt();
    }

    private int getColInput(Player player) {
        System.out.print("Player " + player.getId() + " (" + player.getSymbol() + "), enter column (0-2): ");
        return scanner.nextInt();
    }

    public void startGame() {
        while (!board.isFull()) {
            // Show current board
            board.displayBoard();

            // Get user input
            int row = getRowInput(currentPlayer);
            int col = getColInput(currentPlayer);

            boolean moveSuccess = board.placeSymbol(row, col, currentPlayer.getSymbol());
            if (!moveSuccess) {
                System.out.println("Invalid move, try again!");
                continue;
            }

            // Check for winner
            Symbol winner = board.checkWinner();
            if (winner != Symbol.EMPTY) {
                System.out.println("Player " + currentPlayer.getId() + " (" + currentPlayer.getSymbol() + ") wins!");
                board.displayBoard();
                return;
            }

            currentPlayer = currentPlayer == player1 ? player2 : player1;
        }
        
        // If loop ends and no winner → draw!
        System.out.println("It's a draw!");
        board.displayBoard();
    }
}

public class TicTacToe {
    public static void main(String[] args) {
        Board board = new Board();
        Player player1 = new Player("Player1", Symbol.CROSS);
        Player player2 = new Player("Player2", Symbol.CIRCLE);

        TicTacToeController ticTacToe = new TicTacToeController(board, player1, player2);
        ticTacToe.startGame();
    }
}

/*

+-----------------------------------------+
|                 TicTacToe               |
|         (Entry point - main class)      |
+-----------------------------------------+
| + main(String[] args): void             |
+-----------------------------------------+

                     |
                     v
+-----------------------------------------+
|         TicTacToeController             |
+-----------------------------------------+
| - board: Board                          |
| - player1: Player                       |
| - player2: Player                       |
| - currentPlayer: Player                 |
| - scanner: Scanner                      |
+-----------------------------------------+
| + startGame(): void                     |
| - getRowInput(player: Player): int      |
| - getColInput(player: Player): int      |
+-----------------------------------------+

                     |
                     v
+-----------------------------------------+
|                Board                    |
+-----------------------------------------+
| - grid: Symbol[3][3]                    |
+-----------------------------------------+
| + displayBoard(): void                  |
| + isFull(): boolean                     |
| + placeSymbol(row: int, col: int,       |
|     symbol: Symbol): boolean            |
| + checkHorizontalWin(): Symbol          |
| + checkVerticalWin(): Symbol            |
| + checkDiagonalWin(): Symbol            |
| + checkWinner(): Symbol                 |
+-----------------------------------------+

                     |
                     v
+-----------------------------------------+
|                Player                   |
+-----------------------------------------+
| - id: String                            |
| - symbol: Symbol                        |
+-----------------------------------------+
| + getId(): String                       |
| + getSymbol(): Symbol                   |
+-----------------------------------------+

+-----------------------------------------+
|                Symbol                   |
|               (enum)                    |
+-----------------------------------------+
| + CROSS                                 |
| + CIRCLE                                |
| + EMPTY                                 |
+-----------------------------------------+

============================================

No need of DB as it is memory game 

 */