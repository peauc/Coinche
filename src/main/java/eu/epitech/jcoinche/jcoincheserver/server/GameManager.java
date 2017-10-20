package eu.epitech.jcoinche.jcoincheserver.server;

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

    public Game findPlayerGame(Player p) {
        for (Game g : gameList) {
            if (g.countainPlayer(p))
                return (g);
        }
        return (null);
    }
}
