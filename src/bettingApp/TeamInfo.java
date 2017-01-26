package bettingApp;

import java.util.*;

/**
 * Created by User on 3/31/2016.
 */
public class TeamInfo {
    private String name;
    private double winningPerc;
    private float wins;
    private float losses;
    private char conference;
    //this map contains a map of this team to each other team keeping track of winning/losing margins
    private HashMap<String, Integer> marginRecords;

    public TeamInfo(String name, float wins, float losses, char conference) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.conference = conference;
        this.winningPerc = Math.floor((wins / (wins+losses)) * 100) / 100;
        marginRecords = new HashMap<String, Integer>();
    }

    public String getName() {
        return name;
    }

    public char getConference() {
        return conference;
    }

    public void setConference(char conference) {
        this.conference = conference;
    }

    public double getWinningPerc() {
        return winningPerc;
    }

    public float getWins() {
        return wins;
    }

    public void setWins(float wins) {
        this.wins = wins;
        this.winningPerc = Math.floor((this.wins / (this.wins+this.losses)) * 100) / 100;
    }

    public float getLosses() {
        return losses;
    }

    public void setLosses(float losses) {
        this.losses = losses;
        this.winningPerc = Math.floor((this.wins / (this.wins+this.losses)) * 100) / 100;
    }
}
