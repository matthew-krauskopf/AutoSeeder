package MyUtils;

public class BracketData {
    public String [] entrants;
    public int [][] conflicts;

    public BracketData(String [] e, int [][] c) {
        entrants = e;
        conflicts = c;
    }

    public BracketData(String [] e) {
        entrants = e;
        conflicts = new int [0][0];
    }
};