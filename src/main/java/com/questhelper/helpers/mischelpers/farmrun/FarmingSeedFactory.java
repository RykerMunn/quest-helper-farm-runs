package com.questhelper.helpers.mischelpers.farmrun;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.inject.Singleton;

import net.runelite.api.ItemID;

@Singleton
public class FarmingSeedFactory {

    private final List<FarmingSeed> herbSeeds = new ArrayList<>();
    private final List<FarmingSeed> flowerSeeds = new ArrayList<>();
    private final List<FarmingSeed> bushSeeds = new ArrayList<>();

    public FarmingSeedFactory() {
        setHerbSeeds();
        setFlowerSeeds();
        setBushSeeds();
    }

    @Nullable
    public FarmingSeed[] getValidSeeds(PatchImplementation patchImplementation) {
        List<FarmingSeed> seeds = null;
        switch (patchImplementation) {
            case BELLADONNA:
                seeds = new ArrayList<FarmingSeed>();
                seeds.add(new FarmingSeed(ItemID.BELLADONNA_SEED, "Belladonna"));
                break;
            case BUSH:
                seeds = bushSeeds;
                break;
            case FLOWER:
                seeds = flowerSeeds;
                break;
            case HERB:
                seeds = herbSeeds;
                break;
            default:
                throw new IllegalArgumentException("Unknown patch type: " + patchImplementation);
        }
        if (seeds == null) {
            return null;
        }
        return seeds.toArray(new FarmingSeed[0]);
    }

    private final void setHerbSeeds() {
        herbSeeds.add(new FarmingSeed(ItemID.GUAM_SEED, "Guam"));
        herbSeeds.add(new FarmingSeed(ItemID.MARRENTILL_SEED, "Marrentill"));
        herbSeeds.add(new FarmingSeed(ItemID.TARROMIN_SEED, "Tarromin"));
        herbSeeds.add(new FarmingSeed(ItemID.HARRALANDER_SEED, "Harralander"));
        herbSeeds.add(new FarmingSeed(ItemID.RANARR_SEED, "Ranarr"));
        herbSeeds.add(new FarmingSeed(ItemID.TOADFLAX_SEED, "Toadflax"));
        herbSeeds.add(new FarmingSeed(ItemID.IRIT_SEED, "Irit"));
        herbSeeds.add(new FarmingSeed(ItemID.AVANTOE_SEED, "Avantoe"));
        herbSeeds.add(new FarmingSeed(ItemID.KWUARM_SEED, "Kwuarm"));
        herbSeeds.add(new FarmingSeed(ItemID.SNAPDRAGON_SEED, "Snapdragon"));
        herbSeeds.add(new FarmingSeed(ItemID.HUASCA_SEED, "Huasca"));
        herbSeeds.add(new FarmingSeed(ItemID.CADANTINE_SEED, "Cadantine"));
        herbSeeds.add(new FarmingSeed(ItemID.LANTADYME_SEED, "Lantadyme"));
        herbSeeds.add(new FarmingSeed(ItemID.DWARF_WEED_SEED, "Dwarf weed"));
        herbSeeds.add(new FarmingSeed(ItemID.TORSTOL_SEED, "Torstol"));
    }

    private final void setFlowerSeeds() {
        flowerSeeds.add(new FarmingSeed(ItemID.MARIGOLD_SEED, "Marigold"));
        flowerSeeds.add(new FarmingSeed(ItemID.ROSEMARY_SEED, "Rosemary"));
        flowerSeeds.add(new FarmingSeed(ItemID.NASTURTIUM_SEED, "Nasturtium"));
        flowerSeeds.add(new FarmingSeed(ItemID.WOAD_SEED, "Woad"));
        flowerSeeds.add(new FarmingSeed(ItemID.LIMPWURT_SEED, "Limpwurt"));
        flowerSeeds.add(new FarmingSeed(ItemID.WHITE_LILY_SEED, "White lily"));
    }

    private final void setBushSeeds() {
        bushSeeds.add(new FarmingSeed(ItemID.REDBERRY_SEED, "Redberry"));
        bushSeeds.add(new FarmingSeed(ItemID.CADAVABERRY_SEED, "Cadavaberry"));
        bushSeeds.add(new FarmingSeed(ItemID.DWELLBERRY_SEED, "Dwellberry"));
        bushSeeds.add(new FarmingSeed(ItemID.JANGERBERRY_SEED, "Jangerberry"));
        bushSeeds.add(new FarmingSeed(ItemID.WHITEBERRY_SEED, "Whiteberry"));
        bushSeeds.add(new FarmingSeed(ItemID.POISON_IVY_SEED, "Poison ivy"));
    }
}
