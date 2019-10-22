package net.Indyuce.bountyhunters.comp.flags;

public enum CustomFlag {
	CLAIM_BOUNTIES;

	public String getPath() {
		return name().toLowerCase().replace("_", "-");
	}
}