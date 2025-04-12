package com.questhelper.helpers.mischelpers.farmrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.config.ConfigKeys;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.util.Text;

public abstract class AbstractFarmRun extends QuestStep {

    private HashSet<ItemRequirement> recommendedItems;
    private HashSet<ItemRequirement> requiredItems;

    private ItemRequirement compostItemRequirement = new ItemRequirement("Compost", ItemCollections.COMPOST);

    private final Comparator<ItemRequirement> itemRequirementComparator = Comparator
            .comparing(ItemRequirement::getName);

    private final HashSet<ItemRequirement> alwaysRequiredItems = new HashSet<>();
    private final HashSet<ItemRequirement> alwaysRecommendedItems = new HashSet<>();

    private ItemRequirement seedItemRequirement;
    private List<Requirement> patchRequirements = new ArrayList<>();

    private boolean initialized = false;

    private ConditionalStep step;
    private DetailedQuestStep waitForGrowthStep;

    private String growthStepText = "Wait for the crops to grow.";
    private String seedsRequirementText = "Seeds";

    private String configKey = "";

    private PanelDetails panelDetails;

    private Tab timeTrackingTab;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Requirement conditionToHide;

    protected FarmingWorld farmingWorld;

    protected FarmingHandler farmingHandler;

    private Enum<?> defaultSeed;

    public AbstractFarmRun(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler,
            String configKey, Tab tab, Enum<?> defaultSeed) {
        super(questHelper);
        this.farmingWorld = farmingWorld;
        this.farmingHandler = farmingHandler;
        this.client = questHelper.getQuestHelperPlugin().getClient();
        this.clientThread = questHelper.getQuestHelperPlugin().getClientThread();
        this.configKey = configKey;
        assert this.configKey != null && !this.configKey.isEmpty() : "Config key cannot be null or empty";
        this.timeTrackingTab = tab;
        this.defaultSeed = defaultSeed;
        requiredItems = new HashSet<>();
        recommendedItems = new HashSet<>();
        compostItemRequirement.setDisplayMatchedItemName(true);
        alwaysRequiredItems.add(new ItemRequirement("Seed dibber", ItemID.DIBBER)
                .hideConditioned(new RuneliteRequirement(questHelper.getConfigManager(),
                        ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey(), "true",
                        "Completed the Barbarian bare-handed farming training.")));
        alwaysRequiredItems.add(new ItemRequirement("Spade", ItemID.SPADE));
        alwaysRequiredItems.add(new ItemRequirement("Rake", ItemID.RAKE)
                .hideConditioned(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2)));
        alwaysRequiredItems.add(compostItemRequirement);

        alwaysRecommendedItems.add(new ItemRequirement("Magic secateurs", ItemID.FAIRY_ENCHANTED_SECATEURS));
    }

    public boolean isInitialized() {
        return initialized;
    }

    public ConditionalStep loadStep() {
        setupConditions();
        setupSteps();
        addSteps();
        this.initialized = true;
        return step;
    }

    private void setupConditions() {
        var configManager = questHelper.getConfigManager();
        this.seedItemRequirement = new ItemRequirement(this.seedsRequirementText, ItemID.POTATO_SEED);
        String seedName = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                this.configKey);

        if (seedName != null) {
            this.seedItemRequirement.setId(getSeedID(seedName));
            this.seedItemRequirement.setName(Text.titleCase(getSeedEnum(seedName)) + " seed");
        }

        addRequiredItems(this.seedItemRequirement);
    }

    private void setupSteps() {
        this.waitForGrowthStep = new DetailedQuestStep(questHelper, this.growthStepText);

        this.step = new ConditionalStep(questHelper, this.waitForGrowthStep);
    }

    private void addSteps() {
        for (FarmingPatchRequirements patch : getPatches()) {
            var ready = patch.getPatchReadyRequirement();
            var harvestStep = patch.getHarvestStep(questHelper);
            if (harvestStep == null)
                continue;

            step.addStep(ready, harvestStep);
            var plantStep = patch.getPlantStep(questHelper);
            step.addStep(patch.getPatchEmptyRequirement(), plantStep);
            addRecommendedItems(patch.getPatchItemRecommendations());

            patchRequirements.add(ready);
            patchRequirements.add(patch.getPatchEmptyRequirement());
        }
        this.waitForGrowthStep.conditionToHideInSidebar(new Conditions(LogicType.OR, patchRequirements));
    }

    public PanelDetails getPanelDetails() {
        if (this.panelDetails != null) {
            return this.panelDetails;
        }
        this.panelDetails = new PanelDetails(getPanelTitle(), List.copyOf(step.getSteps()));

        return this.panelDetails;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!this.initialized)
            return;
        int seedsNeeded = 0;
        var patches = getPatches();
        for (FarmingPatch patch : farmingWorld.getTabs().get(timeTrackingTab)) {
            CropState state = farmingHandler.predictPatch(patch);
            boolean isHarvestable = state == CropState.HARVESTABLE;
            boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD ||
                    state == null;

            if (isHarvestable || isPlantable) {
                ++seedsNeeded;
            }

            FarmingPatchRequirements patchRequirements = getPatchRequirements(patch);

            int activePatchIndex = patches.indexOf(patchRequirements);
            if (activePatchIndex == -1) {
                --seedsNeeded;
                continue;
            }
            Requirement hideCondition = patches.get(activePatchIndex).getConditionsToHideRequirement();
            if (hideCondition != null) {
                if (hideCondition.check(client)) {
                    --seedsNeeded;
                    continue;
                }
            }
            patches.get(activePatchIndex).setPatchHarvestable(isHarvestable);
            patches.get(activePatchIndex).setPatchPlantable(isPlantable);
        }
        seedItemRequirement.setQuantity(seedsNeeded);
        setRequiredCompostQuantity(seedsNeeded);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
            return;
        if (event.getKey().equals(this.configKey)) {
            String seedName = event.getNewValue();
            if (seedName == null || seedName.isEmpty()) {
                return;
            }
            Enum<?> seedEnum = getSeedEnum(seedName);
            if (seedEnum == null) {
                return;
            }
            seedItemRequirement.setId(getSeedID(seedName));
            seedItemRequirement.setName(Text.titleCase(seedEnum) + " seed");
            questHelper.getQuestHelperPlugin().refreshBank();
            questHelper.getQuestHelperPlugin().getClientThread().invokeLater(() -> {
                // force the inventory requirements to update.
                ItemAndLastUpdated inventoryData = QuestContainerManager.getInventoryData();
                inventoryData.update(inventoryData.getLastUpdated() + 1, inventoryData.getItems());
            });
        }
    }

    protected abstract int getSeedID(String seedName);

    protected abstract Enum<?> getSeedEnum(String seedName);

    protected abstract List<FarmingPatchRequirements> getPatches();

    protected abstract FarmingPatchRequirements getPatchRequirements(FarmingPatch patch);

    protected abstract String getPanelTitle();

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

    protected void setGrowthStepText(String text) {
        this.growthStepText = text;
    }

    protected void setSeedItemRequirementText(String text) {
        this.seedsRequirementText = text;
    }

    protected void setTimeTrackingTab(Tab tab) {
        this.timeTrackingTab = tab;
    }
}
