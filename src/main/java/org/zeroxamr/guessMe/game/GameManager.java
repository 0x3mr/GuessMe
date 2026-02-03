package org.zeroxamr.guessMe.game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.zeroxamr.guessMe.GuessMe;
import org.zeroxamr.guessMe.utilities.GameStatus;
import org.zeroxamr.guessMe.utilities.Utilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class GameManager {
    protected final GuessMe plugin;
    private final int[] coordinates = new int[101]; // Not '101' - set based on config
    private final Map<String, Game> games = new HashMap<>();

    public GameManager(GuessMe plugin) {
        this.plugin = plugin;
    }

    private boolean mapGameInstances(String configGameID) {
        String sql = "SELECT * FROM Arenas";

        try (Connection connection = plugin.getDatabase().getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                String gameMapName = result.getString("gameMapName");

                // Not 'Lotus' but the chosen theme from the config!
                if (Objects.equals(gameMapName, "Lotus")) {

                    // Not '101' but based on preset amount of instances in config!
                    for (int i = 1; i < 101; i++) {
                        String gameID = "mini-" + Utilities.randomNumberGenerator(3) + Utilities.randomLetterGenerator(2);
                        String gameAdmin = result.getString("gameAdmin");
                        // String gameStatus is set to IDLE by default

                        int gameMinPlayers = 3; // Should be read from config (should have checks when reading value from config)
                        int gameMaxPlayers = Integer.parseInt(result.getString("gameMaxPlayers"));

                        Location[] playerLocations = Utilities.deserializeLocations(result.getString("playerLocations"));
                        Location waitingLocation = Utilities.deserializeLocations(result.getString("waitingLocation"))[0];
                        Location returnLobby = null; // Should be set from config or settable by ingame command

                        String gameMapSchematic = result.getString("gameMapSchematic");

                        Game gameInstance = new Game()

                        int coordinateSlot = getNextReservableCoordSlot();

                        if (coordinateSlot == -1) {
                            getLogger().info("GameManager: Reached maximum amount of games created!");
                            return;
                        }

                        Game newGame = new Game(
                                gameID,
                                gameStatus,
                                gameMapName,
                                gameMapSchematic,
                                playerLocations,
                                waitingLocation,
                                returnLobby,
                                coordinateSlot
                        );

                        games.put(gameID, newGame);

                        boolean creation = games.containsKey(gameID);
                        boolean construct = constructInstance(games.get(gameID));

                        getLogger().info("first: " + creation + " second: " + construct);

                        if (creation && construct) {
                            loadedGamesIDs.add(gameID);
                            found = true;
                        }
                    }

                    break;
                }
            }

                    if (!found) {
                        getLogger().info("GameManager: No game maps exists within the database.");
                    } else {
                        StringBuilder tempString = new StringBuilder();
                        getLogger().info("GameManager: Successfully loaded maps: ");

                        for (int i = 0; i < loadedGamesIDs.size(); i++) {
                            tempString.append(loadedGamesIDs.get(i));
                            if (i + 1 != loadedGamesIDs.size()) {
                                tempString.append(", ");
                            }
                        }

                        getLogger().info(String.valueOf(tempString));
                    }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    private boolean setupEnvironment() {
        File worldFolder = new File(Bukkit.getWorldContainer(), "Games");

        if (worldFolder.exists() && worldFolder.isDirectory()) {
            plugin.getLogger().info("GameManager: Found an existing 'Games' instances world.");
            return true;
        } else {
            plugin.getLogger().info("GameManager: Games instances world not found.");
            plugin.getLogger().info("GameManager: Creating 'Games' instances world...");

            WorldCreator creator = new WorldCreator("Games");
            creator.generator(new Utilities.VoidChunkGenerator());
            World newWorld = Bukkit.createWorld(creator);
            File tempWorldFolder = new File(Bukkit.getWorldContainer(), "Games");

            if (tempWorldFolder.exists() && tempWorldFolder.isDirectory()) {
                plugin.getLogger().info("GameManager: Games instances world created successfully.");
            } else {
                plugin.getLogger().info("GameManager: Games instances world not found after attempting to create it!");
                return false;
            }
        }

        return false;
    }

    private void loadDeveloperMaps() {
        if (plugin.getDatabase().isOnline()) {
            if (plugin.getDatabase().doTablesExist()) {

                String gameMapName = "Lotus";
                String gameAdmin = "0x3mr";
                String gameMaxPlayers = "36";
                String gameMapSchematic = "BBA.schematic";
                String playerLocations = "";
                String waitingLocation = "";

                String sql = "INSERT INTO Arenas (gameID, gameAdmin, gameMaxPlayers, gameMapSchematic, playerLocations, waitingLocation) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection connection = plugin.getDatabase().getDataSource().getConnection();
                     PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setString(1, gameMapName);
                        ps.setString(2, gameAdmin);
                        ps.setString(3, gameMaxPlayers);
                        ps.setString(4, gameMapSchematic);
                        ps.setString(5, playerLocations);
                        ps.setString(6, waitingLocation);
                        ps.executeUpdate();

                } catch (SQLException error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
