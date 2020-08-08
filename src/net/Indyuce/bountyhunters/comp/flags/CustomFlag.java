package net.Indyuce.bountyhunters.comp.flags;

public enum CustomFlag {
	CLAIM_BOUNTIES,
	CREATE_BOUNTIES,
	AUTO_BOUNTY;

	public String getPath() {
		return name().toLowerCase().replace("_", "-");
	}
}