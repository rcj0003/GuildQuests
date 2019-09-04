package org.guildcraft.guildquests.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;

public class QuestController {
	private Map<QuestType, List<Quest>> questSessions = new HashMap<>();
	private Map<QuestType, Long> questSessionExpirations = new HashMap<>();
	private FileConfiguration config;

	public QuestController(FileConfiguration config) {
		this.config = config;
	}

	public void load() {
		for (String questTypeName : config.getKeys(false)) {
			QuestType questType = QuestType.valueOf(questTypeName.toUpperCase());
			List<Quest> quests = new ArrayList<>();

			for (String questName : config.getStringList(questTypeName + ".quests"))
				quests.add(Quest.getByName(questName));

			questSessions.put(questType, quests);
			questSessionExpirations.put(questType, config.getLong(questTypeName + ".expiration"));
		}
	}

	public void save() {
		for (QuestType questType : QuestType.values()) {
			config.set(questType.name().toLowerCase() + ".quests", questSessions.get(questType).stream()
					.map(e -> e.name().toLowerCase()).collect(Collectors.toList()));
			config.set(questType.name().toLowerCase() + ".expiration", questSessionExpirations.get(questType));
		}
	}
}