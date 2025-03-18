package me.wilkosam.deathcore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class DeathCore extends SimplePlugin {

	@Override
	protected void onPluginStart() {

		// Check if multiverse core is installed
		if (!HookManager.isMultiverseCoreLoaded()) {
			Common.warning("Multi-verse Core is not installed... Plugin will not work.");
		}
	}

	@Override
	protected void onReloadablesStart() {

		// You can check for necessary plugins and disable loading if they are missing
		//Valid.checkBoolean(HookManager.isVaultLoaded(), "You need to install Vault so that we can work with packets, offline player data, prefixes and groups.");

		// Uncomment to load variables
		// Variable.loadVariables();

		//
		// Add your own plugin parts to load automatically here
		// Please see @AutoRegister for parts you do not have to register manually
		//
	}

	@Override
	protected void onPluginPreReload() {

		// Close your database here if you use one
		//YourDatabase.getInstance().close();
	}

	/* ------------------------------------------------------------------------------- */
	/* Events */
	/* ------------------------------------------------------------------------------- */

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
	}

	/* ------------------------------------------------------------------------------- */
	/* Static */
	/* ------------------------------------------------------------------------------- */

	public static DeathCore getInstance() {
		return (DeathCore) SimplePlugin.getInstance();
	}
}
