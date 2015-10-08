# -*- coding: utf-8 -*-
import numpy

board = numpy.zeros((7,6))

def placePiece(x, color):
    """
    Places a piece on the board.
    
    x -> Column Number
    color -> 1 or 2 depending on team
    
    returns boolean of piece placement success
    """
    print(x)
    print(color)
    for y in range(0,6):
        if board[x][y] == 0:
            print(y)
            board[x][y] = color
            return True
    return False
        
def testWin():
    notDraw = False
    for x in range(0,7):
        for y in range(0,6):
            if board[x][y] == 0:
                notDraw = True
            dWinStatus = diagTest(x,y)
            if dWinStatus != 0:
                return dWinStatus
        """        
    for (int x = 0; x < 7; x++) {
            WinStatus vWinStatus = vTest(x);
            if (vWinStatus != WinStatus.NONE) {
                return vWinStatus;
            }
        }
        for (int y = 0; y < 6; y++) {
            WinStatus hWinStatus = hTest(y);
            if (hWinStatus != WinStatus.NONE) {
                return hWinStatus;
            }
        }
        boolean notDraw = false;
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                if (spaces[x][y] == SpaceType.EMPTY){
                    notDraw = true;
                }
                WinStatus dWinStatus = diagTest(x, y);
                if (dWinStatus != WinStatus.NONE) {
                    return dWinStatus;
                }
            }
        }
        if(!notDraw){
            return WinStatus.DRAW;
        }
        return WinStatus.NONE;
        """         

def diagTest(x, y):
    concSpacesUp = 1
    concSpacesDown = 1
    lastSpaceUp = board[x][y]
    lastSpaceDown = board[x][y]
    for i in range(1,4):
        if (x + i) < 7:
            cont = False
            if (y + i < 6 and board[x+i][y+i] == lastSpaceUp):
                concSpacesUp += 1
                cont = True
            if (y - i >= 0 and board[x+i][y-i] == lastSpaceDown):
                concSpacesDown += 1
                cont = True
            if cont is False:
                return 0
            if concSpacesUp == 4:
                return lastSpaceUp
            if concSpacesDown == 4:
                return lastSpaceDown
        return 0

"""
    public WinStatus hTest(int y) {
        SpaceType lastSpace = SpaceType.EMPTY;
        int concSpaces = 1;
        for (int x = 0; x < 7; x++) {
            if (spaces[x][y] == lastSpace) {
                concSpaces++;
            } else {
                concSpaces = 1;
                lastSpace = spaces[x][y];
            }
            if (concSpaces == 4 && lastSpace != SpaceType.EMPTY) {
                return spaceTypeToWinStatus(lastSpace);
            }
        }
        return WinStatus.NONE;
    }

    public WinStatus vTest(int x) {
        SpaceType lastSpace = SpaceType.EMPTY;
        int concSpaces = 1;
        for (int y = 0; y < 6; y++) {
            if (spaces[x][y] == lastSpace) {
                concSpaces++;
            } else {
                concSpaces = 1;
                lastSpace = spaces[x][y];
            }
            if (concSpaces == 4 && lastSpace != SpaceType.EMPTY) {
                return spaceTypeToWinStatus(lastSpace);
            }
        }
        return WinStatus.NONE;
    }
        
"""


def takeTurn():
    win = 0
    turn = 1
    while win == 0:
        slot = None
        if turn == 1:
            print("X Turn.")
        else:
            print("O Turn.")
        notPlaced = True
        while notPlaced:
            inpt = input()
            isInteger = True            
            try:
                slot = int(inpt)
            except ValueError:
                isInteger = False
                print("Invalid input, slot must be a number between 0 and 6")
            if isInteger:
                if slot > 6 or slot < 0:
                    print("Invalid input, slot must be a number between 0 and 6")
                if placePiece(slot, turn):
                    notPlaced = False
                else:
                    print("You can't go there.")
        outputBoard()
        if turn == 1:
            turn = 2
        else:
            turn = 1
        win = testWin()
    if win == 1:
        print("X Wins!")
    else:
        print("O Wins!")
            
    

def outputBoard():
    """Outputs the board to the console."""    
    print("= 0 1 2 3 4 5 6 =")
    for ny in range(0,6):
        y = 5-ny
        line = "| "
        for x in range(0,7):
            if board[x][y] == 1:
                line = line + "X "
            elif board[x][y] == 2:
                line = line + "O "
            else:
                line = line + "  "
        line = line + "|"
        print(line)   
    print("= 0 1 2 3 4 5 6 =")         
                 
                    