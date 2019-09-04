package org.guildcraft.guildquests;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.guildcraft.guildquests.api.QuestDataPlugin;
import org.guildcraft.guildquests.data.QuestDataFactory;
import org.guildcraft.guildquests.quests.QuestController;
import org.guildcraft.guildquests.utils.SQLHelper;

public class GuildQuests extends JavaPlugin implements QuestDataPlugin {
	private static GuildQuests instance;
	private SQLHelper sqlHelper;

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
					long startTime = System.currentTimeMillis();
					System.out.println("[GuildQuests] Initializing...");
					
					questDataFactory = loadQuestDataFactory();
					
					if (questDataFactory == null) {
						System.out.println("[GuildQuests] No quest data factory! Disabling...");
						Bukkit.getPluginManager().disablePlugin(GuildQuests.getInstance());
						return;
					}
					
					questController = loadQuestController();
					
					System.out.println("[GuildQuests] Initialization complete! (Completed in "
							+ (System.currentTimeMillis() - startTime) + " ms)");
				}
			}.runTask(this);

			System.out.println("[GuildQuests] Pre-initialization complete! (Completed in "
					+ (System.currentTimeMillis() - startTime) + " ms)");
		} catch (Exception e) {
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
	
	private QuestDataFactory loadQuestDataFactory() {
		System.out.println("[GuildQuests] Loading quest data factory...");
		
		QuestDataPlugin questDataPlugin = this;
		
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin instanceof QuestDataPlugin) {
				QuestDataPlugin newQuestDataPlugin = (QuestDataPlugin) plugin;
				
				if (newQuestDataPlugin.getQuestDataPriority() > questDataPlugin.getQuestDataPriority())
					questDataPlugin = newQuestDataPlugin;
			}
		}
		
		System.out.println("[GuildQuests] Quest data plugin: " + questDataPlugin.getName());
		
		return questDataPlugin.getNewQuestDataFactory();
	}
	
	private QuestController loadQuestController() {
		return null;
	}

	public QuestDataFactory getQuestDataFactory() {
		return questDataFactory;
	}

	public QuestController getQuestController() {
		return questController;
	}
	
	// === [ QuestDataPlugin Methods ] === //

	public short getQuestDataPriority() {
		return 0;
	}

	public QuestDataFactory getNewQuestDataFactory() {
		if (sqlHelper == null) {
			sqlHelper = new SQLHelper(getConfig().getString("database.ip"), getConfig().getString("database.port"),
					getConfig().getString("database.database"), getConfig().getString("database.username"),
					getConfig().getString("database.password"));

			if (!sqlHelper.connect())
				return null;
		}

		if (!sqlHelper.isConnected())
			return null;
		
		return new SQLQuestDataFactory(getConfig().getString("database.prefix", ""), this, sqlHelper);
	}
}