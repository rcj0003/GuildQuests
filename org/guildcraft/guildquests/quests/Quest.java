package org.guildcraft.guildquests.quests;

public enum Quest {
	UNKNOWN, POTIONS_DRANK, KILLS;
	
	public static Quest getByName(String questName) {
		Quest matchedQuest = UNKNOWN;
		
		for (Quest quest : values()) {
			if (quest.name().equals(questName))
				matchedQuest = quest;
		}
		
		return matchedQuest;
	}
}