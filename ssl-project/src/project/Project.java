package project;

import client.Client;
import server.Server;

/**
 *
 * @author
 */
public class Project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("client")) {
            Client client = new Client();
            client.main();
        } else if (args[0].equals("server")) {
            Server server = new Server();
            server.main();
        } else {
            System.out.println("unrecognized argument\nPlease give an arugment of: server or client");
        }
    }
}
