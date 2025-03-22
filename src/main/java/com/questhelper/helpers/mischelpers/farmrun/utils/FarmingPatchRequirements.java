package com.questhelper.helpers.mischelpers.farmrun.utils;

import javax.annotation.Nullable;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.QuestStep;

public interface FarmingPatchRequirements {
    public Requirement getPatchEmptyRequirement();

    public Requirement getPatchReadyRequirement();

    @Nullable
    public QuestStep getHarvestStep(QuestHelper questHelper);

    @Nullable
    public QuestStep getPlantStep(QuestHelper questHelper);

    public void setPatchHarvestable(boolean isHarvestable);
    public void setPatchPlantable(boolean isPlantable);
}
