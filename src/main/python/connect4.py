# -*- coding: utf-8 -*-
import numpy
import os

board = numpy.zeros((7,6))

def c4():
    board = numpy.zeros((7,6))
    outputBoard()
    takeTurn()

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
    

    """Vertical Testing"""
    for x in range(0,7):
        vWinStatus = vTest(x)
        if vWinStatus != 0:
            return vWinStatus

    """Horizontal Testing"""    
    for y in range(0,6):
        hWinStatus = hTest(y)
        if hWinStatus != 0:
            return hWinStatus
    
    """Diagonal Testing and Draw Testing"""
    draw = True
    for x in range(0,7):
        for y in range(0,6):
            if board[x][y] == 0:
                draw = False
            dWinStatus = diagTest(x,y)
            if dWinStatus != 0:
                return dWinStatus
                
    if draw:
        return 3
    
    return 0
      
    
def hTest(y):
    lastSpace = 0
    concSpaces = 1
    for x in range(0,7):
        if board[x][y] == lastSpace:
            concSpaces += 1
        else:
            concSpaces = 1
            lastSpace = board[x][y]
        if concSpaces == 4 and lastSpace != 0:
            return lastSpace
    return 0
    
    
def vTest(x):
    lastSpace = 0
    concSpaces = 1
    for y in range(0,6):
        if board[x][y] == lastSpace:
            concSpaces += 1
        else:
            concSpaces = 1
            lastSpace = board[x][y]
        if concSpaces == 4 and lastSpace != 0:
            return lastSpace
    return 0


def diagTest(x, y):
    concSpacesUp = 1
    concSpacesDown = 1
    lastSpaceUp = board[x][y]
    lastSpaceDown = board[x][y]
    if board[x][y] == 0:
        return 0
    for i in range(1,4):
        if (x + i) < 7:
            cont = False
            if (y + i < 6 and board[x+i][y+i] == lastSpaceUp):
                concSpacesUp += 1
                cont = True
            if (y - i >= 0 and board[x+i][y-i] == lastSpaceDown):
                concSpacesDown += 1
                cont = True
            if cont == False:
                return 0
            if concSpacesUp == 4:
                return lastSpaceUp
            if concSpacesDown == 4:
                return lastSpaceDown
    return 0


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
            if inpt == "stop":
                return
            isInteger = True            
            try:
                slot = int(inpt)
            except ValueError:
                isInteger = False
                print("Invalid input, slot must be a number between 0 and 6")
            if isInteger:
                if slot > 6 or slot < 0:
                    print("Invalid input, slot must be a number between 0 and 6")
                elif placePiece(slot, turn):
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
    elif win == 2:
        print("O Wins!")
    elif win == 3:
        print("It's a draw!")
            
    

def outputBoard():
    """Outputs the board to the console."""    
    os.system('cls' if os.name == 'nt' else 'clear')
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
                 
                    