package com.questhelper.helpers.mischelpers.farmrun;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.Client;
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

    public AbstractFarmRun(Client client, QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper);
        this.farmingWorld = farmingWorld;
        this.farmingHandler = farmingHandler;
        this.client = client;
        this.clientThread = questHelper.getQuestHelperPlugin().getClientThread();
        requiredItems = new HashSet<>();
        recommendedItems = new HashSet<>();
        compostItemRequirement.setDisplayMatchedItemName(true);
        requiredItems.add(new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER));
        requiredItems.add(new ItemRequirement("Spade", ItemID.SPADE));
        requiredItems.add(new ItemRequirement("Rake", ItemID.RAKE)
                .hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2)));
        requiredItems.add(compostItemRequirement);

        recommendedItems.add(new ItemRequirement("Magic secateurs", ItemID.MAGIC_SECATEURS));

    }

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
        List<ItemRequirement> items = Arrays.asList(requiredItems.toArray(ItemRequirement[]::new));
        items.sort(itemRequirementComparator);
        return items;
    }

    protected List<ItemRequirement> getRecommendedItems() {
        List<ItemRequirement> items = Arrays.asList(recommendedItems.toArray(ItemRequirement[]::new));
        items.sort(itemRequirementComparator);
        return items;
    }

    protected void setRequiredCompostQuantity(int quantity) {
        compostItemRequirement.quantity(quantity);
    }
}
