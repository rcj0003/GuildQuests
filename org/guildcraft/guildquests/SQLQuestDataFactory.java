package org.guildcraft.guildquests;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.guildcraft.guildquests.data.AbstractQuestDataFactory;
import org.guildcraft.guildquests.data.QuestData;
import org.guildcraft.guildquests.utils.DataUtils;
import org.guildcraft.guildquests.utils.SQLHelper;

public class SQLQuestDataFactory extends AbstractQuestDataFactory {
	private HashMap<UUID, QuestData> questDataCache = new HashMap<>();
	private String tablePrefix;
	private Plugin plugin;
	private SQLHelper sqlHelper;

	public SQLQuestDataFactory(String tablePrefix, Plugin plugin, SQLHelper sqlHelper) {
		this.tablePrefix = tablePrefix;
		this.plugin = plugin;
		this.sqlHelper = sqlHelper;
	}

	public void initialize() {
		sqlHelper.refreshConnection();

		try {
			sqlHelper.getConnection().prepareStatement(
					"CREATE TABLE IF NOT EXISTS `" + tablePrefix + "GuildQuestData` (id CHAR(36), questData BLOB);")
					.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public void disable() {
		for (QuestData questData : questDataCache.values())
			updateQuestData(questData);
		questDataCache.clear();
	}

	public void initializeQuestData(QuestData questData) {
		try {
			sqlHelper.refreshConnection();

			PreparedStatement statement = sqlHelper.getConnection().prepareStatement(
					"INSERT INTO `" + tablePrefix + "GuildQuestData` values(?, ?);");
			
			statement.setString(1, questData.getUniqueId().toString());
			statement.setBytes(2, DataUtils.serializeObject(questData));
			
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public void initializeQuestDataAsync(QuestData questData) {
		new BukkitRunnable() {
			public void run() {
				initializeQuestData(questData);
			}
		}.runTaskAsynchronously(plugin);
	}

	public void deleteQuestData(UUID playerId) {
		try {
			sqlHelper.refreshConnection();

			PreparedStatement statement = sqlHelper.getConnection()
					.prepareStatement("DELETE FROM `" + tablePrefix + "GuildQuestData` WHERE id=?;");
			statement.setString(1, playerId.toString());
			
			statement.executeUpdate();
			questDataCache.remove(playerId);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public void deleteQuestDataAsync(UUID playerId) {
		new BukkitRunnable() {
			public void run() {
				deleteQuestData(playerId);
			}
		}.runTaskAsynchronously(plugin);
	}

	public QuestData getQuestData(UUID playerId) {
		if (questDataCache.containsKey(playerId))
			return questDataCache.get(playerId);
		
		long startTime = System.currentTimeMillis();
		
		try {
			QuestData questData = null;
			
			PreparedStatement statement = sqlHelper.getConnection()
					.prepareStatement("SELECT questData FROM `" + tablePrefix + "GuildQuestData` WHERE id=? LIMIT 1;");
			statement.setString(1, playerId.toString());
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				questData = DataUtils.deserializeObject(results.getBytes("questData"));
			} else {
				questData = new QuestData(playerId);
				initializeQuestData(questData);
			}
			
			results.close();
			System.out.println("Load time (" + playerId.toString() + ") (ms): " + (System.currentTimeMillis() - startTime));
			
			return questData;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return null;
	}

	public void updateQuestData(QuestData questData) {
		try {
			sqlHelper.refreshConnection();

			if (!doesQuestDataExist(questData.getUniqueId())) {
				initializeQuestData(questData);
			} else {
				PreparedStatement statement = sqlHelper.getConnection().prepareStatement("UPDATE `" + tablePrefix
						+ "GuildQuestData` SET questData=? WHERE id=?;");

				statement.setBytes(1, DataUtils.serializeObject(questData));
				statement.setString(2, questData.getUniqueId().toString());
				
				statement.executeUpdate();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateQuestDataAsync(QuestData questData) {
		new BukkitRunnable() {
			public void run() {
				updateQuestData(questData);
			}
		}.runTaskAsynchronously(plugin);
	}

	public boolean doesQuestDataExist(UUID playerId) {
		if (isQuestDataLoaded(playerId))
			return true;

		sqlHelper.refreshConnection();

		try {
			PreparedStatement statement = sqlHelper.getConnection()
					.prepareStatement("SELECT * FROM `" + tablePrefix + "GuildQuestData` WHERE id=?;");
			statement.setString(1, playerId.toString());

			try (ResultSet results = statement.executeQuery()) {
				return results.next();
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return false;
	}

	public boolean isQuestDataLoaded(UUID playerId) {
		return questDataCache.containsKey(playerId);
	}

	public void loadQuestData(UUID playerId) {
		getQuestData(playerId);
	}

	public void loadQuestDataAsync(UUID playerId) {
		new BukkitRunnable() {
			public void run() {
				loadQuestData(playerId);
			}
		}.runTaskAsynchronously(plugin);
	}

	public void unloadQuestData(UUID playerId) {
		if (questDataCache.containsKey(playerId))
			updateQuestData(questDataCache.remove(playerId));
	}

	public void unloadQuestDataAsync(UUID playerId) {
		new BukkitRunnable() {
			public void run() {
				unloadQuestData(playerId);
			}
		}.runTaskAsynchronously(plugin);
	}
}