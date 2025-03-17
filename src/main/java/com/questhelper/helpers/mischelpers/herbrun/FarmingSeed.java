package com.questhelper.helpers.mischelpers.herbrun;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Describes any type of seed used in farming.
 */
@AllArgsConstructor
public class FarmingSeed {
    @Getter @Setter 
    private int seedId;
    @Getter @Setter
    private String seedName;

    @Override
    public String toString() {
        return seedName;
    }
}
