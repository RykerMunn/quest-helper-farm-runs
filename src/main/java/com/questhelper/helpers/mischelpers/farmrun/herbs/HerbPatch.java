package com.questhelper.helpers.mischelpers.farmrun.herbs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.questhelper.helpers.mischelpers.farmrun.utils.CommonPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingPatchRequirements;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
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

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

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
        HOSIDIUS(new ManualRequirement(), new ManualRequirement()),
        VARLAMORE(new ManualRequirement(), new ManualRequirement());

        private final ManualRequirement empty;

        private final ManualRequirement ready;

        private final ItemRequirement trollheimRunes = new ItemRequirements("Trollheim teleport runes",
                        new ItemRequirement("Law rune",
                                        ItemID.LAW_RUNE, 2),
                        new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 2));
        private final ItemRequirement trollheimTablet = new ItemRequirement("Trollheim tablet",
                        ItemID.TROLLHEIM_TELEPORT);
        private final ItemRequirement trollheimTeleport = new ItemRequirements(LogicType.OR, "Trollheim teleport",
                        trollheimRunes, trollheimTablet)
                        .hideConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                                        QuestState.FINISHED));

        private final ItemRequirement icyBasalt = new ItemRequirement("Icy basalt", ItemID.ICY_BASALT)
                        .showConditioned(
                                        new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM,
                                                        QuestState.FINISHED));

        private final ItemRequirement stonyBasalt = new ItemRequirement("Stony basalt", ItemID.STONY_BASALT)
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
                                                NullObjectID.NULL_8152,
                                                new WorldPoint(2670, 3374, 0),
                                                "Harvest your herbs from the Ardougne patch.",
                                                CommonPatchRequirements.getArdougneCloak());
                                break;
                        case CATHERBY:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8151,
                                                new WorldPoint(2813, 3463, 0),
                                                "Harvest your herbs from the Catherby patch.",
                                                CommonPatchRequirements.getCatherybyTeleport());
                                break;
                        case FALADOR:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8150,
                                                new WorldPoint(3058, 3311, 0),
                                                "Harvest your herbs from the Falador patch.",
                                                CommonPatchRequirements.getExplorerRing());
                                break;
                        case FARMING_GUILD:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_38979,
                                                new WorldPoint(1238, 3726, 0),
                                                "Harvest your herbs from the Farming Guild patch.",
                                                CommonPatchRequirements.getFarmingGuildTeleport());

                                harvestStep.conditionToHideInSidebar(
                                                CommonPatchRequirements.getFarmingGuildAccess());

                                break;
                        case HARMONY:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8153,
                                                new WorldPoint(3789, 2837, 0),
                                                "Harvest your herbs from the Harmony patch.",
                                                CommonPatchRequirements.getHarmonyIslandTeleport());
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR,
                                                                new QuestRequirement(QuestHelperQuest.MORYTANIA_ELITE,
                                                                                QuestState.FINISHED)));
                                break;
                        case MORYTANIA:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8153,
                                                new WorldPoint(3605, 3529, 0),
                                                "Harvest your herbs from the Morytania patch.",
                                                CommonPatchRequirements.getEctophial());
                        case TROLL_STRONGHOLD:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_18816,
                                                new WorldPoint(3605, 3529, 0),
                                                "Harvest your herbs from the Troll Stronghold patch.",
                                                trollheimTeleport, stonyBasalt);
                                conditionsToHide.add(new Conditions(LogicType.NOR, accessToTrollStronghold));
                                break;
                        case WEISS:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_33176,
                                                new WorldPoint(2848, 3934, 0),
                                                "Harvest your herbs from the Weiss patch.",
                                                icyBasalt);
                                conditionsToHide.add(new Conditions(LogicType.NOR, accessToWeiss));
                                break;
                        case HOSIDIUS:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_27115,
                                                new WorldPoint(1738, 3550, 0),
                                                "Harvest your herbs from the Hosidius patch.",
                                                CommonPatchRequirements.getHosidiusTeleport());
                                break;
                        case VARLAMORE:
                                harvestStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_50697,
                                                new WorldPoint(1582, 3094, 0),
                                                "Harvest your herbs from the Varlamore patch.",
                                                CommonPatchRequirements.getHunterWhistle());
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR,
                                                                CommonPatchRequirements.getVarlamoreAccess()));

                                break;
                        default:
                                return null;
                }
                conditionsToHide.add(new Conditions(LogicType.NOR, getPatchReadyRequirement()));
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
                                                NullObjectID.NULL_8152,
                                                new WorldPoint(2670, 3374, 0),
                                                "Plant your seeds into the Ardougne patch.",
                                                CommonPatchRequirements.getArdougneCloak());
                                break;
                        case CATHERBY:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8151,
                                                new WorldPoint(2813, 3463, 0),
                                                "Plant your seeds into the Catherby patch.",
                                                CommonPatchRequirements.getCatherybyTeleport());
                                break;
                        case FALADOR:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8150,
                                                new WorldPoint(3058, 3311, 0),
                                                "Plant your seeds into the Faladar patch.",
                                                CommonPatchRequirements.getExplorerRing());
                                break;
                        case FARMING_GUILD:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_33979,
                                                new WorldPoint(1238, 3726, 0),
                                                "Plant your seeds into the Farming Guild patch.",
                                                CommonPatchRequirements.getFarmingGuildTeleport());
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR,
                                                                CommonPatchRequirements.getFarmingGuildAccess()));
                                break;
                        case HARMONY:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8153,
                                                new WorldPoint(3789, 2837, 0),
                                                "Plant your seeds into the Harmony patch.",
                                                CommonPatchRequirements.getHarmonyIslandTeleport());
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR,
                                                                new QuestRequirement(QuestHelperQuest.MORYTANIA_ELITE,
                                                                                QuestState.FINISHED)));
                                break;

                        case MORYTANIA:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_8153,
                                                new WorldPoint(3605, 3529, 0),
                                                "Plant your seeds into the Morytania patch.",
                                                CommonPatchRequirements.getEctophial());
                                break;
                        case TROLL_STRONGHOLD:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_18816,
                                                new WorldPoint(3605, 3529, 0),
                                                "Plant your seeds into the Troll Stronghold patch.",
                                                trollheimTeleport, stonyBasalt);
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR, accessToTrollStronghold));
                                break;
                        case WEISS:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_33176,
                                                new WorldPoint(2848, 3934, 0),
                                                "Plant your seeds into the Weiss patch.",
                                                icyBasalt);
                                conditionsToHide.add(new Conditions(LogicType.NOR, accessToWeiss));
                                break;
                        case HOSIDIUS:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_27115,
                                                new WorldPoint(1738, 3550, 0),
                                                "Plant your seeds into the Hosidius patch.",
                                                CommonPatchRequirements.getHosidiusTeleport());
                                break;
                        case VARLAMORE:
                                plantStep = new ObjectStep(
                                                questHelper,
                                                NullObjectID.NULL_50697,
                                                new WorldPoint(1582, 3094, 0),
                                                "Plant your seeds into the Varlamore patch.",
                                                CommonPatchRequirements.getHunterWhistle());
                                conditionsToHide.add(
                                                new Conditions(LogicType.NOR,
                                                                CommonPatchRequirements.getVarlamoreAccess()));
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
}
