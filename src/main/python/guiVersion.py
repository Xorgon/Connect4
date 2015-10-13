import sys
import connect4
import math
from PyQt4 import QtCore, QtGui
import c4FileIO


class Piece():
    x = None
    y = None
    color = None
    guiboard = None

    def __init__(self, x, y, color, guiboard):
        self.x = x
        self.y = y
        self.color = color
        self.guiboard = guiboard

    def get_rect(self):
        gb = self.guiboard.geometry()
        b_width = gb.width()
        b_height = gb.height()
        p_width = b_width / 7.0
        p_height = math.ceil(b_height / 6.0)
        t_left = gb.topLeft()
        x = t_left.x() + p_width * self.x
        y = t_left.y() + b_height - p_height * (self.y + 1)
        return QtCore.QRect(x, y, p_width, p_height)


class GUIBoard(QtGui.QWidget):
    vBoard = connect4.VirtualBoard()
    pieces = []

    qp = None

    turn = 1
    window = None

    def __init__(self, window):
        super(GUIBoard, self).__init__()
        self.window = window

    def reset_board(self):
        self.vBoard.reset_board()
        self.turn = 1
        self.pieces = []
        self.window.set_buttons_active(True)
        self.window.text_area.setText("Red Turn")

    def draw_board(self, qp):
        self.qp = qp
        color = QtGui.QColor(100, 100, 100)
        pen = QtGui.QPen(color)
        pen.setStyle(QtCore.Qt.NoPen)
        qp.setPen(pen)

        brush = QtGui.QBrush(color)
        qp.setBrush(brush)

        qp.drawRect(self.geometry())

        pen = QtGui.QPen(QtCore.Qt.black, 2, QtCore.Qt.SolidLine)

        left_x = self.geometry().topLeft().x()
        bottom_y = self.geometry().bottomRight().y()
        box_width = self.geometry().width()
        box_height = self.geometry().height()
        x_incr = box_width / 7.0
        y_incr = box_height / 6.0

        self.draw_pieces(qp)

        for y in range(1, 6):
            qp.setPen(pen)
            y_height = bottom_y - y_incr * y
            qp.drawLine(left_x, y_height, left_x + box_width, y_height)

        for x in range(1, 7):
            qp.setPen(pen)
            x_dist = left_x + x_incr * x
            qp.drawLine(x_dist, bottom_y, x_dist, bottom_y - box_height)

    def draw_pieces(self, qp):
        qp.setPen(QtCore.Qt.NoPen)
        self.window.update()
        for p in self.pieces:
            brush = QtGui.QBrush(p.color)
            qp.setBrush(brush)
            qp.drawRect(p.get_rect())

    def add_piece(self, p_x, p_y):
        color = None
        if self.turn == 1:
            color = QtGui.QColor(255, 000, 000)
        else:
            color = QtGui.QColor(000, 000, 255)

        piece = Piece(p_x, p_y, color, self)

        self.pieces.append(piece)

    @QtCore.pyqtSlot(int)
    def take_turn(self):
        sender = self.sender()
        buttons = self.window.buttons
        for indx in range(0, 7):
            if buttons[indx] is sender:
                piece = self.vBoard.place_piece(indx, self.turn)
                if piece:
                    self.add_piece(piece[0], piece[1])
                    if self.turn == 1:
                        self.turn = 2
                        self.window.text_area.setText("Blue Turn")
                        for b in buttons:
                            b.setStyleSheet("background-color: blue")
                    else:
                        self.turn = 1
                        self.window.text_area.setText("Red Turn")
                        for b in buttons:
                            b.setStyleSheet("background-color: red")
                    win = self.vBoard.test_win()
                    if win != 0:
                        if win == 1:
                            self.window.text_area.setText("Red Wins!")
                            self.window.fileio.red_wins += 1
                        elif win == 2:
                            self.window.text_area.setText("Blue Wins!")
                            self.window.fileio.blue_wins += 1
                        elif win == 3:
                            self.window.text_area.setText("It's a draw!")
                        self.window.fileio.serialize_file()
                        self.window.set_buttons_active(False)
                        self.window.check_play_again()
                else:
                    self.window.text_area.setText("You can't place a piece there.")
        self.window.update()


class C4GUIWindow(QtGui.QWidget):
    guiboard = None
    buttons = [0, 0, 0, 0, 0, 0, 0]
    text_area = None
    fileio = None

    def __init__(self):
        super(C4GUIWindow, self).__init__()
        self.fileio = c4FileIO.C4FileIO()
        self.initUI()

    def initUI(self):
        grid = QtGui.QGridLayout()
        self.setLayout(grid)

        self.resize(450, 380)
        self.center()
        self.setStyleSheet("QWidget{background-color: black; color: white;} "
                           "QPushButton { background-color: grey; color: black;}")
        self.setWindowTitle('Connect4')
        self.show()
        self.guiboard = GUIBoard(self)

        positions = [(i, j) for i in range(3) for j in range(7)]

        for p in positions:
            if p == (0, 0):
                self.text_area = QtGui.QLabel()
                height = self.geometry().height()
                self.text_area.setFixedHeight(height / 9)
                style_string = "background-color: white; color: black; font-size:" + str(int(height / 9) - 10) + "px"
                self.text_area.setStyleSheet(style_string)
                self.text_area.setText("Red Turn")
                self.text_area.setAlignment(QtCore.Qt.AlignCenter)
                grid.addWidget(self.text_area, p[0], p[1], 1, 7)
            elif p == (1, 0):
                grid.addWidget(self.guiboard, p[0], p[1], 1, 7)
            elif p[0] == 2:
                button = QtGui.QPushButton('', self)
                self.buttons[p[1]] = button
                grid.addWidget(button, *p)
                button.setStyleSheet("background-color: red")
                button.clicked.connect(self.guiboard.take_turn)
                self.buttons[p[1]] = button

    def paintEvent(self, event):
        qp = QtGui.QPainter()
        qp.begin(self)
        self.guiboard.update()
        self.guiboard.draw_board(qp)
        qp.end()

    def set_rel_font(self, factor):
        height = self.geometry().height()
        self.text_area.setFixedHeight(height / 9)
        size = (height / 9 - height / 20) / factor
        style_string = "background-color: white; color: black; font-size:" + str(int(size)) + "px"
        self.text_area.setStyleSheet(style_string)

    def center(self):
        screen = QtGui.QDesktopWidget().screenGeometry()
        size = self.geometry()
        self.move((screen.width() - size.width()) / 2,
                  (screen.height() - size.height()) / 2)

    def set_buttons_active(self, active):
        if active:
            for b in self.buttons:
                b.setStyleSheet("background-color: red")
                b.clicked.disconnect(self.show_wins)
                b.clicked.connect(self.guiboard.take_turn)
        else:
            for b in self.buttons:
                b.setStyleSheet("background-color: grey")
                b.clicked.disconnect(self.guiboard.take_turn)
                b.clicked.connect(self.show_wins)

    @QtCore.pyqtSlot()
    def show_wins(self):
        self.set_rel_font(2)
        self.text_area.setText("Red wins: " + str(self.fileio.red_wins) + "\n" +
                               "Blue wins: " + str(self.fileio.blue_wins))

    def check_play_again(self):
        box = QtGui.QMessageBox()
        box.setStyleSheet("background-color: grey")
        reply = box.question(self, "", "Play again?", QtGui.QMessageBox.Yes, QtGui.QMessageBox.No)

        if reply == QtGui.QMessageBox.Yes:
            self.guiboard.reset_board()
            self.set_rel_font(1)


def main():
    app = QtGui.QApplication(sys.argv)
    window = C4GUIWindow()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
