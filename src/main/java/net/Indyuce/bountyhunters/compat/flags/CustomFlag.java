package net.Indyuce.bountyhunters.compat.flags;

public enum CustomFlag {

    /**
     * Ability to claim bounties in specific regions only;
     * this enables the creation of bounty-free PvP zones
     */
    CLAIM_BOUNTIES,

    /**
     * Ability to create a bounty when the CREATOR is at
     * a specific location. This can be good for servers
     * which have worlds not supporting bounties
     */
    CREATE_BOUNTIES,

    /**
     * Ability for the auto bounty system to create a bounty
     * or increase an existing bounty in a specific location
     */
    AUTO_BOUNTY;

    public String getPath() {
        return name().toLowerCase().replace("_", "-");
    }
}