package com.questhelper.helpers.mischelpers.farmrun.herbs;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.questhelper.helpers.mischelpers.farmrun.FarmRun;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.CropState;

/**
 * Manages herb runs.
 */
public class HerbRun2 extends FarmRun {

    private List<HerbPatch> patches;
    private DetailedQuestStep waitForHerbsToGrow;
    private ConditionalStep herbRunStep;

    private QuestStep herbRunSidebar;
    private PanelDetails herbRunDetails;

    public HerbRun2(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper, farmingWorld, farmingHandler);
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
        patches = List.of(HerbPatch.values());
    }

    @Override
    protected void setupSteps() {
        waitForHerbsToGrow = new DetailedQuestStep(questHelper, "Wait for your herbs to grow.");
        // conditional step to group everything.
        herbRunStep = new ConditionalStep(questHelper, waitForHerbsToGrow);
    }

    @Override
    protected void addSteps() {
        herbRunSidebar = new DetailedQuestStep(questHelper, "Complete your herb run.");
        for (HerbPatch patch : patches) {
            var ready = patch.getPatchReadyRequirement();
            var harvestStep = patch.getHarvestStep(questHelper);
            if (harvestStep == null)
                continue;
            herbRunStep.addStep(ready, harvestStep);
            var plantStep = patch.getPlantStep(questHelper);
            herbRunStep.addStep(patch.getPatchEmptyRequirement(), plantStep);

            herbRunSidebar.addSubSteps(harvestStep, plantStep);
        }
        herbRunSidebar.addSubSteps(herbRunStep);

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
                seedsNeeded++;
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
                continue;
            }
            patches.get(activePatchIndex).setPatchHarvestable(isHarvestable);
            patches.get(activePatchIndex).setPatchPlantable(isPlantable);

        }
    }

}
