package me.wilkosam.deathcore;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import lombok.Getter;
import me.wilkosam.deathcore.data.ActiveGame;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompProperty;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.Remain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GameManager {

	@Getter
	private static ActiveGame activeGame;

	public static MultiverseCore getCore() {
		return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
	}

	public static void loadSavedGame() {
		activeGame = new ActiveGame();
	}

	public static void newGame() {

		// The old world
		MultiverseWorld oldWorld = getActiveGame().getGameWorld();

		// Generate new unique name for the new world
		String newWorldName = generateNewWorldName();

		// Create new world
		getCore().getMVWorldManager().addWorld(
				newWorldName,
				World.Environment.NORMAL,
				null,
				WorldType.NORMAL,
				true,
				null
		);

		// Get the new world
		MultiverseWorld newWorld = getCore().getMVWorldManager().getMVWorld(newWorldName);

		// Set difficulty to hard
		newWorld.setDifficulty(Difficulty.HARD);

		// Set gamemode to survival
		newWorld.setGameMode(GameMode.SURVIVAL);

		// Teleport all players to new world
		for (Player player : Remain.getOnlinePlayers()) {

			// Play whoosh sound and send message
			CompSound.ENTITY_ARROW_SHOOT.play(player);
			Common.tellNoPrefix(player, "&eTeleporting...");

			// Teleport player to new world spawn
			player.teleport(newWorld.getSpawnLocation());

			// Reset the player
			player.setGameMode(GameMode.SURVIVAL);

			player.setFlying(false);
			player.setAllowFlight(false);

			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.2F);
			CompProperty.GRAVITY.apply(player, true);
			CompProperty.INVULNERABLE.apply(player, false);

			player.getInventory().clear();
			player.setFireTicks(0);
			player.clearActivePotionEffects();
			player.heal(100);
			player.setExp(0);
			player.setFoodLevel(20);

			Remain.resetTitle(player);
		}

		// Unload and remove old world from config
		//TODO unless it's the default bukkit world
		getCore().getMVWorldManager().unloadWorld(oldWorld.getName());
		getCore().getMVWorldManager().removeWorldFromConfig(oldWorld.getName());

		//TODO Move the old world folder into the backups folder and purge oldest backup if needed

		// Update the active game
		activeGame = new ActiveGame(newWorld);
	}

	private static String generateNewWorldName() {

		/*
		Formatted as dd/mm/yyyy_mm-hh
		For example: 19-03-2025_12-03
		Determines when the world was created and gives it a unique identifier
		 */

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm");

		return now.format(formatter);
	}

	private static void purgeOldestBackup() {

	}

	private static Path getPathToBackups() {
		return DeathCore.getInstance().getDataPath().resolve("backups");
	}

	private static int countBackups() {

		// Get the backups path
		Path backupsPath = getPathToBackups();

		// Check that the backups folder exists and that it is actually a folder
		if (Files.exists(backupsPath) && Files.isDirectory(backupsPath)) {

			// Count the number of subsequent folders that are inside it
			try {
				return (int) Files.list(backupsPath)
						.filter(Files::isDirectory)
						.count();
			} catch (IOException e) {
				return 0;
			}
		}

		return 0;
	}
}
