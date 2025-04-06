package com.questhelper.helpers.mischelpers.farmrun;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.questhelper.helpers.mischelpers.farmrun.flowers.FlowerRun;
import com.questhelper.helpers.mischelpers.farmrun.herbs.HerbRun2;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.helpers.mischelpers.farmrun.utils.PatchImplementation;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.util.RequirementBuilder;

import lombok.NonNull;

import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

public final class FarmRunBuilder {

    private PatchImplementation patchImplementation;
    private QuestHelper questHelper;
    private FarmingWorld farmingWorld;
    private FarmingHandler farmingHandler;
    private EventBus eventBus;
    private Predicate<Client> hideCondition;

    private FarmRunBuilder(
            PatchImplementation patchImplementation,
            QuestHelper questHelper,
            FarmingWorld farmingWorld,
            FarmingHandler farmingHandler,
            EventBus eventBus,
            Predicate<Client> hideCondition) {
        this.patchImplementation = patchImplementation;
        this.questHelper = questHelper;
        this.farmingWorld = farmingWorld;
        this.farmingHandler = farmingHandler;
        this.eventBus = eventBus;
        this.hideCondition = hideCondition;
    }

    public static FarmRunBuilder builder(PatchImplementation patchImplementation, @Nonnull QuestHelper questHelper,
            @Nonnull FarmingWorld farmingWorld,
            @Nonnull FarmingHandler farmingHandler,
            @NonNull EventBus eventBus) {
        return new FarmRunBuilder(patchImplementation, questHelper, farmingWorld, farmingHandler, eventBus, null);
    }

    public static FarmRunBuilder builder() {
        return new FarmRunBuilder(null, null, null, null, null, null);
    }

    public FarmRunBuilder withPatchImplementation(PatchImplementation patchImplementation) {
        this.patchImplementation = patchImplementation;
        return this;
    }

    public FarmRunBuilder withQuestHelper(@Nonnull QuestHelper questHelper) {
        this.questHelper = questHelper;
        return this;
    }

    public FarmRunBuilder withFarmingWorld(@Nonnull FarmingWorld farmingWorld) {
        this.farmingWorld = farmingWorld;
        return this;
    }

    public FarmRunBuilder withFarmingHandler(@Nonnull FarmingHandler farmingHandler) {
        this.farmingHandler = farmingHandler;
        return this;
    }

    public FarmRunBuilder withEventBus(@NonNull EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }

    public FarmRunBuilder withHideCondition(@NonNull Predicate<Client> hidePredicate) {
        this.hideCondition = hidePredicate;
        return this;
    }

    /**
     * Build the farm run based on the patch implementation.
     * 
     * @apiNote this can return null if no implementation is found.
     * @return the farm run implementation
     */
    @Nullable
    public AbstractFarmRun build() {
        assert patchImplementation != null;
        assert questHelper != null;
        assert farmingWorld != null;
        assert farmingHandler != null;
        AbstractFarmRun farmRun = null;
        switch (patchImplementation) {
            case HERB:
                farmRun = new HerbRun2(questHelper, farmingWorld, farmingHandler);
                break;
            case FLOWER:
                farmRun = new FlowerRun(questHelper, farmingWorld, farmingHandler);
                break;
            default:
                assert false : "No implementation found for " + patchImplementation;
                break;
        }

        if (eventBus != null && farmRun != null) {
            eventBus.register(farmRun);
        }

        if (hideCondition != null && farmRun != null) {
            farmRun.setConditionToHide(RequirementBuilder.builder().check(hideCondition).build());
        }
        
        return farmRun;
    }
}
