package betternet.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class Board {
    public static final Boolean X = true;
    public static final Boolean O = false;
    public static final Boardline[] winningLines = {
            new Boardline(0,0, 1,0, 2,0),
            new Boardline(0,1, 1,1, 2,1),
            new Boardline(0,2, 1,2, 2,2),

            new Boardline(0,0, 0,1, 0,2),
            new Boardline(1,0, 1,1, 1,2),
            new Boardline(2,0, 2,1, 2,2),

            new Boardline(0,0,  1,1, 2,2),
            new Boardline(2,0, 1,1, 0,2),


    };
    private final Boolean[] board;

    public Board() {
        this(new Boolean[9]);
    }
    public Board(Boolean[] board) {
        this.board = board;
    }

    public void set(int x, int y, Boolean b) {
        board[map(x, y)] = b;
    }

    public Boolean get(int x, int y) {
        return board[map(x, y)];
    }

    private int map(int x, int y) {
        return (3 * y) + x;
    }

    public void clear() {
        Arrays.fill(board, null);
    }

    public void setX(int x, int y) {
        set(x, y, X);
    }

    public void setO(int x, int y) {
        set(x, y, O);
    }

    public boolean isO(int x, int y) {
        Boolean b = get(x, y);
        return b != null && b == O;
    }

    public boolean isX(int x, int y) {
        Boolean b = get(x, y);
        return b != null && b == X;
    }

    public boolean isEmpty(int x, int y) {
        return get(x,y) == null;
    }

    public char render(int x, int y){
        return isEmpty(x,y) ? '-' : isX(x,y) ? 'X' : 'O';
    }

    public void render(){
        StringBuilder row = new StringBuilder();

        for(int i = 0 ; i<3 ; i++){

            for(int j = 0 ; j<3 ; j++){
              row.append(render(j,i));

            }
            System.out.println(row);
            row.setLength(0);
        }

    }

    public void write(DataOutputStream dos) throws IOException {
        for(int i = 0; i<board.length ; i++){
            dos.writeByte(board[i] == null ? 0 : board[i] ? 1 : 2);
        }
    }

    public static Board read(DataInputStream din) throws IOException {
        Boolean[] board = new Boolean[9];
        for(int i = 0; i<board.length ; i++){
            byte b = din.readByte();
            if(b==0){
                continue;
            }
            board[i] = b == 1;
        }
        return new Board(board);
    }

    public Boolean getWinner(){
        for (Boardline i : winningLines){
            Boolean w = i.getWinner(this);
            if (w != null){
                return w;
            }
        }
        return null;
    }

}
