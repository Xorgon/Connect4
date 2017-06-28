# Connect4
This project is Connect4 in a variety of languages. Versions currently available are as follows:
- Java, console version
- Java, Bukkit plugin version
- Python, console version
- Python, GUI version

You can download the Python GUI version <a href="http://xorg.us/Connect4.zip">here</a>. Simply extract the zip and run connect4.bat. Alternatively run guiVersion.py from a terminal.


### Bukkit Plugin Instructions

The plugin is easy to use. Place the Connect4 jar in the plugins folder and launch the server.

First build a board. The board must be 6 wide and 5 high with a row of blocks beneath it with buttons facing the players. As an example, see the image below.

To use `/c4` commands the player must have the permission `c4.admin`. Ops have this by default.

Create a board using `/c4 add <board name>`.

Select the board region using WorldEdit and then set the region for your board using `/c4 set region <board name>`. The selection should look like this:
![Example board](http://xorg.us/iv43YA.png)

Face the board as the player in the picture is. Set the board facing direction using `/c4 set face <board name>`. This determines the direction that the buttons face.

The board will automatically load and be available for play. To start a game simply press one of the buttons. Two players are required to start a game.

If for any reason you wish to reset a board you can use `/c4 reset <board name>`.

The config can be reloaded using `/c4 reload <board name>`.