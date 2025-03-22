package com.questhelper.helpers.mischelpers.farmrun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

public abstract class FarmRun extends QuestStep {

    HashSet<ItemRequirement> recommendedItems;
    HashSet<ItemRequirement> requiredItems;

    public FarmRun(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper);
        this.farmingWorld = farmingWorld;
        this.farmingHandler = farmingHandler;
        requiredItems = new HashSet<>();
        recommendedItems = new HashSet<>();

        requiredItems.add(new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER));
        requiredItems.add(new ItemRequirement("Spade", ItemID.SPADE));
        requiredItems.add(new ItemRequirement("Rake", ItemID.RAKE)
                .hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2)));

        recommendedItems.add(new ItemRequirement("Magic secateurs", ItemID.MAGIC_SECATEURS));
    }

    protected FarmingWorld farmingWorld;

    protected FarmingHandler farmingHandler;

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

    protected List<ItemRequirement> getRequiredItems() {
        return Arrays.asList(requiredItems.toArray(ItemRequirement[]::new));
    }

    protected List<ItemRequirement> getRecommendedItems() {
        return Arrays.asList(recommendedItems.toArray(ItemRequirement[]::new));
    }
}
