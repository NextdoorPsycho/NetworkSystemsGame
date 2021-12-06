package betternet.data;

public class Boardline {
    private final int[] line;

    public Boardline(int... line) {
        if (line.length != 6) {
            throw new RuntimeException("MUST BE 6");
        }
        this.line = line;
    }

    public Boolean getWinner(Board board) {
        return (board.isX(line[0], line[1]) && board.isX(line[2], line[3]) && board.isX(line[4], line[5])) ? Board.X : (board.isO(line[0], line[1]) && board.isO(line[2], line[3]) && board.isO(line[4], line[5])) ? Board.O : null;
    }
}
