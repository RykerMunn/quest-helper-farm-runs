package com.questhelper.helpers.mischelpers.farmrun;

import java.util.Arrays;
import java.util.List;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;

public abstract class FarmRun extends QuestStep {

    List<ItemRequirement> recommendedItems;
    List<ItemRequirement> requiredItems;

    public FarmRun(QuestHelper questHelper) {
        super(questHelper);
        requiredItems = Arrays.asList(new ItemRequirement[] {
                new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER),
                new ItemRequirement("Spade", ItemID.SPADE),
                new ItemRequirement("Rake", ItemID.RAKE)
        });
    }

    protected abstract ConditionalStep loadStep();

    protected abstract void setupConditions();

    protected abstract void setupSteps();

    protected abstract void addSteps();

    protected abstract PanelDetails getPanelDetails();

    protected void setRecommended(ItemRequirement... items) {
        this.recommendedItems.addAll(recommendedItems);
    }

    protected void setRequired(ItemRequirement... items) {
        this.requiredItems.addAll(Arrays.asList(items));
    }
}
