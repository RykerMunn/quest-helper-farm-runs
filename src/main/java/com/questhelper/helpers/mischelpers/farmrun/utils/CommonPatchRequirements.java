package com.questhelper.helpers.mischelpers.farmrun.utils;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;

import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;

public class CommonPatchRequirements {

    public static ItemRequirement getArdougneCloak() {
        return getArdougneCloak(true);
    }

    public static ItemRequirement getArdougneCloak(boolean withAlternates) {
        ItemRequirement ardougneCloak = new ItemRequirement("Ardogune Cloak 2+", ItemID.ARDOUGNE_CLOAK_2)
                .showConditioned(new QuestRequirement(QuestHelperQuest.ARDOUGNE_MEDIUM, QuestState.FINISHED));
        if (withAlternates) {
            ardougneCloak.addAlternates(ItemID.ARDOUGNE_CLOAK_3, ItemID.ARDOUGNE_CLOAK_4);
        }

        ardougneCloak.setTooltip(null);
        return ardougneCloak;
    }

    public static ItemRequirements getCatherybyTeleport() {
        ItemRequirements catherbyRunes = new ItemRequirements(
                "Runes for Catherby Teleport",
                new ItemRequirement("Water rune", ItemID.WATER_RUNE, 10),
                new ItemRequirement("Law rune", ItemID.LAW_RUNE, 3),
                new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE, 3));

        catherbyRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 87));
        catherbyRunes.showConditioned(new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED));

        ItemRequirement catherbyTablet = new ItemRequirement("Catherby tablet", ItemID.CATHERBY_TELEPORT);
        ItemRequirement camelotRunes = new ItemRequirements(
                "Runes for Camelot Teleport",
                new ItemRequirement("Law rune", ItemID.LAW_RUNE),
                new ItemRequirement("Air rune", ItemID.AIR_RUNE, 5));

        ItemRequirement camelotTablet = new ItemRequirement("Camelot teleport", ItemID.CAMELOT_TELEPORT);
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
        ItemRequirement explorerRing = new ItemRequirement("Explorer's Ring", ItemID.EXPLORERS_RING_2)
                .showConditioned(new QuestRequirement(QuestHelperQuest.LUMBRIDGE_MEDIUM, QuestState.FINISHED));
        if (withAlternates) {
            explorerRing.addAlternates(ItemID.EXPLORERS_RING_3, ItemID.EXPLORERS_RING_4);
        }
        return explorerRing;
    }

    public static ItemRequirement getFarmingGuildTeleport() {
        ItemRequirement farmingGuildTeleport = new ItemRequirement(
                "Farming guild teleport (Skills' Necklace or CIR fairy ring)",
                ItemID.FARMING_CAPET);

        farmingGuildTeleport.addAlternates(ItemID.FARMING_CAPE);
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

        ectophial.addAlternates(ItemID.ECTOPHIAL_4252);
        ectophial.setTooltip(null);
        return ectophial;
    }

    public static ItemRequirements getHarmonyIslandTeleport() {
        ItemRequirement ectophial = getEctophial();

        ItemRequirement harmonyIslandRunes = new ItemRequirements(
                "Runes for Harmony Island Teleport",
                new ItemRequirement("Nature rune", ItemID.NATURE_RUNE, 1),
                new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1),
                new ItemRequirement("Soul rune", ItemID.SOUL_RUNE, 1));
        harmonyIslandRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 65));

        ItemRequirement harmonyIslandTable = new ItemRequirement("Harmony Island teleport",
                ItemID.HARMONY_ISLAND_TELEPORT);

        ItemRequirements harmonyTeleport = new ItemRequirements(LogicType.OR, "Harmony Island Teleport", ectophial,
                harmonyIslandRunes, harmonyIslandTable);

        harmonyTeleport.setTooltip("Arceuus Spellbok (Lvl 65 Magic) or Harmony Island teleport tablet.");

        return harmonyTeleport;
    }

    public static ItemRequirement getXericsTalisman() {
        return new ItemRequirement("Xeric's talisman", ItemID.XERICS_TALISMAN);
    }

    public static ItemRequirement getHosidiusTeleport() {
        ItemRequirement hosidiusHouseTeleport = new ItemRequirement(
                "Teleport to Hosidius House (Tablet or Xeric's Talisman)",
                ItemID.HOSIDIUS_TELEPORT);
        hosidiusHouseTeleport.addAlternates(ItemID.XERICS_TALISMAN);
        return hosidiusHouseTeleport;
    }

    public static QuestRequirement getVarlamoreAccess() {
        return new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);
    }

    public static ItemRequirement getHunterWhistle() {
        ItemRequirement hunterWhistle = new ItemRequirement("Quetzal whistle", ItemID.PERFECTED_QUETZAL_WHISTLE)
                .showConditioned(getVarlamoreAccess());
        hunterWhistle.addAlternates(ItemID.BASIC_QUETZAL_WHISTLE);
        hunterWhistle.addAlternates(ItemID.ENHANCED_QUETZAL_WHISTLE);
        return hunterWhistle;
    }

    public static QuestRequirement getPrifddinasAccess() {
        return new QuestRequirement(QuestHelperQuest.SONG_OF_THE_ELVES, QuestState.FINISHED);
    }

    public static ItemRequirements getPrifddinasTeleport() {
        // teleport crystal or house teleport / redirect tab
        ItemRequirement teleportCrystal = new ItemRequirement("Teleport crystal", ItemCollections.TELEPORT_CRYSTAL);

        ItemRequirement prifddinasTablet = new ItemRequirement("Prifddinas teleport", ItemID.PRIFDDINAS_TELEPORT);
        ItemRequirement houseTeleport = new ItemRequirement("House teleport", ItemID.TELEPORT_TO_HOUSE);
        houseTeleport.addAlternates(ItemID.CONSTRUCT_CAPE, ItemID.CONSTRUCT_CAPET);

        ItemRequirement houseTeleportRunes = new ItemRequirements(
                "Runes for House Teleport",
                new ItemRequirement("Law rune", ItemID.LAW_RUNE),
                new ItemRequirement("Air rune", ItemID.AIR_RUNE, 1),
                new ItemRequirement("Earth rune", ItemID.EARTH_RUNE, 1));

        houseTeleportRunes.showConditioned(new SkillRequirement(Skill.MAGIC, 40));

        ItemRequirements prifddinasTeleport = new ItemRequirements(LogicType.OR, "Prifddinas Teleport",
                teleportCrystal, prifddinasTablet, houseTeleport, houseTeleportRunes);
        prifddinasTeleport.setTooltip("Teleport crystal, Prifddinas teleport tablet, or house teleport.");

        prifddinasTeleport.showConditioned(getPrifddinasAccess());

        prifddinasTeleport.setQuantity(1);
        return prifddinasTeleport;
    }
}
