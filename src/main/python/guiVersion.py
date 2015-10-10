__author__ = 'Elijah'

import sys
import connect4
from PyQt4 import QtCore, QtGui


class GUIBoard(QtGui.QWidget):
    vBoard = connect4.VirtualBoard()

    turn = 1
    window = None

    def __init__(self, window):
        super(GUIBoard, self).__init__()
        self.window = window

    def reset_board(self):
        self.vBoard.reset_board()
        self.turn = 1

    def draw_board(self, qp):
        color = QtGui.QColor(0, 0, 0)
        color.setNamedColor('#d4d4d4')
        qp.setPen(color)

        qp.setBrush(QtGui.QColor(100, 100, 100))
        rect = QtCore.QRect(self.geometry())
        qp.drawRect(rect)

        pen = QtGui.QPen(QtCore.Qt.black, 1, QtCore.Qt.SolidLine)

        left_x = rect.topLeft().x()
        bottom_y = rect.bottomRight().y()
        box_width = rect.width()
        box_height = rect.height()
        x_incr = box_width / 7.0
        y_incr = box_height / 6.0

        for y in range(1, 6):
            qp.setPen(pen)
            y_height = bottom_y - y_incr * y
            qp.drawLine(left_x, y_height, left_x + box_width, y_height)

        for x in range(1, 7):
            qp.setPen(pen)
            x_dist = left_x + x_incr * x
            qp.drawLine(x_dist, bottom_y, x_dist, bottom_y - box_height)

    @QtCore.pyqtSlot(int)
    def takeTurn(self):
        sender = self.sender()
        buttons = self.window.buttons
        for indx in range(0, 7):
            if buttons[indx] is sender:
                if self.vBoard.place_piece(indx, self.turn):
                    print(self.vBoard.board)
                    if self.turn == 1:
                        self.turn = 2
                    else:
                        self.turn = 1
                    win = self.vBoard.test_win()
                    if win == 1:
                        print("X Wins!")
                    elif win == 2:
                        print("O Wins!")
                    elif win == 3:
                        print("It's a draw!")
                else:
                    """TODO: Failed to place piece message."""


class C4GUIWindow(QtGui.QWidget):
    guiboard = None
    buttons = [0, 0, 0, 0, 0, 0, 0]

    def __init__(self):
        super(C4GUIWindow, self).__init__()
        self.initUI()

    def initUI(self):
        grid = QtGui.QGridLayout()
        self.setLayout(grid)

        self.resize(450, 380)
        self.center()
        self.setWindowTitle('Connect4')
        self.show()
        self.guiboard = GUIBoard(self)

        positions = [(i, j) for i in range(3) for j in range(7)]

        for p in positions:
            if p == (0, 0):
                grid.addWidget(QtGui.QPushButton('Button', self), p[0], p[1], 1, 7)
            elif p == (1, 0):
                grid.addWidget(self.guiboard, p[0], p[1], 1, 7)
            elif p[0] == 2:
                button = QtGui.QPushButton('&Place', self)
                self.buttons[p[1]] = button
                grid.addWidget(button, *p)
                button.setStyleSheet("background-color: red")
                button.clicked.connect(self.guiboard.takeTurn)
                self.buttons[p[1]] = button

    def paintEvent(self, event):
        qp = QtGui.QPainter()
        qp.begin(self)
        self.guiboard.draw_board(qp)
        qp.end()

    def center(self):
        screen = QtGui.QDesktopWidget().screenGeometry()
        size = self.geometry()
        self.move((screen.width() - size.width()) / 2,
                  (screen.height() - size.height()) / 2)


def main():
    app = QtGui.QApplication(sys.argv)
    window = C4GUIWindow()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
