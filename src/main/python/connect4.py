# -*- coding: utf-8 -*-
import numpy
import os

board = numpy.zeros((7, 6))


def c4():
    """Starts a game of Connect 4."""
    reset_board()
    output_board()
    take_turn()


def reset_board():
    """Resets the board."""
    for x in range(0, 7):
        for y in range(0, 6):
            board[x][y] = 0


def place_piece(x, color):
    """
    Places a piece on the board.
    
    x -- Column Number
    color -- 1 or 2 depending on team
    
    returns boolean of piece placement success
    """
    print(x)
    print(color)
    for y in range(0, 6):
        if board[x][y] == 0:
            print(y)
            board[x][y] = color
            return True
    return False


def test_win():
    """
    Tests the win status of the game
    
    returns win status
    0 -- No win
    1 -- X win
    2 -- Y win
    3 -- Draw
    """
    """Vertical Testing"""
    for x in range(0, 7):
        v_win_status = v_test(x)
        if v_win_status != 0:
            return v_win_status

    """Horizontal Testing"""
    for y in range(0, 6):
        h_win_status = h_test(y)
        if h_win_status != 0:
            return h_win_status

    """Diagonal Testing and Draw Testing"""
    draw = True
    for x in range(0, 7):
        for y in range(0, 6):
            if board[x][y] == 0:
                draw = False
            d_win_status = diag_test(x, y)
            if d_win_status != 0:
                return d_win_status

    if draw:
        return 3

    return 0


def h_test(y):
    """
    Tests for horizontal wins
    
    returns win status
    """
    last_space = 0
    conc_spaces = 1
    for x in range(0, 7):
        if board[x][y] == last_space:
            conc_spaces += 1
        else:
            conc_spaces = 1
            last_space = board[x][y]
        if conc_spaces == 4 and last_space != 0:
            return last_space
    return 0


def v_test(x):
    """
    Tests for vertical wins
    
    returns win status
    """
    last_space = 0
    conc_spaces = 1
    for y in range(0, 6):
        if board[x][y] == last_space:
            conc_spaces += 1
        else:
            conc_spaces = 1
            last_space = board[x][y]
        if conc_spaces == 4 and last_space != 0:
            return last_space
    return 0


def diag_test(x, y):
    """
    Tests for diagonal wins
    
    returns win status
    """
    conc_spaces_up = 1
    conc_spaces_down = 1
    last_space_up = board[x][y]
    last_space_down = board[x][y]
    if board[x][y] == 0:
        return 0
    for i in range(1, 4):
        if (x + i) < 7:
            cont = False
            if y + i < 6 and board[x + i][y + i] == last_space_up:
                conc_spaces_up += 1
                cont = True
            if y - i >= 0 and board[x + i][y - i] == last_space_down:
                conc_spaces_down += 1
                cont = True
            if not cont:
                return 0
            if conc_spaces_up == 4:
                return last_space_up
            if conc_spaces_down == 4:
                return last_space_down
    return 0


def take_turn():
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
                elif place_piece(slot, turn):
                    not_placed = False
                else:
                    print("You can't go there.")
        output_board()
        if turn == 1:
            turn = 2
        else:
            turn = 1
        win = test_win()
    if win == 1:
        print("X Wins!")
    elif win == 2:
        print("O Wins!")
    elif win == 3:
        print("It's a draw!")


def output_board():
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

c4()
