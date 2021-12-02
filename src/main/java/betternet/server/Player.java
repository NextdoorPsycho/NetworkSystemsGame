package betternet.server;

import betternet.data.PacketType;
import betternet.data.Board;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player extends Thread {
    private boolean connected;
    private boolean type;
    private final DataInputStream in;
    private final Server server;
    private final DataOutputStream out;
    private final Socket connection;

    public Player(Server server, Socket connection, boolean type) throws IOException {
        connected = true;
        this.connection = connection;
        this.in = new DataInputStream(connection.getInputStream());
        this.out = new DataOutputStream(connection.getOutputStream());
        this.type = type;
        this.server = server;

        start(); // start me daddy
    }

    public void kick() {
        try {
            connected = false;
            connection.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out.writeByte(PacketType.SERVER_ASSIGN.ordinal());
            out.writeBoolean(type);
            sendBoard(server.getBoard(), server.getTurn());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isConnected() && !interrupted()) {
            try {
                switch (PacketType.values()[in.readByte()]) {
                    case CLIENT_MOVE -> {
                        int x = in.readByte();
                        int y = in.readByte();
                        server.move(type, x, y);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                interrupt();
                connected = false;
            }
        }
    }


    public void sendBoard(Board board, boolean turn) throws IOException {
        out.writeByte(PacketType.SERVER_BOARD.ordinal());
        out.writeBoolean(turn);
        board.write(out);
        out.flush();
    }


    public boolean isConnected() {
        return connection.isConnected() && connected;
    }

    public void sendWinner(boolean winner) throws IOException {
        out.writeByte(PacketType.SERVER_WINNER.ordinal());
        out.writeBoolean(winner);
        out.flush();//flush the potty
    }
}
