package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.util.ArrayList;

public class GameManager {
    private ArrayList<Game> gameList = new ArrayList<>();

    private void createNewGame() {
        gameList.add(new Game());
    }
    public Game getFreeGame() {
        for (Game g : gameList) {
            if (!g.isFull())
                return (g);
        }
        return (null);
    }

    public void addPlayerToGame(Player p) {
        Game g;

        if ((g = getFreeGame()) != null) {
            g.addPlayer(p);
        }
        else {
            createNewGame();
            addPlayerToGame(p);
        }
        if (g != null && g.isReady()) {
            g.start();
        }
    }

    public Player findPlayerByCongext(ChannelHandlerContext ctx) {
        for (Game g : gameList) {
            return (g.countainPlayer(ctx));
        }
        return (null);
    }

    public Game findPlayerGame(Player p) {
        for (Game g : gameList) {
            if (g.countainPlayer(p) != null)
                return (g);
        }
        return (null);
    }

    public void removePlayerAndStopGame(ChannelHandlerContext ctx) {
        for (Game g : gameList) {
            if (g.countainPlayer(ctx) != null) {

            }
        }
    }
}
