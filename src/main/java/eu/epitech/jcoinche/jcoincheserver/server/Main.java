package eu.epitech.jcoinche.jcoincheserver.server;
public class Main {
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
       server.DiscardServer newToto = new server.DiscardServer(port);
        newToto.run();
    }
}
