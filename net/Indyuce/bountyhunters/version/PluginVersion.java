package net.Indyuce.bountyhunters.version;

public class PluginVersion {
	private String[] version;

	public PluginVersion(String version) {
		this.version = version.replaceAll("[^0-9\\.]", "").split("\\.");
	}

	public int getLength() {
		return version.length;
	}

	public Integer numberAt(int index) {
		return index < version.length ? Integer.parseInt(version[index]) : 0;
	}

	public boolean higherThan(PluginVersion version) {
		for (int j = 0; j < Math.max(getLength(), version.getLength()); j++)
			if (numberAt(j) != version.numberAt(j))
				return numberAt(j) > version.numberAt(j);
		return false;
	}
}
