package betternet.server;

import betternet.Main;
import betternet.data.Board;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final Board board;
    private boolean turn;
    private final ServerSocket socket;
    private final List<Player> players;

    public Server(int port) throws IOException {
        socket = new ServerSocket(port);
        socket.setSoTimeout(5000);
        players = new ArrayList<Player>();
        board = new Board();
        turn = true;
    }

    public boolean getTurn() {
        return turn;
    }

    public Board getBoard() {
        return board;
    }

    public void run() {
        board.render();
        while (!interrupted()) {
            try {
                players.removeIf(p -> !p.isConnected());
                if (players.size() > 1) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }
                System.out.println("Waiting for players " + players.size() + " Connected.");
                Socket connection = socket.accept();
                Player p = new Player(this, connection, players.isEmpty());
                players.add(p); // possible Double team bug... O & O instead of O & X


            } catch (SocketTimeoutException ignore) {
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    public void move(boolean type, int x, int y) throws InterruptedException {
        if (players.size() != 2) {
            System.out.println("Not Enough Players!");
            return;
        }
        if (type != turn) {
            System.out.println("Player Attempted to cheat!");
            return;
        }

        if (board.isEmpty(x, y)) {
            Main.clear();
            board.set(x, y, type);
            turn = !turn;
            players.forEach((i) -> {
                try {
                    i.sendBoard(board, turn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("turn: " + turn);
            board.render();
            if (board.getWinner() != null) {
                players.forEach((i) -> {
                    try {
                        i.sendWinner(board.getWinner());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                Thread.sleep(5000);
                System.exit(0);
            }
        }
    }
}
