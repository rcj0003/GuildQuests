package org.guildcraft.guildquests;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.guildcraft.guildquests.api.QuestDataPlugin;
import org.guildcraft.guildquests.data.QuestDataFactory;
import org.guildcraft.guildquests.quests.QuestController;

public class GuildQuests extends JavaPlugin implements QuestDataPlugin {
	private static GuildQuests instance;
	
	private QuestDataFactory questDataFactory;
	private QuestController questController;
	
	public static GuildQuests getInstance() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("[GuildQuests] Pre-initializing...");
			
			// This will run once all plugins are loaded.
			new BukkitRunnable() {
				public void run() {
				}
			}.runTask(this);
			
			System.out.println("[GuildQuests] Pre-initialization complete! (Completed in "
					+ (System.currentTimeMillis() - startTime) + " ms)");
		}
		catch (Exception e) {
			System.out.println("[GuildQuests] Initialization failed! Disabling...");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
	}
	
	public void onDisable() {
		System.out.println("[GuildQuests] Disabling quest data factory...");
		
		Bukkit.getScheduler().cancelTasks(this);
		
		if (questDataFactory != null)
			questDataFactory.disable();
		
		System.out.println("[GuildQuests] Finished up.");
	}
	
	public QuestDataFactory getQuestDataFactory() {
		return questDataFactory;
	}
	
	public QuestController getQuestController() {
		return questController;
	}
	
	public short getQuestDataPriority() {
		return 0;
	}
	
	public QuestDataFactory getNewQuestDataFactory() {
		return null;
	}
}