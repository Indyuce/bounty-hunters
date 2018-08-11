package net.Indyuce.bh.resource;

public enum TitleReward {
	HEAD_HUNTER("Head Hunter", 1),
	HEAD_COLLECTOR("Head Collector", 2),
	EXPERIENCED_HUNTER("Experienced Hunter", 3),
	GREEDY_HUNTER("Greedy Hunter", 4),
	DEAD_OR_ALIVE("Dead or Alive", 5),
	BLOODTHIRSTY("Bloodthirsty", 6),
	BOUNTY_HUNTER("%star% Bounty Hunter %star%", 8),
	LEGENDARY_HUNTER("%star% Legendary Hunter %star%", 15),;

	public String title;
	public int level;

	private TitleReward(String title, int level) {
		this.title = title;
		this.level = level;
	}
}