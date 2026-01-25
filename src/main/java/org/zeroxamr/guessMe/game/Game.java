package org.zeroxamr.guessMe.game;

import org.bukkit.Location;
import org.zeroxamr.guessMe.utilities.GameStatus;

public class Game {
    private int coordinateSlot;
    private int gameMinPlayers;
    private int gameMaxPlayers;
    private String gameID = null;
    private String gameAdmin = null;
    private String gameMapName = null;
    private GameStatus gameStatus = GameStatus.IDLE;
    private Location[] playerLocations = null;
    private Location waitingLocation = null;
    private Location returnLobby = null;

    public Game(int coordSlot, int gameMinPlayers, int gameMaxPlayers, String gameID, String gameAdmin, String gameMapName, Location[] playerLocations, Location waitingLocation, Location returnLobby) {
        coordinateSlot = coordSlot;
        this.gameMinPlayers = gameMinPlayers;
        this.gameMaxPlayers = gameMaxPlayers;
        this.gameID = gameID;
        this.gameAdmin = gameAdmin;
        this.gameMapName = gameMapName;
        this.playerLocations = playerLocations;
        this.waitingLocation = waitingLocation;
        this.returnLobby = returnLobby;
    }
}
