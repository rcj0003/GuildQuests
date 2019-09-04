package org.guildcraft.guildquests.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.guildcraft.guildquests.data.QuestData;
import org.guildcraft.guildquests.data.QuestDataFactory;
import org.guildcraft.guildquests.quests.Quest;

public class QuestListener implements Listener {
	private QuestDataFactory questDataFactory;
	
	public QuestListener(QuestDataFactory questDataFactory) {
		this.questDataFactory = questDataFactory;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		questDataFactory.loadQuestDataAsync(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		questDataFactory.unloadQuestDataAsync(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrinkPotion(PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.POTION) {
			QuestData questData = questDataFactory.getQuestData(e.getPlayer());
			questData.adjustQuestProgress(Quest.POTIONS_DRANK, 1);
		}
	}
}