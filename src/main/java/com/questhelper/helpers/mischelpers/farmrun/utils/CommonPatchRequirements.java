package com.questhelper.helpers.mischelpers.farmrun.utils;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;

import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

public class CommonPatchRequirements {

    public static ItemRequirement getArdougneCloak() {
        return getArdougneCloak(true);
    }

    public static ItemRequirement getArdougneCloak(boolean withAlternates) {
        ItemRequirement ardougneCloak = new ItemRequirement("Ardogune Cloak 2+", ItemID.ARDY_CAPE_MEDIUM)
                .showConditioned(new QuestRequirement(QuestHelperQuest.ARDOUGNE_MEDIUM, QuestState.FINISHED));
        if (withAlternates) {
            ardougneCloak.addAlternates(ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE);
        }

        ardougneCloak.setTooltip(null);
        return ardougneCloak;
    }

    public static ItemRequirements getCatherybyTeleport() {
        ItemRequirements catherbyRunes = new ItemRequirements(
                "Runes for Catherby Teleport",
                new ItemRequirement("Water rune", ItemID.WATERRUNE, 10),
                new ItemRequirement("Law rune", ItemID.LAWRUNE, 3),
                new ItemRequirement("Astral rune", ItemID.ASTRALRUNE, 3));

        catherbyRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 87));
        catherbyRunes.showConditioned(new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED));

        ItemRequirement catherbyTablet = new ItemRequirement("Catherby tablet", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
        ItemRequirement camelotRunes = new ItemRequirements(
                "Runes for Camelot Teleport",
                new ItemRequirement("Law rune", ItemID.LAWRUNE),
                new ItemRequirement("Air rune", ItemID.AIRRUNE, 5));

        ItemRequirement camelotTablet = new ItemRequirement("Camelot teleport", ItemID.POH_TABLET_CAMELOTTELEPORT);
        camelotRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 45));

        ItemRequirements catherybyTeleport = new ItemRequirements(LogicType.OR, "Catherby Teleport", catherbyRunes,
                catherbyTablet, camelotRunes, camelotTablet);

        catherybyTeleport.setTooltip(
                "Lunar Spellbook (Lvl 87 Magic) or Catherby teleport tablet. Camelot teleport is an alternative.");
        catherybyTeleport.setQuantity(1);
        return catherybyTeleport;
    }

    public static ItemRequirement getExplorerRing() {
        return getExplorerRing(true);
    }

    public static ItemRequirement getExplorerRing(boolean withAlternates) {
        ItemRequirement explorerRing = new ItemRequirement("Explorer's Ring", ItemID.LUMBRIDGE_RING_MEDIUM)
                .showConditioned(new QuestRequirement(QuestHelperQuest.LUMBRIDGE_MEDIUM, QuestState.FINISHED));
        if (withAlternates) {
            explorerRing.addAlternates(ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE);
        }
        return explorerRing;
    }

    public static ItemRequirement getFarmingGuildTeleport() {
        ItemRequirement farmingGuildTeleport = new ItemRequirement(
                "Farming guild teleport (Skills' Necklace or CIR fairy ring)",
                ItemID.SKILLCAPE_FARMING_TRIMMED);

        farmingGuildTeleport.addAlternates(ItemID.SKILLCAPE_FARMING);
        farmingGuildTeleport.addAlternates(ItemCollections.SKILLS_NECKLACES);
        farmingGuildTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

        return farmingGuildTeleport;
    }

    public static SkillRequirement getFarmingGuildAccess() {
        return new SkillRequirement(Skill.FARMING, 65);
    }

    public static ItemRequirement getEctophial() {
        ItemRequirement ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL)
                .showConditioned(new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED));

        ectophial.addAlternates(ItemID.ECTOPHIAL_EMPTY);
        ectophial.setTooltip(null);
        return ectophial;
    }

    public static ItemRequirements getHarmonyIslandTeleport() {
        ItemRequirement ectophial = getEctophial();

        ItemRequirement harmonyIslandRunes = new ItemRequirements(
                "Runes for Harmony Island Teleport",
                new ItemRequirement("Nature rune", ItemID.NATURERUNE, 1),
                new ItemRequirement("Law rune", ItemID.LAWRUNE, 1),
                new ItemRequirement("Soul rune", ItemID.SOULRUNE, 1));
        harmonyIslandRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 65));

        ItemRequirement harmonyIslandTable = new ItemRequirement("Harmony Island teleport",
                ItemID.TELETAB_HARMONY);

        ItemRequirements harmonyTeleport = new ItemRequirements(LogicType.OR, "Harmony Island Teleport", ectophial,
                harmonyIslandRunes, harmonyIslandTable);

        harmonyTeleport.setTooltip("Arceuus Spellbok (Lvl 65 Magic) or Harmony Island teleport tablet.");

        return harmonyTeleport;
    }

    public static ItemRequirement getXericsTalisman() {
        return new ItemRequirement("Xeric's talisman", ItemID.XERIC_TALISMAN);
    }

    public static ItemRequirement getHosidiusTeleport() {
        ItemRequirement hosidiusHouseTeleport = new ItemRequirement(
                "Teleport to Hosidius House (Tablet or Xeric's Talisman)",
                ItemID.NZONE_TELETAB_KOUREND);
        hosidiusHouseTeleport.addAlternates(ItemID.XERIC_TALISMAN);
        return hosidiusHouseTeleport;
    }

    public static QuestRequirement getVarlamoreAccess() {
        return new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);
    }

    public static ItemRequirement getHunterWhistle() {
        ItemRequirement hunterWhistle = new ItemRequirement("Quetzal whistle", ItemID.HG_QUETZALWHISTLE_PERFECTED)
                .showConditioned(getVarlamoreAccess());
        hunterWhistle.addAlternates(ItemID.HG_QUETZALWHISTLE_BASIC);
        hunterWhistle.addAlternates(ItemID.HG_QUETZALWHISTLE_ENHANCED);
        return hunterWhistle;
    }

    public static QuestRequirement getPrifddinasAccess() {
        return new QuestRequirement(QuestHelperQuest.SONG_OF_THE_ELVES, QuestState.FINISHED);
    }

    public static ItemRequirements getPrifddinasTeleport() {
        // teleport crystal or house teleport / redirect tab
        ItemRequirement teleportCrystal = new ItemRequirement("Teleport crystal", ItemCollections.TELEPORT_CRYSTAL);

        ItemRequirement prifddinasTablet = new ItemRequirement("Prifddinas teleport", ItemID.PRIF_TELEPORT_CRYSTAL);
        ItemRequirement houseTeleport = new ItemRequirement("House teleport", ItemID.POH_TABLET_TELEPORTTOHOUSE);
        houseTeleport.addAlternates(ItemID.SKILLCAPE_CONSTRUCTION, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED);

        ItemRequirement houseTeleportRunes = new ItemRequirements(
                "Runes for House Teleport",
                new ItemRequirement("Law rune", ItemID.LAWRUNE),
                new ItemRequirement("Air rune", ItemID.AIRRUNE, 1),
                new ItemRequirement("Earth rune", ItemID.EARTHRUNE, 1));

        houseTeleportRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 40));

        ItemRequirements prifddinasTeleport = new ItemRequirements(LogicType.OR, "Prifddinas Teleport",
                teleportCrystal, prifddinasTablet, houseTeleport, houseTeleportRunes);
        prifddinasTeleport.setTooltip("Teleport crystal, Prifddinas teleport tablet, or house teleport.");

        prifddinasTeleport.showConditioned(getPrifddinasAccess());

        prifddinasTeleport.setQuantity(1);
        return prifddinasTeleport;
    }
}
