package org.zeroxamr.guessMe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.zeroxamr.guessMe.database.DatabaseManager;
import org.zeroxamr.guessMe.game.GameManager;

public final class GuessMe extends JavaPlugin implements Listener {
    private GameManager gameManager = null;
    private DatabaseManager databaseInstance = null;

    @Override
    public void onEnable() {
        databaseInstance = new DatabaseManager(this);

//        getCommand("guessme").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("GuessMe has been enabled!");

        gameManager = new GameManager(this);
    }

    @Override
    public void onDisable() {
        databaseInstance.shutdown();
    }

    public DatabaseManager getDatabase() {
        return databaseInstance;
    }
    public GameManager getGameManager() { return gameManager; }
}
