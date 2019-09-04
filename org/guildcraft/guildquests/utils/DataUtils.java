package org.guildcraft.guildquests.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataUtils {
	public static <T> void writeListToObjectOutput(List<T> list, ObjectOutput output) throws IOException {
		output.writeInt(list.size());
		for (T element : list)
			output.writeObject(element);
	}

	@SuppressWarnings("unchecked")
	public static <T> void readObjectInputToList(List<T> emptyList, ObjectInput input)
			throws IOException, ClassNotFoundException {
		int entries = input.readInt();
		while (entries > 0) {
			emptyList.add((T) input.readObject());
			entries--;
		}
	}

	public static <T, K> void writeMapToObjectOutput(Map<T, K> map, ObjectOutput output) throws IOException {
		output.writeInt(map.size());
		for (Entry<T, K> entry : map.entrySet()) {
			output.writeObject(entry.getKey());
			output.writeObject(entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, K> void readObjectInputToMap(Map<T, K> emptyMap, ObjectInput input)
			throws IOException, ClassNotFoundException {
		int entries = input.readInt();
		while (entries > 0) {
			emptyMap.put((T) input.readObject(), (K) input.readObject());
			entries--;
		}
	}

	public static <T> byte[] serializeObject(T object) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);

			outputStream.writeObject(object);
			outputStream.close();

			return byteStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializeObject(byte[] data) {
		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream inputStream = new ObjectInputStream(byteStream);

			T object = (T) inputStream.readObject();

			inputStream.close();
			byteStream.close();

			return object;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}