class C4FileIO():
    red_wins = 0
    blue_wins = 0

    def __init__(self):
        try:
            f = open("c4wins.txt", "r")
            lines = f.readlines()
            f.close()
            if type(lines) == list:
                self.deserialize_file(lines)
        except FileNotFoundError:
            print("File not found, writing new file.")

    def deserialize_file(self, lines):
        for line in lines:
            line = line.strip("\n")
            w = line.split(",")
            if w[0] == "red_wins":
                self.red_wins = int(w[1])
            elif w[0] == "blue_wins":
                self.blue_wins = int(w[1])

    def serialize_file(self):
        f = open("c4wins.txt", "w")
        f_str = ""
        f_str += "red_wins," + str(self.red_wins) + " \n"
        f_str += "blue_wins," + str(self.blue_wins) + " \n"
        print(f_str)
        f.write(f_str)
        f.close()
