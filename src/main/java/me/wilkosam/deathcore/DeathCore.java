package me.wilkosam.deathcore;

import me.wilkosam.deathcore.data.GameSettings;
import me.wilkosam.deathcore.data.PlayerCache;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompProperty;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.Remain;

public final class DeathCore extends SimplePlugin {

	@Override
	protected void onPluginStart() {

		// Check if multiverse core is installed
		if (!HookManager.isMultiverseCoreLoaded()) {
			Common.warning("Multi-verse Core is not installed... Plugin will not work.");
		}

		// Load game settings
		GameSettings.get();

		// Load saved active game
		GameManager.loadSavedGame();
	}

	/* ------------------------------------------------------------------------------- */
	/* Events */
	/* ------------------------------------------------------------------------------- */

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {

		// The player
		Player joinedPlayer = event.getPlayer();

		// Load their cache file
		PlayerCache cache = PlayerCache.get(joinedPlayer);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		// The player
		Player quitPlayer = event.getPlayer();

		// Unload their cache file
		PlayerCache.unload(quitPlayer);
	}

	@EventHandler
	public void onPlayerDeath(final EntityDamageEvent event) {

		// Check that it was a player that was damaged
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		// Get the player that was damaged
		Player hurtPlayer = (Player) event.getEntity();

		// Check if the damage done has killed the player
		if (hurtPlayer.getHealth() - event.getDamage() > 0)
			return;

		// Get player cache file
		PlayerCache playerCache = PlayerCache.get(hurtPlayer);

		// Increase players' total deaths
		playerCache.setTotalDeaths(playerCache.getTotalDeaths() + 1);

		// Update the active game death counter
		GameManager.getActiveGame().registerDeath(hurtPlayer, event.getCause());

		// Check if enough deaths have occured to restart the world
		if (GameManager.getActiveGame().getTotalDeaths() < GameSettings.get().getDeathsRequired())
			return;

		// Cancel the event
		event.setCancelled(true);

		// Play death sound
		CompSound.ENTITY_PLAYER_DEATH.play(hurtPlayer);

		// Initiate death effect for all players
		for (Player player : Remain.getOnlinePlayers()) {

			// Make sure they don't die mid-air and can't move
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setWalkSpeed(0F);
			player.setFlySpeed(0F);
			CompProperty.GRAVITY.apply(player, false);
			CompProperty.INVULNERABLE.apply(player, true);

			// Teleport way up high
			player.teleport(player.getLocation().clone().add(0, 1000, 0));

			// Give blindness
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100000));

			// Play scary sound
			CompSound.ENTITY_WITHER_DEATH.play(player);

			// Send titles
			if (player == hurtPlayer)
				Remain.sendTitle(player, 20, 30 * 20, 20,
						"&c&lYou Died", "");
			else
				Remain.sendTitle(player, 20, 30 * 20, 20,
						"&c&l" + player.getName() + " died", "");

			// Send new world creation message
			Common.tellNoPrefix(player, "&eCreating new world...");
		}

		// Begin a new game. Runs a tick later to prevent async lag problems (idk man but it works like let's go)
		Common.runLater(2, GameManager::newGame);
	}

	/* ------------------------------------------------------------------------------- */
	/* Static */
	/* ------------------------------------------------------------------------------- */

	public static DeathCore getInstance() {
		return (DeathCore) SimplePlugin.getInstance();
	}
}
