package me.wilkosam.deathcore.data;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.mineacademy.fo.collection.SerializedMap;

import java.util.UUID;

@Getter
public class GameDeath {

	public static GameDeath deserialise(SerializedMap serializedMap) {
		return new GameDeath(
				serializedMap.getString("player-name"),
				serializedMap.getUUID("uuid"),
				DamageCause.valueOf(serializedMap.getString("death-cause"))
		);
	}

	private final String playerName;
	private final UUID uuid;
	private final DamageCause deathCause;

	public GameDeath(Player player, DamageCause damageCause) {
		this(player.getName(), player.getUniqueId(), damageCause);
	}

	public GameDeath(String name, UUID uuid, DamageCause damageCause) {
		this.playerName = name;
		this.uuid = uuid;
		this.deathCause = damageCause;
	}

	public SerializedMap serialise() {
		SerializedMap serializedMap = new SerializedMap();

		serializedMap.put("player-name", this.playerName);
		serializedMap.put("uuid", this.uuid.toString());
		serializedMap.put("death-cause", this.deathCause.toString());


		return serializedMap;
	}
}
