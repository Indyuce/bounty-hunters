package net.Indyuce.bountyhunters.version;

public class PluginVersion {
	public String version;
	public int[] integers;

	public PluginVersion(Class<?> clazz) {
		this.version = clazz.getPackage().getName().replace(".", ",").split(",")[3];
		String[] split = version.substring(1).split("\\_");
		this.integers = new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]) };
	}

	public boolean isBelowOrEqual(int... version) {
		return version[0] > integers[0] ? true : version[1] >= integers[1];
	}

	public boolean isStrictlyHigher(int... version) {
		return version[0] < integers[0] ? true : version[1] < integers[1];
	}

	public int getRevisionNumber() {
		return Integer.parseInt(version.split("\\_")[2].replaceAll("[^0-9]", ""));
	}

	public int[] toNumbers() {
		return integers;
	}

	@Override
	public String toString() {
		return version;
	}
}
