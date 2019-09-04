package org.guildcraft.guildquests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class AbstractQuestDataFactory implements QuestDataFactory {
	public List<QuestData> getQuestData(List<Player> players) {
		List<QuestData> playerData = new ArrayList<>();

		for (Player player : players)
			playerData.add(getQuestData(player));

		return playerData;
	}

	public void deleteQuestData(Player player) {
		deleteQuestData(player.getUniqueId());
	}

	public void deleteQuestDataAsync(Player player) {
		deleteQuestDataAsync(player.getUniqueId());
	}

	public QuestData getQuestData(Player player) {
		return getQuestData(player.getUniqueId());
	}

	public boolean doesQuestDataExist(Player player) {
		return doesQuestDataExist(player.getUniqueId());
	}

	public boolean isQuestDataLoaded(Player player) {
		return isQuestDataLoaded(player.getUniqueId());
	}

	public void loadQuestData(Player player) {
		loadQuestData(player.getUniqueId());
	}

	public void loadQuestDataAsync(Player player) {
		loadQuestDataAsync(player.getUniqueId());
	}

	public void unloadQuestData(Player player) {
		unloadQuestData(player.getUniqueId());
	}

	public void unloadQuestDataAsync(Player player) {
		unloadQuestDataAsync(player.getUniqueId());
	}
}