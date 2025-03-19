package me.wilkosam.deathcore.data;

import lombok.Getter;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.settings.YamlConfig;


@Getter
public class GameSettings extends YamlConfig {

	public static GameSettings get() {
		return new GameSettings();
	}

	private int deathsRequired;

	private int maxBackups;

	private long autoDelete;

	private GameSettings() {
		this.loadConfiguration("game-settings.yml", "settings/game-settings.yml");
	}

	@Override
	protected void onLoad() {
		this.deathsRequired = getInteger("deaths-required");
		this.maxBackups = getInteger("max-backups");
		this.autoDelete = TimeUtil.toMilliseconds(getString("auto-delete"));
	}

	@Override
	protected void onSave() {
		set("deaths-required", String.valueOf(this.deathsRequired));
		set("max-backups", String.valueOf(this.maxBackups));
		set("auto-delete", String.valueOf(this.autoDelete));
	}

	public void setDeathsRequired(int deathsRequired) {
		this.deathsRequired = deathsRequired;

		this.save();
	}

	public void setMaxBackups(int maxBackups) {
		this.maxBackups = maxBackups;

		this.save();
	}

	public void setAutoDelete(long autoDelete) {
		this.autoDelete = autoDelete;

		this.save();
	}
}
