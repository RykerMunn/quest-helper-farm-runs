package com.questhelper.helpers.mischelpers.farmrun.herbs;

import java.util.ArrayList;
import java.util.List;

import com.questhelper.helpers.mischelpers.farmrun.utils.CommonPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

public enum HerbPatch implements FarmingPatchRequirements {
        UNKNOWN(new ManualRequirement(), new ManualRequirement()),
        ARDOUGNE(new ManualRequirement(), new ManualRequirement()),
        CATHERBY(new ManualRequirement(), new ManualRequirement()),
        FALADOR(new ManualRequirement(), new ManualRequirement()),
        FARMING_GUILD(new ManualRequirement(), new ManualRequirement()),
        HARMONY(new ManualRequirement(), new ManualRequirement()),
        MORYTANIA(new ManualRequirement(), new ManualRequirement()),
        TROLL_STRONGHOLD(new ManualRequirement(), new ManualRequirement()),
        WEISS(new ManualRequirement(), new ManualRequirement()),
        KOUREND(new ManualRequirement(), new ManualRequirement()),
        CIVITAS_ILLA_FORTIS(new ManualRequirement(), new ManualRequirement());

        private final ManualRequirement empty;

        private final ManualRequirement ready;

        private final ItemRequirement trollheimRunes = new ItemRequirements("Trollheim teleport runes",
                        new ItemRequirement("Law rune",
                                        ItemID.LAWRUNE, 2),
                        new ItemRequirement("Fire rune", ItemID.FIRERUNE, 2));
        private final ItemRequirement trollheimTablet = new ItemRequirement("Trollheim tablet",
                        ItemID.NZONE_TELETAB_TROLLHEIM);
        private final ItemRequirement trollheimTeleport = new ItemRequirements(LogicType.OR, "Trollheim teleport",
                        trollheimRunes, trollheimTablet)
                        .hideConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                                        QuestState.FINISHED));

        private final ItemRequirement icyBasalt = new ItemRequirement("Icy basalt", ItemID.WEISS_TELEPORT_BASALT)
                        .showConditioned(
                                        new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                                                        QuestState.FINISHED));

        private final ItemRequirement stonyBasalt = new ItemRequirement("Stony basalt",
                        ItemID.STRONGHOLD_TELEPORT_BASALT)
                        .showConditioned(
                                        new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                                                        QuestState.FINISHED));

        private final QuestRequirement accessToWeiss = new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                        QuestState.FINISHED);
        private final QuestRequirement accessToTrollStronghold = new QuestRequirement(
                        QuestHelperQuest.MY_ARMS_BIG_ADVENTURE, QuestState.FINISHED);

        HerbPatch(ManualRequirement emptyRequirement, ManualRequirement ready) {
                this.empty = emptyRequirement;
                this.ready = ready;

                trollheimTeleport.setTooltip(
                                "Trollheim teleport runes, tablet, or stony basalt (with Making Friends with My Arm)");
                trollheimTeleport.setQuantity(1);
        }

        public final Requirement getPatchEmptyRequirement() {
                return empty;
        }

        public final Requirement getPatchReadyRequirement() {
                return ready;
        }

        public QuestStep getHarvestStep(QuestHelper questHelper) {
                ObjectStep harvestStep = null;
                List<Requirement> conditionsToHide = new ArrayList<>();
                switch (this) {
                        case ARDOUGNE:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_3,
                                                new WorldPoint(2670, 3374, 0),
                                                "Harvest your herbs from the Ardougne patch.",
                                                CommonPatchRequirements.getArdougneCloak());
                                break;
                        case CATHERBY:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_2,
                                                new WorldPoint(2813, 3463, 0),
                                                "Harvest your herbs from the Catherby patch.",
                                                CommonPatchRequirements.getCatherybyTeleport());
                                break;
                        case FALADOR:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_1,
                                                new WorldPoint(3058, 3311, 0),
                                                "Harvest your herbs from the Falador patch.",
                                                CommonPatchRequirements.getExplorerRing());
                                break;
                        case FARMING_GUILD:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_7,
                                                new WorldPoint(1238, 3726, 0),
                                                "Harvest your herbs from the Farming Guild patch.",
                                                CommonPatchRequirements.getFarmingGuildTeleport());

                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case HARMONY:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_5,
                                                new WorldPoint(3789, 2837, 0),
                                                "Harvest your herbs from the Harmony patch.",
                                                CommonPatchRequirements.getHarmonyIslandTeleport());
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case MORYTANIA:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_4,
                                                new WorldPoint(3605, 3529, 0),
                                                "Harvest your herbs from the Morytania patch.",
                                                CommonPatchRequirements.getEctophial());
                        case TROLL_STRONGHOLD:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.MYARM_HERBPATCH,
                                                new WorldPoint(826, 3694, 0),
                                                "Harvest your herbs from the Troll Stronghold patch.",
                                                trollheimTeleport, stonyBasalt);
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case WEISS:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.MY2ARM_HERBPATCH,
                                                new WorldPoint(2848, 3934, 0),
                                                "Harvest your herbs from the Weiss patch.",
                                                icyBasalt);
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case KOUREND:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_6,
                                                new WorldPoint(1738, 3550, 0),
                                                "Harvest your herbs from the Hosidius patch.",
                                                CommonPatchRequirements.getHosidiusTeleport());
                                break;
                        case CIVITAS_ILLA_FORTIS:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_8,
                                                new WorldPoint(1582, 3094, 0),
                                                "Harvest your herbs from the Varlamore patch.",
                                                CommonPatchRequirements.getHunterWhistle());
                                conditionsToHide.add(getConditionsToHideRequirement());

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

        public QuestStep getPlantStep(QuestHelper questHelper) {
                QuestStep plantStep = null;
                List<Requirement> conditionsToHide = new ArrayList<>();
                switch (this) {
                        case ARDOUGNE:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_3,
                                                new WorldPoint(2670, 3374, 0),
                                                "Plant your seeds into the Ardougne patch.",
                                                CommonPatchRequirements.getArdougneCloak());
                                break;
                        case CATHERBY:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_2,
                                                new WorldPoint(2813, 3463, 0),
                                                "Plant your seeds into the Catherby patch.",
                                                CommonPatchRequirements.getCatherybyTeleport());
                                break;
                        case FALADOR:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_1,
                                                new WorldPoint(3058, 3311, 0),
                                                "Plant your seeds into the Faladar patch.",
                                                CommonPatchRequirements.getExplorerRing());
                                break;
                        case FARMING_GUILD:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_7,
                                                new WorldPoint(1238, 3726, 0),
                                                "Plant your seeds into the Farming Guild patch.",
                                                CommonPatchRequirements.getFarmingGuildTeleport());
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case HARMONY:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_5,
                                                new WorldPoint(3789, 2837, 0),
                                                "Plant your seeds into the Harmony patch.",
                                                CommonPatchRequirements.getHarmonyIslandTeleport());
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;

                        case MORYTANIA:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_4,
                                                new WorldPoint(3605, 3529, 0),
                                                "Plant your seeds into the Morytania patch.",
                                                CommonPatchRequirements.getEctophial());
                                break;
                        case TROLL_STRONGHOLD:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.MYARM_HERBPATCH,
                                                new WorldPoint(826, 3694, 0),
                                                "Plant your seeds into the Troll Stronghold patch.",
                                                trollheimTeleport, stonyBasalt);
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case WEISS:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.MY2ARM_HERBPATCH,
                                                new WorldPoint(2848, 3934, 0),
                                                "Plant your seeds into the Weiss patch.",
                                                icyBasalt);
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        case KOUREND:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_6,
                                                new WorldPoint(1738, 3550, 0),
                                                "Plant your seeds into the Hosidius patch.",
                                                CommonPatchRequirements.getHosidiusTeleport());
                                break;
                        case CIVITAS_ILLA_FORTIS:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                ObjectID.FARMING_HERB_PATCH_8,
                                                new WorldPoint(1582, 3094, 0),
                                                "Plant your seeds into the Varlamore patch.",
                                                CommonPatchRequirements.getHunterWhistle());
                                conditionsToHide.add(getConditionsToHideRequirement());
                                break;
                        default:
                                return null;
                }
                plantStep.addIcon(ItemID.RANARR_SEED);
                conditionsToHide.add(new Conditions(LogicType.NOR, getPatchEmptyRequirement()));
                plantStep.conditionToHideInSidebar(
                                new Conditions(LogicType.OR, conditionsToHide));
                return plantStep;
        }

        public void setPatchHarvestable(boolean isHarvestable) {
                ready.setShouldPass(isHarvestable);
        }

        public void setPatchPlantable(boolean isPlantable) {
                empty.setShouldPass(isPlantable);
        }

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
                        case HARMONY:
                                return List.of(CommonPatchRequirements.getHarmonyIslandTeleport()
                                                .hideConditioned(getConditionsToHideRequirement()));
                        case MORYTANIA:
                                return List.of(CommonPatchRequirements.getEctophial());
                        case TROLL_STRONGHOLD:
                                return List.of(trollheimTeleport, stonyBasalt);
                        case WEISS:
                                return List.of(icyBasalt);
                        case KOUREND:
                                return List.of(CommonPatchRequirements.getHosidiusTeleport());
                        case CIVITAS_ILLA_FORTIS:
                                return List.of(CommonPatchRequirements.getHunterWhistle());
                        default:
                                return List.of();
                }
        }

        public Requirement getConditionsToHideRequirement() {
                switch (this) {
                        case ARDOUGNE:
                                return null;
                        case CATHERBY:
                                return null;
                        case FALADOR:
                                return null;
                        case FARMING_GUILD:
                                return new Conditions(LogicType.NOR,
                                                CommonPatchRequirements.getFarmingGuildAccess());
                        case HARMONY:
                                return new Conditions(LogicType.NOR, new QuestRequirement(
                                                QuestHelperQuest.MORYTANIA_ELITE, QuestState.FINISHED));
                        case MORYTANIA:
                                return null;
                        case TROLL_STRONGHOLD:
                                return new Conditions(LogicType.NOR, accessToTrollStronghold);
                        case WEISS:
                                return new Conditions(LogicType.NOR, accessToWeiss);
                        case KOUREND:
                                return null;
                        case CIVITAS_ILLA_FORTIS:
                                return new Conditions(LogicType.NOR, CommonPatchRequirements.getVarlamoreAccess());
                        default:
                                return null;
                }
        }
}
