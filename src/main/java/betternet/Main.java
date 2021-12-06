package betternet;//Brian Fopiano


import betternet.client.Client;
import betternet.server.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, String> config = new HashMap<String, String>();
        Set<String> flags = new HashSet<>();


        for (String i : args) {
            if (i.contains("=")) {
                config.put(i.split("\\Q=\\E")[0], i.split("\\Q=\\E")[1]);
            } else {
                flags.add(i);
            }
        }

        if (flags.contains("-server")) {
            Server server = new Server(Integer.parseInt(config.get("-port")));
            server.start();

        } else if (flags.contains("-client")) {
            new Client(config.get("-address"), Integer.parseInt(config.get("-port")));


        } else {
            System.out.println("For Servers, 'java -jar JarfileHere.jar -server -port=8123'");
            System.out.println("For Clients, 'java -jar JarfileHere.jar -client -address=localhost -port=8123'");

        }

    }

    public static void clear() {
        for (int i = 0; i < 64; i++) {
            System.out.println();
        }
    }
}