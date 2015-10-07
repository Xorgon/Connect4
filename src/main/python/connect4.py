# -*- coding: utf-8 -*-
import numpy

board = numpy.zeros((7,6))

def placePiece(x, color):
    """
    Places a piece on the board.
    
    x -> Column Number
    color -> 1 or 2 depending on team
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