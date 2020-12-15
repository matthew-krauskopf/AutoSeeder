package Backend;

public class Match {
    public String winner;
    public String loser;
    public String date;
    public int tourney_ID;

    public Match(String w, String l, String d, int id) {
        winner = w;
        loser = l;
        date = d;
        tourney_ID = id;
    }
};