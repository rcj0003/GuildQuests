package org.guildcraft.guildquests.data;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface QuestDataFactory {
	void initialize();
	void disable();
	
	void initializeQuestData(QuestData questData);
	void initializeQuestDataAsync(QuestData questData);
	
	void deleteQuestData(UUID playerId);
	void deleteQuestData(Player player);
	void deleteQuestDataAsync(UUID playerId);
	void deleteQuestDataAsync(Player player);
	
	QuestData getQuestData(UUID playerId);
	QuestData getQuestData(Player player);
	List<QuestData> getQuestData(List<Player> players);
	
	void updateQuestData(QuestData playerData);
	void updateQuestDataAsync(QuestData playerData);
	
	boolean doesQuestDataExist(UUID playerId);
	boolean doesQuestDataExist(Player player);
	boolean isQuestDataLoaded(UUID playerId);
	boolean isQuestDataLoaded(Player player);
	
	void loadQuestData(UUID playerId);
	void loadQuestData(Player player);
	void loadQuestDataAsync(UUID playerId);
	void loadQuestDataAsync(Player player);
	
	void unloadQuestData(UUID playerId);
	void unloadQuestData(Player player);
	void unloadQuestDataAsync(UUID playerId);
	void unloadQuestDataAsync(Player player);
}