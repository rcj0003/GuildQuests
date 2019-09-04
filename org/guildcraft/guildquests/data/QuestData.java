package org.guildcraft.guildquests.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.guildcraft.guildquests.quests.Quest;
import org.guildcraft.guildquests.utils.DataUtils;

public class QuestData implements Externalizable {
	private Map<Quest, Integer> questProgress = new HashMap<>();
	private UUID owner;
	
	public QuestData() {
	}
	
	public QuestData(UUID owner) {
		this.owner = owner;
	}
	
	public UUID getUniqueId() {
		return owner;
	}
	
	public int getQuestProgress(Quest quest) {
		return questProgress.getOrDefault(quest, 0);
	}
	
	public void adjustQuestProgress(Quest quest, int progress) {
		questProgress.getOrDefault(quest, getQuestProgress(quest) + progress);
	}
	
	public void setQuestProgress(Quest quest, int progress) {
		questProgress.getOrDefault(quest, progress);
	}
	
	public void resetQuestProgress(Quest quest) {
		questProgress.put(quest, 0);
	}

	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		int versionId = input.readInt();
		owner = (UUID) input.readObject();
		
		switch (versionId) {
		case 0: {
			Map<String, Integer> rawQuestData = new HashMap<>();
			DataUtils.readObjectInputToMap(rawQuestData, input);
			
			for (Entry<String, Integer> dataEntry : rawQuestData.entrySet()) {
				Quest quest = Quest.getByName(dataEntry.getKey());
				
				if (quest == Quest.UNKNOWN) // This quest data doesn't exist in this case.
					continue;
				
				questProgress.put(quest, dataEntry.getValue());
			}
			
			break;
		}
		default: {
			throw new UnsupportedOperationException("Invalid quest data for " + owner.toString() + "! [Unknown version]");
		}
		}
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeInt(0);
		output.writeObject(owner);
		
		Map<String, Integer> rawQuestData = new HashMap<>();
		
		for (Entry<Quest, Integer> dataEntry : questProgress.entrySet())
			rawQuestData.put(dataEntry.getKey().name(), dataEntry.getValue());
		
		DataUtils.writeMapToObjectOutput(rawQuestData, output);
	}
}