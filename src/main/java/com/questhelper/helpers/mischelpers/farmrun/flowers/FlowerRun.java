package com.questhelper.helpers.mischelpers.farmrun.flowers;

import java.util.List;

import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.mischelpers.farmrun.AbstractFarmRun;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.timetracking.Tab;

public class FlowerRun extends AbstractFarmRun {
    private List<FarmingPatchRequirements> patches;

    private enum FlowerSeed {
        MARIGOLD(ItemID.MARIGOLD_SEED), ROSEMARY(ItemID.ROSEMARY_SEED),
        NASTURTIUM(ItemID.NASTURTIUM_SEED), WOAD(ItemID.WOAD_SEED),
        LIMPWURT(ItemID.LIMPWURT_SEED), WHITE_LILY(ItemID.WHITE_LILY_SEED);

        final int seedID;

        FlowerSeed(int seedID) {
            this.seedID = seedID;
        }
    }

    private static final String FLOWER_SEEDS = "farmrun_seed_flower";

    public FlowerRun(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper, farmingWorld, farmingHandler, FLOWER_SEEDS, Tab.FLOWER);
        this.patches = List.of(FlowerPatch.values());
        this.setGrowthStepText("Wait for your flowers to grow.");
        this.setSeedItemRequirementText("Flower seeds of your choice.");
    }

    @Override
    protected int getSeedID(String seedName) {
        try {
            return FlowerSeed.valueOf(seedName.replace(' ', '_').toUpperCase()).seedID;
        } catch (IllegalArgumentException err) {
            questHelper.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                    FLOWER_SEEDS, FlowerSeed.MARIGOLD);
            return ItemID.MARIGOLD_SEED;
        }
    }

    @Override
    protected Enum<?> getSeedEnum(String seedName) {
        try {
            return FlowerSeed.valueOf(seedName.replace(' ', '_').toUpperCase());
        } catch (IllegalArgumentException err) {
            questHelper.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                    FLOWER_SEEDS, FlowerSeed.MARIGOLD);
            return FlowerSeed.MARIGOLD;
        }
    }

    @Override
    protected List<FarmingPatchRequirements> getPatches() {
        return patches;
    }

    @Override
    protected FarmingPatchRequirements getPatchRequirements(FarmingPatch patch) {
        FlowerPatch flowerPatch = FlowerPatch.UNKNOWN;
        try {
            flowerPatch = FlowerPatch.valueOf(patch.getRegion().getName().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException err) {
            return FlowerPatch.UNKNOWN;
        }
        return flowerPatch;
    }

    @Override
    protected String getPanelTitle() {
        return "Flower Run";
    }
}
