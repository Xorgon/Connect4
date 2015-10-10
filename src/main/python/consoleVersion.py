# -*- coding: utf-8 -*-
import os
import connect4


class ConsoleVersion:
    vBoard = None

    def __init__(self):
        self.vBoard = connect4.VirtualBoard()
        self.c4()

    def output_board(self, board):
        """Outputs the board to the console."""
        os.system('cls' if os.name == 'nt' else 'clear')
        print("= 0 1 2 3 4 5 6 =")
        for ny in range(0, 6):
            y = 5 - ny
            line = "| "
            for x in range(0, 7):
                if board[x][y] == 1:
                    line += "X "
                elif board[x][y] == 2:
                    line += "O "
                else:
                    line += "  "
            line += "|"
            print(line)
        print("= 0 1 2 3 4 5 6 =")

    def take_turn(self):
        """Function governing the cycle of turns."""
        win = 0
        turn = 1
        while win == 0:
            slot = None
            if turn == 1:
                print("X Turn.")
            else:
                print("O Turn.")
            not_placed = True
            while not_placed:
                inpt = input()
                if inpt == "stop" or inpt == "ff":
                    not_placed = False
                    win == 4
                    if turn == 1:
                        print("X Surrenders.")
                    else:
                        print("O Surrenders.")
                    return
                is_integer = True
                try:
                    slot = int(inpt)
                except ValueError:
                    is_integer = False
                    print("Invalid input, slot must be a number between 0 and 6")
                if is_integer:
                    if slot > 6 or slot < 0:
                        print("Invalid input, slot must be a number between 0 and 6")
                    elif self.vBoard.place_piece(slot, turn):
                        not_placed = False
                    else:
                        print("You can't go there.")
            self.output_board(self.vBoard.board)
            if turn == 1:
                turn = 2
            else:
                turn = 1
            win = self.vBoard.test_win()
        if win == 1:
            print("X Wins!")
        elif win == 2:
            print("O Wins!")
        elif win == 3:
            print("It's a draw!")

    def c4(self):
        """Starts a game of Connect 4."""
        self.output_board(self.vBoard.board)
        self.take_turn()


ConsoleVersion()