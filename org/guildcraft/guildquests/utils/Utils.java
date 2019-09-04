package org.guildcraft.guildquests.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Utils {
	public static class DataUtils {
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

	public static class ConfigUtils {
		public static void updateConfiguration(Plugin p) {
			p.reloadConfig();

			if (p.getConfig().contains("version") && p.getConfig().getDefaults().contains("version")) {
				if (p.getConfig().getInt("version") >= p.getConfig().getDefaults().getInt("version"))
					return;
				p.getConfig().set("version", p.getConfig().getDefaults().get("version"));
			}

			Set<String> n = p.getConfig().getKeys(true);
			for (String s : p.getConfig().getDefaults().getKeys(true))
				if (!n.contains(s))
					p.getConfig().set(s, p.getConfig().getDefaults().get(s));

			p.saveConfig();
		}

		public static void makeConfiguration(Plugin p) { // Do work to create the configuration.
			try {
				if (!p.getDataFolder().exists())
					p.getDataFolder().mkdirs();
				if (!new File(p.getDataFolder(), "config.yml").exists())
					p.saveDefaultConfig();
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage("An error occurred while loading the config.");
				e.printStackTrace();
			}
		}

		public static FileConfiguration getConfiguration(Plugin p, String name) throws Exception {
			File f = new File(p.getName() + File.separator + name);
			if (!f.exists())
				throw new Exception();
			return YamlConfiguration.loadConfiguration(f);
		}

		public static FileConfiguration getConfiguration(String name) throws Exception {
			File f = new File(name.replace("/", File.separator));
			if (!f.exists())
				throw new Exception();
			return YamlConfiguration.loadConfiguration(f);
		}
	}

	public static class TimeUtils {
		public static long getTimeAfter(int days, int hours, int minutes, int seconds, int milliseconds) {
			return System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000)
					+ (minutes * 60 * 1000) + (seconds * 1000) + milliseconds;
		}

		public static long timeRemainingUntil(long time) {
			return Math.max(Math.min(time - System.currentTimeMillis(), Long.MAX_VALUE), 0);
		}

		public static String millisecondsToString(long ms) {
			int days = 0, hours = 0, mins = 0, seconds = 0;
			while (ms >= 24 * 60 * 60 * 1000) {
				ms -= 24 * 60 * 60 * 1000;
				days++;
			}
			while (ms >= 60 * 60 * 1000) {
				ms -= 60 * 60 * 1000;
				hours++;
			}
			while (ms >= 60 * 1000) {
				ms -= 60 * 1000;
				mins++;
			}
			while (ms >= 1000) {
				ms -= 1000;
				seconds++;
			}
			String s = days > 0 ? days + " days" : "";
			s += hours > 0 ? (s.length() > 0 ? ", " : "") + hours + " hours" : "";
			s += mins > 0 ? (s.length() > 0 ? ", " : "") + mins + " minutes" : "";
			s += (s.length() > 0 ? ", " : "") + seconds + " seconds";
			return s;
		}
	}

	public static class IntUtils {
		public static int containIntWithinRange(int value, int min, int max) {
			return Math.max(Math.min(max, value), min);
		}

		public static boolean containedWithinRange(int value, int min, int max) {
			return Math.max(Math.min(max, value), min) == value;
		}
	}

	public static class ArrayUtils {
		@SuppressWarnings("unchecked")
		public static <T> T[] popArray(T[] array, int index) {
			if (index >= 0 && index < array.length) {
				T[] newArray = (T[]) new Object[array.length - 1];
				int newArrayOffset = 0;

				for (int offset = 0; offset < array.length; offset++)
					if (offset != index)
						newArray[newArrayOffset++] = array[offset];

				return newArray;
			} else
				return array;
		}
	}

	public static class StringUtils {
		public static List<String> getFlags(String[] arguments) {
			List<String> flags = new ArrayList<>();

			for (String string : arguments) {
				if (string.startsWith("-"))
					flags.add(string.substring(1));
			}

			return flags;
		}

		public static Map<String, List<String>> parseArguments(String[] arguments) {
			Map<String, List<String>> parsedArguments = new HashMap<>();
			parsedArguments.put("", new ArrayList<>());
			String lastParsedArgument = "";

			for (String argument : arguments) {
				if (argument.startsWith("-")) {
					parsedArguments.put(argument.substring(1), new ArrayList<>());
					continue;
				}
				parsedArguments.get(lastParsedArgument).add(argument);
			}

			return parsedArguments;
		}

		public static String[] convertColorCodes(String[] array) {
			int offset = 0;

			for (String element : array)
				array[offset++] = convertColorCodes(element);

			return array;
		}

		public static String convertColorCodes(String string) {
			return string.replace((char) 38, (char) 167);
		}

		public static String parseLocationToString(Location location) {
			return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", "
					+ location.getZ() + ", " + location.getYaw() + ", " + location.getPitch();
		}

		public static Location parseLocationFromString(String string) {
			String[] locationData = string.split(", ");
			return new Location(Bukkit.getWorld(locationData[0]), Double.parseDouble(locationData[1]),
					Double.parseDouble(locationData[2]), Double.parseDouble(locationData[3]),
					Float.parseFloat(locationData[4]), Float.parseFloat(locationData[5]));
		}

		// Borrowed from Tibo442's PracticePvP
		public static String joinNames(String... players) {
			StringBuilder toReturn = null;

			for (String name : players)
				if (toReturn == null)
					toReturn = new StringBuilder(name);
				else
					toReturn.append(", ").append(name);

			return replaceLast(toReturn.toString(), ", ", " and ");
		}

		// Borrowed from Tibo442's PracticePvP
		public static String replaceLast(String string, String substring, String replacement) {
			int index = string.lastIndexOf(substring);
			if (index == -1)
				return string;
			return string.substring(0, index) + replacement + string.substring(index + substring.length());
		}

		// Borrowed from Tibo442's PracticePvP
		public static String joinNames(Player... players) {
			StringBuilder toReturn = null;

			for (Player player : players)
				if (toReturn == null)
					toReturn = new StringBuilder(player.getName());
				else
					toReturn.append(", ").append(player.getName());

			return replaceLast(toReturn.toString(), ", ", " and ");
		}
	}
}
