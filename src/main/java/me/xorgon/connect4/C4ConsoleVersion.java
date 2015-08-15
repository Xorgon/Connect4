package me.xorgon.connect4;

import me.xorgon.connect4.util.VirtualBoard;

import java.util.Scanner;

/**
 * Created by Elijah on 15/08/2015.
 */
public class C4ConsoleVersion {

    private VirtualBoard board;

    private boolean redTurn = true;

    private Scanner scanner;

    /*public static void main(String[] s){
        C4ConsoleVersion game = new C4ConsoleVersion();
    }*/

    public C4ConsoleVersion() {
        board = new VirtualBoard();
        board.initialize();
        outputBoard();
        scanner = new Scanner(System.in);
        takeTurn();
    }

    public void outputBoard() {
        VirtualBoard.SpaceType[][] spaces = board.getSpaces();
        System.out.println("= 0 1 2 3 4 5 6 =");
        for (int y = 5; y >= 0; y--) {
            String line = "| ";
            for (int x = 0; x < 7; x++) {
                if (spaces[x][y] == VirtualBoard.SpaceType.RED){
                    line = line.concat("X ");
                } else if (spaces[x][y] == VirtualBoard.SpaceType.BLUE){
                    line = line.concat("O ");
                } else {
                    line = line.concat("  ");
                }
            }
            line = line.concat("|");
            System.out.println(line);
        }
        System.out.println("= 0 1 2 3 4 5 6 =");
    }

    public void takeTurn(){
        VirtualBoard.WinStatus win = VirtualBoard.WinStatus.NONE;
        while (win == VirtualBoard.WinStatus.NONE) {
            if (redTurn){
                System.out.println("X Turn.");
            } else {
                System.out.println("O Turn.");
            }
            int slot = Integer.valueOf(scanner.nextLine());
            if (slot > 6 || slot < 0){
                System.out.println("Invalid input, slot must be between 0 and 6.");
                slot = Integer.valueOf(scanner.nextLine());
            }
            board.placePiece(slot, redTurn ? VirtualBoard.SpaceType.RED : VirtualBoard.SpaceType.BLUE);
            outputBoard();
            redTurn = !redTurn;
            win = board.testWin();
        }
        if (win == VirtualBoard.WinStatus.RED) {
            System.out.println("X Wins!");
        } else {
            System.out.println("O Wins!");
        }
    }
}
