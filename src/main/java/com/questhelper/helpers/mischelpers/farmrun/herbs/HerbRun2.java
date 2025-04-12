package com.questhelper.helpers.mischelpers.farmrun.herbs;

import java.util.List;

import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.mischelpers.farmrun.AbstractFarmRun;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.questhelpers.QuestHelper;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.timetracking.Tab;
/**
 * Manages herb runs.
 */
public class HerbRun2 extends AbstractFarmRun {

    private List<FarmingPatchRequirements> patches;

    private enum HerbSeed {
        GUAM(ItemID.GUAM_SEED), MARRENTILL(ItemID.MARRENTILL_SEED), TARROMIN(ItemID.TARROMIN_SEED),
        HARRALANDER(ItemID.HARRALANDER_SEED),
        RANARR(ItemID.RANARR_SEED), TOADFLAX(ItemID.TOADFLAX_SEED), IRIT(ItemID.IRIT_SEED),
        AVANTOE(ItemID.AVANTOE_SEED), KWUARM(ItemID.KWUARM_SEED),
        SNAPDRAGON(ItemID.SNAPDRAGON_SEED), HUASCA(ItemID.HUASCA_SEED), CADANTINE(ItemID.CADANTINE_SEED),
        LANTADYME(ItemID.LANTADYME_SEED),
        DWARF_WEED(ItemID.DWARF_WEED_SEED), TORSTOL(ItemID.TORSTOL_SEED);

        final int seedID;

        HerbSeed(int seedID) {
            this.seedID = seedID;
        }
    }

    private static final String HERB_SEEDS = "farmrun_seed_herb";

    public HerbRun2(QuestHelper questHelper, FarmingWorld farmingWorld, FarmingHandler farmingHandler) {
        super(questHelper, farmingWorld, farmingHandler, HERB_SEEDS, Tab.HERB, HerbSeed.GUAM);
        this.patches = List.of(HerbPatch.values());
        this.setGrowthStepText("Wait for your herbs to grow.");
        this.setSeedItemRequirementText("Herb seeds of your choice.");
    }

    @Override
    protected int getSeedID(String seedName) {
        try {
            return HerbSeed.valueOf(seedName.replace(' ', '_').toUpperCase()).seedID;
        } catch (IllegalArgumentException err) {
            questHelper.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                    HERB_SEEDS, HerbSeed.GUAM);
            return ItemID.GUAM_SEED;
        }
    }

    @Override
    protected Enum<?> getSeedEnum(String seedName) {
        try {
            return HerbSeed.valueOf(seedName.replace(' ', '_').toUpperCase());
        } catch (IllegalArgumentException err) {
            questHelper.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                    HERB_SEEDS, HerbSeed.GUAM);
            return HerbSeed.GUAM;
        }
    }

    @Override
    protected List<FarmingPatchRequirements> getPatches() {
        return patches;
    }

    @Override
    protected String getPanelTitle() {
        return "Herb Run";
    }

    @Override
    protected FarmingPatchRequirements getPatchRequirements(FarmingPatch patch) {
        HerbPatch herbPatch = HerbPatch.UNKNOWN;
        try {
            herbPatch = HerbPatch.valueOf(patch.getRegion().getName().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException err) {
            return HerbPatch.UNKNOWN;
        }
        return herbPatch;
    }

}
