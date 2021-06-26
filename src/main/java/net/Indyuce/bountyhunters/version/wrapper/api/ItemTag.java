package net.Indyuce.bountyhunters.version.wrapper.api;

public class ItemTag {
	private final String path;
	private final Object value;

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