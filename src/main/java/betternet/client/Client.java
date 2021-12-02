package betternet.client;

import betternet.Main;
import betternet.data.Board;
import betternet.data.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client extends Thread {
    private final Socket connection;
    private Board board;
    private boolean turn;
    private final DataInputStream in;
    private final DataOutputStream out;
    private boolean type;

    public Client(String address, int port) throws IOException {
        connection = new Socket(address, port);
        in = new DataInputStream(connection.getInputStream());
        out = new DataOutputStream(connection.getOutputStream());
        board = new Board();
        start();
        Scanner s = new Scanner(System.in);
        while (!Thread.interrupted()) {
            try {
                input(s.nextLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void input(String s) throws IOException {
        String[] coords = s.split("\\Q,\\E");
        int x = Integer.parseInt(coords[0].trim());
        int y = Integer.parseInt(coords[1].trim());

        move(x, y);
    }

    public void handle(PacketType type) throws IOException, InterruptedException {
        System.out.println("Recieved Type: " + type);
        switch (type) {
            case SERVER_BOARD -> {
                Main.clear();
                turn = in.readBoolean();
                board = Board.read(in);
                board.render();
                System.out.println(turn == this.type ? "Its your turn, To play enter a Coordinate ie: 0,2" : "Waiting for opponent");
            }
            case SERVER_WINNER ->{
                System.out.println(in.readBoolean() == this.type ? "\n" +
                        " __   __  _______  __   __    _     _  ___   __    _  __  \n" +
                        "|  | |  ||       ||  | |  |  | | _ | ||   | |  |  | ||  | \n" +
                        "|  |_|  ||   _   ||  | |  |  | || || ||   | |   |_| ||  | \n" +
                        "|       ||  | |  ||  |_|  |  |       ||   | |       ||  | \n" +
                        "|_     _||  |_|  ||       |  |       ||   | |  _    ||__| \n" +
                        "  |   |  |       ||       |  |   _   ||   | | | |   | __  \n" +
                        "  |___|  |_______||_______|  |__| |__||___| |_|  |__||__| \n" : "\n" +
                        "   _____      _     ______          _            _ \n" +
                        "  / ____|    | |   |  ____|        | |          | |\n" +
                        " | |  __  ___| |_  | |__ _   _  ___| | _____  __| |\n" +
                        " | | |_ |/ _ \\ __| |  __| | | |/ __| |/ / _ \\/ _` |\n" +
                        " | |__| |  __/ |_  | |  | |_| | (__|   <  __/ (_| |\n" +
                        "  \\_____|\\___|\\__| |_|   \\__,_|\\___|_|\\_\\___|\\__,_|\n" +
                        "                                                   \n" +
                        "                                                   \n");
                Thread.sleep(5000);
                System.exit(0);

            }
            case SERVER_ASSIGN -> {
                this.type = in.readBoolean();
                System.out.println("You are player: " + ((this.type == Board.X ? "X" : "O")));

            }
        }

    }

    public void move(int x, int y) throws IOException {
        if (turn != type) {
            System.out.println("ITS NOT YOUR TURN");
            return;
        }
        if (x > 2 || x < 0 || y > 2 || y < 0) {
            System.out.println("0,0 to 2,2 are your limits!");
            return;
        }
        if (board.isEmpty(x, y)) {
            System.out.println("Moving: " + x + " " + y);

            out.writeByte(PacketType.CLIENT_MOVE.ordinal());
            out.writeByte(x);
            out.writeByte(y);
            out.flush();

        } else {
            System.out.println("That's not a valid move... Learn to play.");

        }

    }

    public void run() {
        while (connection.isConnected() && !interrupted()) {
            try {
                handle(PacketType.values()[in.readByte()]);
            } catch (SocketTimeoutException ignored) {
                System.out.println("Timeout");

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
