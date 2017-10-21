package eu.epitech.jcoinche.jcoincheserver.server;

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
    }

    public Game findPlayerGame(Player p) {
        for (Game g : gameList) {
            if (g.countainPlayer(p))
                return (g);
        }
        return (null);
    }
}
