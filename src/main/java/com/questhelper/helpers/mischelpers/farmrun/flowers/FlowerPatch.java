package com.questhelper.helpers.mischelpers.farmrun.flowers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.questhelper.helpers.mischelpers.farmrun.utils.CommonPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;

public enum FlowerPatch implements FarmingPatchRequirements {
    UNKNOWN(new ManualRequirement(), new ManualRequirement()),
    ARDOUGNE(new ManualRequirement(), new ManualRequirement()),
    CATHERBY(new ManualRequirement(), new ManualRequirement()),
    FALADOR(new ManualRequirement(), new ManualRequirement()),
    FARMING_GUILD(new ManualRequirement(), new ManualRequirement()),
    MORYTANIA(new ManualRequirement(), new ManualRequirement()),
    PRIFDDINAS(new ManualRequirement(), new ManualRequirement()),
    KOUREND(new ManualRequirement(), new ManualRequirement()),
    CIVITAS_ILLA_FORTIS(new ManualRequirement(), new ManualRequirement());

    private final ManualRequirement emptyRequirement;

    private final ManualRequirement readyRequirement;

    FlowerPatch(ManualRequirement emptyRequirement, ManualRequirement readyRequirement) {
        this.emptyRequirement = emptyRequirement;
        this.readyRequirement = readyRequirement;
    }

    @Override
    public Requirement getPatchEmptyRequirement() {
        return emptyRequirement;
    }

    @Override
    public Requirement getPatchReadyRequirement() {
        return readyRequirement;
    }

