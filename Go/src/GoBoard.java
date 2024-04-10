
import java.util.Scanner;


public class GoBoard {

    public static void main(String[] args) {
        // Initialize a 9x9 2D array to represent the Go board
        String[][] goBoard = new String[9][9];
        /*String[][] goBoard = {
            {"+", "+", "◯", "●", "●", "◯", "+", "+", "+"},
            {"+", "◯", "●", "+", "●", "◯", "+", "+", "+"},
            {"+", "◯", "●", "●", "●", "◯", "+", "+", "+"},
            {"+", "+", "◯", "●", "+", "●", "◯", "+", "+"},
            {"+", "+", "+", "◯", "●", "◯", "+", "+", "+"},
            {"+", "+", "+", "+", "◯", "+", "+", "+", "+"},
            {"+", "+", "+", "+", "+", "+", "+", "+", "+"},
            {"+", "+", "+", "+", "+", "+", "+", "+", "+"},
            {"+", "+", "+", "+", "+", "+", "+", "+", "+"}
        };
        }*/
        

        // Initialize the board with spaces and print it
        initializeBoard(goBoard);
        printBoard(goBoard);

        Scanner sc = new Scanner(System.in);
        boolean previousPlayerPassed = false; // Track if the previous player passed
        String currentPlayer = "●"; // Black goes first

        try {
            while (true) {
                boolean playerPassed = promptForMove(goBoard, currentPlayer, sc);
                printBoard(goBoard);

                if (playerPassed) {
                    if (previousPlayerPassed) {
                        System.out.println("Both players passed. Game over.");
                        break; // End the game if both players pass consecutively
                    }
                    previousPlayerPassed = true; // Current player passed
                } else {
                    previousPlayerPassed = false; // Current player made a move
                }

                // Switch current player for next turn
                currentPlayer = currentPlayer.equals("●") ? "◯" : "●";
            }
        } finally {
            sc.close(); // Ensure scanner is closed
        }
    }


        
// Global variable to mark visited stones during recursion
static boolean[][] visited;

public static void initializeBoard(String[][] board) {
    for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
            board[i][j] = "+"; // // Initialize with + for intersections
        }
    }
}

public static void printBoard(String[][] board) {
    // Print column coordinates
    System.out.print("  ");
    for (int i = 0; i < board.length; i++) {
        System.out.print((i + 1) + "   "); // Print column numbers, reset every 10
    }
    System.out.println();

    for (int i = 0; i < board.length; i++) {
        // Print row coordinate
        System.out.print((i + 1) + " "); // Print row numbers, reset every 10

        for (int j = 0; j < board[i].length; j++) {
            System.out.print(board[i][j]);
            if (j < board[i].length - 1) {
                System.out.print(" - "); // Print - to separate intersections, except after the last piece
            }
        }
        System.out.println();

        if (i < board.length - 1) {
            System.out.println("  |   |   |   |   |   |   |   |   |  "); // Print vertical separators
        }
    }
}

public static boolean isInBounds(int row, int column, String[][] board) {
    return row >= 0 && row < board.length && column >= 0 && column < board[row].length;
}
public static void checkForCaptures(String[][] board, int lastRow, int lastColumn, String piece) {
    String opponentPiece = piece.equals("●") ? "◯" : "●";
    visited = new boolean[board.length][board[0].length]; // Reset visited array

    // Check all four directions for captures
    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    for (int[] dir : directions) {
        int newRow = lastRow + dir[0], newColumn = lastColumn + dir[1];
        if (isInBounds(newRow, newColumn, board) && board[newRow][newColumn].equals(opponentPiece)) {
            // Reset visited for each new group check
            visited = new boolean[board.length][board[0].length];
            if (!hasLiberty(board, newRow, newColumn, opponentPiece)) {
                // Reset visited again to remove stones correctly
                visited = new boolean[board.length][board[0].length];
                removeGroup(board, newRow, newColumn, opponentPiece);
            }
        }
    }
}




public static boolean hasLiberty(String[][] board, int row, int column, String piece) {
    if (!isInBounds(row, column, board) || visited[row][column]) {
        return false;
    }
    if (board[row][column].equals("+")) { // An empty space is a liberty
        return true;
    }
    if (!board[row][column].equals(piece)) { // Not part of the group
        return false;
    }
    // Mark this stone as visited
    visited[row][column] = true;

    // Recursively check all directions
    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Down, Left, Up
    for (int[] dir : directions) {
        if (hasLiberty(board, row + dir[0], column + dir[1], piece)) {
            return true;
        }
    }
    return false; // No liberties found
}

public static void removeGroup(String[][] board, int row, int column, String piece) {
    if (!isInBounds(row, column, board) || board[row][column] != piece || visited[row][column]) {
        return;
    }
    // Remove the stone
    board[row][column] = "+";
    visited[row][column] = true; // Mark as visited to avoid revisiting

    // Recursively remove all connected stones
    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Down, Left, Up
    for (int[] dir : directions) {
        removeGroup(board, row + dir[0], column + dir[1], piece);
    }
}

public static boolean promptForMove(String[][] board, String piece, Scanner sc) {
        System.out.println("Player " + piece + ", enter row, column (1-9) to place your piece, or enter 'pass': ");
        String input = sc.nextLine().trim();

        if ("pass".equalsIgnoreCase(input)) {
            System.out.println("Player " + piece + " passed.");
            return true; // Indicate that the player has passed
        }

        // Parse row and column from input
        try {
            String[] parts = input.split("\\s+");
            int row = Integer.parseInt(parts[0]) - 1;
            int column = Integer.parseInt(parts[1]) - 1;

            if (row >= 0 && row < board.length && column >= 0 && column < board[row].length && board[row][column].equals("+")) {
                board[row][column] = piece;
                checkForCaptures(board, row, column, piece);
                System.out.println("Piece placed.");
                return false; // Move made, return false for "not passed"
            } else {
                System.out.println("Invalid move. Try again.");
                return promptForMove(board, piece, sc); // Recursively prompt for a valid move
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            return promptForMove(board, piece, sc); // Handle invalid inputs
        }
    }
}

