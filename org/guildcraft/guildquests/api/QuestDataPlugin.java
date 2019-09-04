package org.guildcraft.guildquests.api;

import org.bukkit.plugin.Plugin;
import org.guildcraft.guildquests.data.QuestDataFactory;

public interface QuestDataPlugin extends Plugin {
	public short getQuestDataPriority();
	public QuestDataFactory getNewQuestDataFactory();
}