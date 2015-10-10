# -*- coding: utf-8 -*-
import numpy


class VirtualBoard():
    board = numpy.zeros((7, 6))

    def __init__(self):
        self.reset_board()

    def reset_board(self):
        """Resets the board."""
        for x in range(0, 7):
            for y in range(0, 6):
                self.board[x][y] = 0

    def place_piece(self, x, color):
        """
        Places a piece on the board.

        x -- Column Number
        color -- 1 or 2 depending on team

        returns boolean of piece placement success
        """
        print(x)
        print(color)
        for y in range(0, 6):
            if self.board[x][y] == 0:
                self.board[x][y] = color
                return True
        return False

    def test_win(self):
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
            v_win_status = self.v_test(x)
            if v_win_status != 0:
                return v_win_status

        """Horizontal Testing"""
        for y in range(0, 6):
            h_win_status = self.h_test(y)
            if h_win_status != 0:
                return h_win_status

        """Diagonal Testing and Draw Testing"""
        draw = True
        for x in range(0, 7):
            for y in range(0, 6):
                if self.board[x][y] == 0:
                    draw = False
                d_win_status = self.diag_test(x, y)
                if d_win_status != 0:
                    return d_win_status

        if draw:
            return 3

        return 0

    def h_test(self, y):
        """
        Tests for horizontal wins

        returns win status
        """
        last_space = 0
        conc_spaces = 1
        for x in range(0, 7):
            if self.board[x][y] == last_space:
                conc_spaces += 1
            else:
                conc_spaces = 1
                last_space = self.board[x][y]
            if conc_spaces == 4 and last_space != 0:
                return last_space
        return 0

    def v_test(self, x):
        """
        Tests for vertical wins

        returns win status
        """
        last_space = 0
        conc_spaces = 1
        for y in range(0, 6):
            if self.board[x][y] == last_space:
                conc_spaces += 1
            else:
                conc_spaces = 1
                last_space = self.board[x][y]
            if conc_spaces == 4 and last_space != 0:
                return last_space
        return 0

    def diag_test(self, x, y):
        """
        Tests for diagonal wins

        returns win status
        """
        conc_spaces_up = 1
        conc_spaces_down = 1
        last_space_up = self.board[x][y]
        last_space_down = self.board[x][y]
        if self.board[x][y] == 0:
            return 0
        for i in range(1, 4):
            if (x + i) < 7:
                cont = False
                if y + i < 6 and self.board[x + i][y + i] == last_space_up:
                    conc_spaces_up += 1
                    cont = True
                if y - i >= 0 and self.board[x + i][y - i] == last_space_down:
                    conc_spaces_down += 1
                    cont = True
                if not cont:
                    return 0
                if conc_spaces_up == 4:
                    return last_space_up
                if conc_spaces_down == 4:
                    return last_space_down
        return 0



