package me.xorgon.connect4.util;

/**
 * Created by Elijah on 14/08/2015.
 */
public class VirtualBoard {

    private SpaceType[][] spaces = new SpaceType[7][6];

    public void initialize() {
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                spaces[x][y] = SpaceType.EMPTY;
            }
        }
    }

    /**
     * Place a piece on the board.
     *
     * @param x     The column number.
     * @param color The color to set, cannot be EMPTY.
     */
    public void placePiece(int x, SpaceType color) {
        for (int y = 0; y < 6; y++) {
            if (spaces[x][y] == SpaceType.EMPTY) {
                spaces[x][y] = color;
                return;
            }
        }
    }

    public WinStatus testWin() {
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
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                WinStatus dWinStatus = diagTest(x, y);
                if (dWinStatus != WinStatus.NONE) {
                    return dWinStatus;
                }
            }
        }
        return WinStatus.NONE;
    }

    public WinStatus diagTest(int x, int y) {
        int concSpacesUp = 1;
        int concSpacesDown = 1;
        SpaceType lastSpaceUp = spaces[x][y];
        SpaceType lastSpaceDown = spaces[x][y];
        for (int i = 1; i < 4; i++) {
            if (x + i < 7) {
                boolean cont = false;
                if (y + i < 6 && spaces[x + i][y + i] == lastSpaceUp) {
                    concSpacesUp++;
                    cont = true;
                }
                if (y - i >= 0 && spaces[x + i][y - i] == lastSpaceDown) {
                    concSpacesDown++;
                    cont = true;
                }
                if (!cont) {
                    return WinStatus.NONE;
                }
                if (concSpacesUp == 4) {
                    return spaceTypeToWinStatus(lastSpaceUp);
                }
                if (concSpacesDown == 4) {
                    return spaceTypeToWinStatus(lastSpaceDown);
                }
            }
        }
        return WinStatus.NONE;
    }

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


    public enum SpaceType {
        RED,
        BLUE,
        EMPTY
    }

    public enum WinStatus {
        RED,
        BLUE,
        NONE
    }

    public WinStatus spaceTypeToWinStatus(SpaceType s) {
        switch (s) {
            case RED:
                return WinStatus.RED;
            case BLUE:
                return WinStatus.BLUE;
            case EMPTY:
                return WinStatus.NONE;
            default:
                return WinStatus.NONE;
        }
    }

    public SpaceType[][] getSpaces() {
        return spaces;
    }
}
