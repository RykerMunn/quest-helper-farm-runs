package com.questhelper.helpers.mischelpers.farmrun;

import lombok.AllArgsConstructor;
/**
 * Describes any type of seed used in farming.
 */
@AllArgsConstructor
public class FarmingSeed {
    
    private int seedId;
    private String seedName;

    @Override
    public String toString() {
        return seedName;
    }

    /**
     * Get the seed id.
     * @return the seed id.
     */
    public int getSeedId() {
        return seedId;
    }

    /**
     * Set the seed id.
     * @param seedId the seed id.
     */
    public void setSeedId(int seedId) {
        this.seedId = seedId;
    }

    /**
     * Get the normalized seed name.
     * @return the seed name.
     */
    public String getSeedName() {
        return seedName.toUpperCase();
    }

    /**
     * Get the seed name in lower case.
     * @return the seed name in lower case.
     */
    public String getSeedNameLower() {
        return seedName.toLowerCase();
    }

    /**
     * Set the seed name.
     * @param seedName the seed name.
     */

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }
}
