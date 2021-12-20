package net.Indyuce.bountyhunters.version.wrapper.api;

public class ItemTag {
	private final String path;
	private final Object value;

	/**
	 * Used to store NBTTags in items
	 *
	 * @param path  Item tag path
	 * @param value Must be a Boolean, String, Integer or Double
	 */
	public ItemTag(String path, Object value) {
		this.path = path;
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public Object getValue() {
		return value;
	}
}