package eu.epitech.jcoinche.jcoincheserver.server;

import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.util.ArrayList;

public class GameManager {
    private ArrayList<Game> gameList = new ArrayList<>();

    private void createNewGame() {
        gameList.add(new Game());
    }
    public Game isAGameFree() {
        for (Game g : gameList) {
            if (!g.isFull())
                return (g);
        }
        return (null);
    }

    public void addPlayerToGame(Player p) {
        Boolean hasFoundAGame = false;

        for (Game g : gameList) {
            if (!(g.isFull())) {
                g.addPlayer(p);
                hasFoundAGame = true;
            }
        }
        if (!hasFoundAGame) {
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
