package bettingApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

/* This application makes stock predictions based on historical trends
* @Author Tong Zou
* */

public class BettingApp {

    //This is the initial teams with NBA records from January. The reason I don't start from the beginning
    //of the NBA season is because a few months is required to get valuable data as to how good each team is
    //and their respective standing in the conferences
    private static final String INITIAL_RECORDS = "initialrecords.csv";

    //these are the calculated win/loss records for each team from Jan - Apr
    private static final String TEAM_RECORDS = "records.csv";

    private static HashMap<String, TeamInfo> teamData = new HashMap<String, TeamInfo>();

    //these are based off of Bovada's betting system. Usually -500 on a team means for every $500 you bet, you make $100
    //So I will bet $500 each time and pretend that every time I lose, I lose $500 and every time I win, I win $100
    //This also means that in order to be profitable, I have to win my bet 5x more than I lose my bets
    private static int AMOUNT_ON_LOSS = 500;
    private static int AMOUNT_ON_WIN = 100;

    public static void main(String args[]) {
        try {

            //first init the teamData map
            FileInputStream fstream = new FileInputStream(INITIAL_RECORDS);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            br.readLine();
            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                String[] str = strLine.split(",");
                String teamName = str[0];
                float wins = Float.parseFloat(str[1]);
                float losses = Float.parseFloat(str[2]);
                char conference = str[3].charAt(0);
                teamData.put(teamName, new TeamInfo(teamName, wins, losses, conference));
            }

            br.close();

            //Now we are reading the records and updating the map
            FileInputStream fstream2 = new FileInputStream(TEAM_RECORDS);
            FileWriter fstream_w = new FileWriter("bet-results.txt");
            DataInputStream in2 = new DataInputStream(fstream2);
            BufferedWriter out = new BufferedWriter(fstream_w);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
            br2.readLine();
            String strLine2;

            out.write("Betting Results");
            out.newLine();

            int wonBets = 0;
            int lostBets = 0;

            //Read File Line By Line
            while ((strLine2 = br2.readLine()) != null) {
                String[] str = strLine2.split(",");
                String date = str[0];
                String visitorTeam = str[1];
                String homeTeam = str[2];
                String winner = null;
                if (str.length > 3) {
                    winner = str[3];
                }

                //get winning percentage for visitor and home team
                double visitorWinningPerc = teamData.get(visitorTeam).getWinningPerc();
                double homeWinningPerc = teamData.get(homeTeam).getWinningPerc();

                //0.2 or 0.3 gave me the best results here.. the higher up you go the better quality bets but less chances to win
                String favoredTeam = "";
                double favoredPercentage = 0;
                if (visitorWinningPerc - homeWinningPerc > 0.2) {
                    favoredTeam = visitorTeam;
                    favoredPercentage = Math.floor((visitorWinningPerc - homeWinningPerc) * 100) / 100;
                } else if (homeWinningPerc - visitorWinningPerc > 0.2) {
                    favoredTeam = homeTeam;
                    favoredPercentage = Math.floor((homeWinningPerc - visitorWinningPerc) * 100) / 100;
                }

                //Do bet calculations
                //arbitrary, but lets multiply the difference in winning percentage by 2
                //home teams get +5% probability
                //if other team is western team and favored team is eastern then favored team gets -5% chance of winning
                //if other team is eastern team and favored team is western then favored team gets +5% chance of winning
                if (favoredPercentage > 0.2) {
                    String otherTeam = favoredTeam.equals(homeTeam) ? visitorTeam : homeTeam;
                    double baseProbability = favoredPercentage * 2;
                    if (homeTeam.equals(favoredTeam)) {
                        baseProbability += 0.1;
                    } else if (homeTeam.equals(otherTeam)) {
                        baseProbability -= 0.1;
                    }

                    if (teamData.get(favoredTeam).getConference() == 'E' && teamData.get(otherTeam).getConference() == 'W') {
                        baseProbability -= 0.1;
                    } else if (teamData.get(favoredTeam).getConference() == 'W' && teamData.get(otherTeam).getConference() == 'E') {
                        baseProbability += 0.1;
                    }

                    //playing around with numbers... found that 0.7 was the magic number that maximized gains and minimized losses
                    if (winner != null && winner.equals(favoredTeam) && baseProbability > 0.70) {
                        out.write("On " + date + ", Bet on " + favoredTeam + " against " + otherTeam + " and WON");
                        out.newLine();
                        wonBets++;
                    } else if (winner != null && winner.equals(otherTeam) && baseProbability > 0.70){
                        out.write("On " + date + ", Bet on " + favoredTeam + " against " + otherTeam + " and LOST");
                        out.newLine();
                        lostBets++;
                    } else if (winner == null && baseProbability > 0.70) {
                        out.write("On " + date + ", you should bet on " + favoredTeam + " against " + otherTeam + " for the best chance of winning");
                        out.newLine();
                    }
                }

                if (winner != null) {
                    String loser = winner.equals(homeTeam) ? visitorTeam : homeTeam;
                    float winnersWins = teamData.get(winner).getWins();
                    float losersLosses = teamData.get(loser).getLosses();
                    teamData.get(winner).setWins(winnersWins + 1);
                    teamData.get(loser).setLosses(losersLosses + 1);
                }

            }

            for (TeamInfo team : teamData.values()) {
                out.write(team.getName() + ":" + (int) team.getWins() + '-' + (int) team.getLosses());
                out.newLine();
            }

            out.write("WON " + wonBets + " bets and LOST " + lostBets + " bets");
            out.newLine();
            out.write("WON A TOTAL OF " + wonBets*AMOUNT_ON_WIN + " AND LOST A TOTAL OF " + lostBets*AMOUNT_ON_LOSS);
            out.newLine();
            out.write("FOR A TOTAL GAIN/LOSS OF " + (wonBets*AMOUNT_ON_WIN - lostBets*AMOUNT_ON_LOSS));

            br2.close();
            out.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}