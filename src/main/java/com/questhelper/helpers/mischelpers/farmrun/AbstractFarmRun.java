package com.questhelper.helpers.mischelpers.farmrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.questhelper.collections.ItemCollections;
import com.questhelper.config.ConfigKeys;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.client.events.ConfigChanged;

public abstract class AbstractFarmRun extends QuestStep {

    private HashSet<ItemRequirement> recommendedItems;
    private HashSet<ItemRequirement> requiredItems;

    private ItemRequirement compostItemRequirement = new ItemRequirement("Compost", ItemCollections.COMPOST);

    private final Comparator<ItemRequirement> itemRequirementComparator = Comparator
            .comparing(ItemRequirement::getName);

    private final HashSet<ItemRequirement> alwaysRequiredItems = new HashSet<>();
    private final HashSet<ItemRequirement> alwaysRecommendedItems = new HashSet<>();

    public AbstractFarmRun(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper);
        this.farmingWorld = farmingWorld;
        this.farmingHandler = farmingHandler;
        this.client = questHelper.getQuestHelperPlugin().getClient();
        this.clientThread = questHelper.getQuestHelperPlugin().getClientThread();
        requiredItems = new HashSet<>();
        recommendedItems = new HashSet<>();
        compostItemRequirement.setDisplayMatchedItemName(true);
        alwaysRequiredItems.add(new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER)
                .hideConditioned(new RuneliteRequirement(questHelper.getConfigManager(),
                        ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey(), "true",
                        "Completed the Barbarian bare-handed farming training.")));
        alwaysRequiredItems.add(new ItemRequirement("Spade", ItemID.SPADE));
        alwaysRequiredItems.add(new ItemRequirement("Rake", ItemID.RAKE)
                .hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2)));
        alwaysRequiredItems.add(compostItemRequirement);

        alwaysRecommendedItems.add(new ItemRequirement("Magic secateurs", ItemID.MAGIC_SECATEURS));
    }

    public abstract boolean isInitialized();

    protected FarmingWorld farmingWorld;

    protected FarmingHandler farmingHandler;

    protected abstract ConditionalStep loadStep();

    protected abstract void setupConditions();

    protected abstract void setupSteps();

    protected abstract void addSteps();

    protected abstract PanelDetails getPanelDetails();

    protected abstract void onGameTick(GameTick event);

    protected abstract void onConfigChanged(ConfigChanged event);

    protected void setRecommended(ItemRequirement... items) {
        this.recommendedItems.clear();
        this.recommendedItems.addAll(Arrays.asList(items));
    }

    protected void setRequired(ItemRequirement... items) {
        this.requiredItems.clear();
        this.requiredItems.addAll(Arrays.asList(items));
    }

    protected void addRecommendedItems(ItemRequirement... items) {
        this.addRecommendedItems(Arrays.asList(items));
    }

    protected void addRecommendedItems(List<ItemRequirement> items) {
        this.recommendedItems.addAll(items);
    }

    protected void addRequiredItems(ItemRequirement... items) {
        this.addRequiredItems(Arrays.asList(items));
    }

    protected void addRequiredItems(List<ItemRequirement> items) {
        this.requiredItems.addAll(items);
    }

    protected List<ItemRequirement> getRequiredItems() {
        List<ItemRequirement> alwaysRequiredList = new ArrayList<>(alwaysRequiredItems);
        alwaysRequiredList.sort(itemRequirementComparator);
        List<ItemRequirement> requiredList = new ArrayList<>(requiredItems);
        requiredList.sort(itemRequirementComparator);
        alwaysRequiredList.addAll(requiredList);
        return alwaysRequiredList;
    }

    protected List<ItemRequirement> getRecommendedItems() {
        List<ItemRequirement> alwaysRecommendedList = new ArrayList<>(alwaysRecommendedItems);
        alwaysRecommendedList.sort(itemRequirementComparator);
        List<ItemRequirement> recommendedList = new ArrayList<>(recommendedItems);
        recommendedList.sort(itemRequirementComparator);
        alwaysRecommendedList.addAll(recommendedList);
        return alwaysRecommendedList;
    }

    protected void setRequiredCompostQuantity(int quantity) {
        compostItemRequirement.quantity(quantity);
    }
}