    @Override
    @Nullable
    public QuestStep getHarvestStep(QuestHelper questHelper) {
        ObjectStep harvestStep = null;
        List<Requirement> conditionsToHide = new ArrayList<>();
        switch (this) {
            case ARDOUGNE:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7849,
                        new WorldPoint(2666, 3374, 0),
                        "Harvest your flowers from the Ardougne patch.",
                        CommonPatchRequirements.getArdougneCloak());
                break;
            case CATHERBY:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7848,
                        new WorldPoint(2809, 3463, 0),
                        "Harvest your flowers from the Catherby patch.",
                        CommonPatchRequirements.getCatherybyTeleport());
                break;
            case FALADOR:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7847,
                        new WorldPoint(3054, 3307, 0),
                        "Harvest your flowers from the Falador patch.",
                        CommonPatchRequirements.getExplorerRing());
                break;
            case FARMING_GUILD:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_33649,
                        new WorldPoint(1261, 3276, 0),
                        "Harvest your flowers from the Farming Guild patch.",
                        CommonPatchRequirements.getFarmingGuildTeleport());
                break;
            case MORYTANIA:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7850,
                        new WorldPoint(3601, 3525, 0),
                        "Harvest your flowers from the Morytania patch.",
                        CommonPatchRequirements.getEctophial());
                break;
            case PRIFDDINAS:
                assert false : "I don't have access to this patch. Please add it.";
                return null;
            case KOUREND:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_27111,
                        new WorldPoint(1734, 3554, 0),
                        "Harvest your flowers from the Hosidius patch.",
                        CommonPatchRequirements.getHosidiusTeleport());
                break;
            case CIVITAS_ILLA_FORTIS:
                harvestStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_50693,
                        new WorldPoint(1585, 3098, 0),
                        "Harvest your flowers from the Farming Guild patch.",
                        CommonPatchRequirements.getHunterWhistle());
                break;
            default:
                return null;
        }

        conditionsToHide.add(new Conditions(LogicType.NOR, getPatchReadyRequirement()));
        conditionsToHide.removeIf(filter -> filter == null);
        harvestStep.conditionToHideInSidebar(
                new Conditions(LogicType.OR, conditionsToHide));
        harvestStep.addSubSteps(getPlantStep(questHelper));
        return harvestStep;

    }

    @Override
    @Nullable
    public QuestStep getPlantStep(QuestHelper questHelper) {
        QuestStep plantStep = null;
        List<Requirement> conditionsToHide = new ArrayList<>();
        switch (this) {
            case ARDOUGNE:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7849,
                        new WorldPoint(2666, 3374, 0),
                        "Plant your flowers in the Ardougne patch.",
                        CommonPatchRequirements.getArdougneCloak());
                break;
            case CATHERBY:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7848,
                        new WorldPoint(2809, 3463, 0),
                        "Plant your flowers in the Catherby patch.",
                        CommonPatchRequirements.getCatherybyTeleport());
                break;
            case FALADOR:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7847,
                        new WorldPoint(3054, 3307, 0),
                        "Plant your flowers in the Falador patch.",
                        CommonPatchRequirements.getExplorerRing());
                break;
            case FARMING_GUILD:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_33649,
                        new WorldPoint(1261, 3276, 0),
                        "Plant your flowers in the Farming Guild patch.",
                        CommonPatchRequirements.getFarmingGuildTeleport());
                break;
            case MORYTANIA:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_7850,
                        new WorldPoint(3601, 3525, 0),
                        "Plant your flowers in the Morytania patch.",
                        CommonPatchRequirements.getEctophial());
                break;
            case PRIFDDINAS:
                assert false : "I don't have access to this patch. Please add it.";
                return null;
            case KOUREND:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_27111,
                        new WorldPoint(1734, 3554, 0),
                        "Plant your flowers in the Hosidius patch.",
                        CommonPatchRequirements.getHosidiusTeleport());
                break;
            case CIVITAS_ILLA_FORTIS:
                plantStep = new ObjectStep(
                        questHelper,
                        NullObjectID.NULL_50693,
                        new WorldPoint(1585, 3098, 0),
                        "Plant your flowers in the Farming Guild patch.",
                        CommonPatchRequirements.getHunterWhistle());
                break;
            default:
                return null;
        }
        plantStep.addIcon(ItemID.LIMPWURT_SEED);
        conditionsToHide.add(new Conditions(LogicType.NOR, getPatchEmptyRequirement()));
        plantStep.conditionToHideInSidebar(
                new Conditions(LogicType.OR, conditionsToHide));
        return plantStep;
    }

    @Override
    public void setPatchHarvestable(boolean isHarvestable) {
        readyRequirement.setShouldPass(isHarvestable);
    }

    @Override
    public void setPatchPlantable(boolean isPlantable) {
        emptyRequirement.setShouldPass(isPlantable);
    }

    @Override
    public List<ItemRequirement> getPatchItemRecommendations() {
        switch (this) {
            case ARDOUGNE:
                return List.of(CommonPatchRequirements.getArdougneCloak());
            case CATHERBY:
                return List.of(CommonPatchRequirements.getCatherybyTeleport());
            case FALADOR:
                return List.of(CommonPatchRequirements.getExplorerRing());
            case FARMING_GUILD:
                return List.of(CommonPatchRequirements.getFarmingGuildTeleport());
            case MORYTANIA:
                return List.of(CommonPatchRequirements.getEctophial());
            case PRIFDDINAS:
                return List.of(CommonPatchRequirements.getPrifddinasTeleport());
            case KOUREND:
                return List.of(CommonPatchRequirements.getHosidiusTeleport());
            case CIVITAS_ILLA_FORTIS:
                return List.of(CommonPatchRequirements.getHunterWhistle());
            default:
                return List.of();
        }

    }

    @Override
    @Nullable
    public Requirement getConditionsToHideRequirement() {
        switch (this) {
            case FARMING_GUILD:
                return new Conditions(LogicType.NOR, CommonPatchRequirements.getFarmingGuildAccess());
            case PRIFDDINAS:
                return new Conditions(LogicType.NOR, CommonPatchRequirements.getPrifddinasAccess());
            case CIVITAS_ILLA_FORTIS:
                return new Conditions(LogicType.NOR, CommonPatchRequirements.getVarlamoreAccess());
            default:
                return null;
        }
    }
}
