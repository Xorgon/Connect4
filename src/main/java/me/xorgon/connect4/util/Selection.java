package me.xorgon.connect4.util;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


/**
 * Connect4 selection class.
 */
public class Selection {

    private Player player;
    private Vector point1;
    private Vector point2;

    public Selection(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Vector getPoint1() {
        return point1;
    }

    public void setPoint1(Vector point1) {
        this.point1 = point1;
    }

    public Vector getPoint2() {
        return point2;
    }

    public void setPoint2(Vector point2) {
        this.point2 = point2;
    }

    public Vector getMax(){
        return Vector.getMaximum(point1, point2);
    }

    public Vector getMin(){
        return Vector.getMinimum(point1, point2);
    }
}