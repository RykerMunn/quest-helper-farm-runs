package com.questhelper.helpers.mischelpers.farmrun.herbs;

import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;

import lombok.Getter;

public enum HerbPatch {
    UNKNOWN(new ManualRequirement(), new ManualRequirement()),
    ARDOUGNE(new ManualRequirement(), new ManualRequirement()),
    CATHERBY(new ManualRequirement(), new ManualRequirement()),
    FALADOR(new ManualRequirement(), new ManualRequirement()),
    FARMING_GUILD(new ManualRequirement(), new ManualRequirement()),
    HARMONY(new ManualRequirement(), new ManualRequirement()),
    MORYTANIA(new ManualRequirement(), new ManualRequirement()),
    TROLL_STRONGHOLD(new ManualRequirement(), new ManualRequirement()),
    WEISS(new ManualRequirement(), new ManualRequirement()),
    HOSIDIUS(new ManualRequirement(), new ManualRequirement()),
    VARLAMORE(new ManualRequirement(), new ManualRequirement());

    @Getter 
    private final Requirement empty;

    @Getter 
    private final Requirement ready;

    HerbPatch(Requirement emptyRequirement, Requirement ready) {
        this.empty = emptyRequirement;
        this.ready = ready;
    }
}
