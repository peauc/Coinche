package eu.epitech.jcoinche.jcoincheclient.client;

import eu.epitech.jcoinche.jcoincheclient.client.networking.Connection;

import java.net.ConnectException;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("HelloClient");
        Connection connect = new Connection();
        try {
            connect.connect();
        }
        catch (ConnectException e) {
            System.exit(84);
        }
    }
}
