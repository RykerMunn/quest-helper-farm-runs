package com.questhelper.helpers.mischelpers.farmrun.herbs;

import java.util.ArrayList;
import java.util.List;

import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.mischelpers.farmrun.AbstractFarmRun;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.util.Text;

/**
 * Manages herb runs.
 */
public class HerbRun2 extends AbstractFarmRun {

    private List<HerbPatch> patches;
    private DetailedQuestStep waitForHerbsToGrow;
    private ConditionalStep herbRunStep;

    private PanelDetails herbRunDetails;

    private ItemRequirement seedItemRequirement;

    private List<Requirement> patchRequirements;

    private enum HerbSeed {
        GUAM(ItemID.GUAM_SEED), MARRENTILL(ItemID.MARRENTILL_SEED), TARROMIN(ItemID.TARROMIN_SEED),
        HARRALANDER(ItemID.HARRALANDER_SEED),
        RANARR(ItemID.RANARR_SEED), TOADFLAX(ItemID.TOADFLAX_SEED), IRIT(ItemID.IRIT_SEED),
        AVANTOE(ItemID.AVANTOE_SEED), KWUARM(ItemID.KWUARM_SEED),
        SNAPDRAGON(ItemID.SNAPDRAGON_SEED), HUASCA(ItemID.HUASCA_SEED), CADANTINE(ItemID.CADANTINE_SEED),
        LATANDYME(ItemID.LANTADYME_SEED),
        DWARF_WEED(ItemID.DWARF_WEED_SEED), TORSTOL(ItemID.TORSTOL_SEED);

        final int seedID;

        HerbSeed(int seedID) {
            this.seedID = seedID;
        }
    }

    private final String HERB_SEEDS = "farmrun_seed_herb";

    public HerbRun2(Client client, QuestHelper questHelper, FarmingWorld farmingWorld,
            FarmingHandler farmingHandler) {
        super(client, questHelper, farmingWorld, farmingHandler);
        this.patchRequirements = new ArrayList<>();
    }

    @Override
    protected ConditionalStep loadStep() {
        setupConditions();
        setupSteps();
        addSteps();

        return herbRunStep;
    }

    @Override
    protected void setupConditions() {
        var configManager = questHelper.getConfigManager();
        patches = List.of(HerbPatch.values());
        seedItemRequirement = new ItemRequirement("Seeds of your choice", ItemID.GUAM_SEED);
        String seedName = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS);

        if (seedName != null) {
            try {
                seedItemRequirement.setId(HerbSeed.valueOf(seedName).seedID);
            } catch (IllegalArgumentException err) {
                seedName = HerbSeed.GUAM.name();
                configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS,
                        HerbSeed.GUAM);
            }
            seedItemRequirement.setName(Text.titleCase(HerbSeed.valueOf(seedName)) + " seed");
        } else {
            configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS, HerbSeed.GUAM);
        }

        addRequiredItems(seedItemRequirement);
    }

    @Override
    protected void setupSteps() {
        waitForHerbsToGrow = new DetailedQuestStep(questHelper, "Wait for your herbs to grow.");

        // conditional step to group everything.
        herbRunStep = new ConditionalStep(questHelper, waitForHerbsToGrow);
    }

    @Override
    protected void addSteps() {
        for (HerbPatch patch : patches) {
            var ready = patch.getPatchReadyRequirement();
            var harvestStep = patch.getHarvestStep(questHelper);
            if (harvestStep == null)
                continue;

            herbRunStep.addStep(ready, harvestStep);
            var plantStep = patch.getPlantStep(questHelper);
            herbRunStep.addStep(patch.getPatchEmptyRequirement(), plantStep);
            addRecommendedItems(patch.getPatchItemRecommendations());

            patchRequirements.add(ready);
            patchRequirements.add(patch.getPatchEmptyRequirement());
        }
        waitForHerbsToGrow.conditionToHideInSidebar(new Conditions(LogicType.OR, patchRequirements));
    }

    @Override
    protected PanelDetails getPanelDetails() {
        herbRunDetails = new PanelDetails("Herb Run", List.copyOf(herbRunStep.getSteps()));

        return herbRunDetails;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        int seedsNeeded = 0;
        for (FarmingPatch patch : farmingWorld.getTabs().get(Tab.HERB)) {

            CropState state = farmingHandler.predictPatch(patch);
            boolean isHarvestable = state == CropState.HARVESTABLE;
            boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD ||
                    state == null;

            if (isHarvestable || isPlantable) {
                ++seedsNeeded;
            }
            HerbPatch herbPatch = HerbPatch.UNKNOWN;
            if (patch.getRegion().getRegionID() == 6192) {
                herbPatch = HerbPatch.VARLAMORE;
            } else if (patch.getRegion().getRegionID() == 6967) {
                herbPatch = HerbPatch.HOSIDIUS;
            } else {
                herbPatch = HerbPatch.valueOf(patch.getRegion().getName().toUpperCase().replace(' ', '_'));
            }
            int activePatchIndex = patches.indexOf(herbPatch);
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
        if (event.getKey().equals(HERB_SEEDS)) {
            try {
                HerbSeed selectedSeed = HerbSeed.valueOf(event.getNewValue());
                seedItemRequirement.setId(selectedSeed.seedID);
                seedItemRequirement.setName(Text.titleCase(selectedSeed) + " seed");
                questHelper.getQuestHelperPlugin().refreshBank();
                questHelper.getQuestHelperPlugin().getClientThread().invokeLater(() -> {
                    // force the inventory requirements to update.
                    ItemAndLastUpdated inventoryData = QuestContainerManager.getInventoryData();
                    inventoryData.update(inventoryData.getLastUpdated() + 1, inventoryData.getItems());
                });
            } catch (IllegalArgumentException err) {
                questHelper.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                        HERB_SEEDS, HerbSeed.GUAM);
            }
        }
    }

}
