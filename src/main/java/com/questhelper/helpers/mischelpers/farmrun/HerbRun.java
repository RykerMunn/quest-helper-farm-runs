/*
 * Copyright (c) 2023, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.mischelpers.farmrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.helpers.mischelpers.farmrun.utils.PatchImplementation;
import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.RequirementBuilder;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

public class HerbRun extends ComplexStateQuestHelper {
	// TODO: Updating setId and setName in ItemRequirement
	@Inject
	private FarmingWorld farmingWorld;

	@Inject
	private FarmingSeedFactory farmingSeedFactory;

	@Inject
	protected EventBus eventBus;

	private FarmingHandler farmingHandler;
	private boolean bPatchesSelected = false;
	private HashSet<PatchImplementation> selectedPatches = new HashSet<>();
	private HashMap<PatchImplementation, AbstractFarmRun> allPatches = new HashMap<>();

	private HashSet<ItemRequirement> allRequiredItems = new HashSet<>();
	private HashSet<ItemRequirement> allRecommendedItems = new HashSet<>();

	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape,
			gracefulOutfit;
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	Requirement patchTypesSelected;
	DetailedQuestStep selectingPatchTypeStep;

	SeedsHelperConfig seedsConfig;

	private enum GracefulOrFarming {
		NONE(),
		GRACEFUL(),
		FARMING();
	}

	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";
	private final String PATCH_SELECTION = "patchSelection";

	@Override
	public QuestStep loadStep() {
		initializeRequirements();
		setupConditions();
		setupSteps();

		// If no patches are selected, show the patch selection step.
		// Else the first step that is shown is the first available step
		// based on the order of selected patches.
		ConditionalStep steps = new ConditionalStep(this, selectingPatchTypeStep);

		for (var patchType : selectedPatches) {
			var patch = allPatches.get(patchType);
			var questStep = patch.loadStep();
			steps.addStep(patch.getConditionToHide(), questStep);
		}

		return steps;
	}

	public void setupConditions() {

	}

	@Override
	protected void setupRequirements() {
		patchTypesSelected = RequirementBuilder
				.builder("Patch Types Selected")
				.check(client -> {
					return !bPatchesSelected;
				})
				.build();

		gracefulHood = new ItemRequirement(
				"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1, true).isNotConsumed();

		gracefulTop = new ItemRequirement(
				"Graceful top", ItemCollections.GRACEFUL_TOP, 1, true).isNotConsumed();

		gracefulLegs = new ItemRequirement(
				"Graceful legs", ItemCollections.GRACEFUL_LEGS, 1, true).isNotConsumed();

		gracefulCape = new ItemRequirement(
				"Graceful cape", ItemCollections.GRACEFUL_CAPE, 1, true).isNotConsumed();

		gracefulGloves = new ItemRequirement(
				"Graceful gloves", ItemCollections.GRACEFUL_GLOVES, 1, true).isNotConsumed();

		gracefulBoots = new ItemRequirement(
			"Graceful boots", ItemCollections.GRACEFUL_BOOTS, 1, true).isNotConsumed();
		gracefulBoots.addAlternates(ItemID.IKOV_BOOTSOFLIGHTNESS);

		gracefulOutfit = new ItemRequirements(
				"Graceful outfit (equipped)",
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape).isNotConsumed()
				.showConditioned(
						new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.GRACEFUL.name()));

		farmingHat = new ItemRequirement(
			"Farmer's strawhat", ItemID.TITHE_REWARD_HAT_MALE, 1 ,true).isNotConsumed();
		farmingHat.addAlternates(ItemID.TITHE_REWARD_HAT_FEMALE, ItemID.TITHE_REWARD_HAT_MALE_DUMMY, ItemID.TITHE_REWARD_HAT_FEMALE_DUMMY);

		farmingTop = new ItemRequirement(
			"Farmer's top", ItemID.TITHE_REWARD_TORSO_MALE, 1, true).isNotConsumed();
		farmingTop.addAlternates(ItemID.TITHE_REWARD_TORSO_FEMALE);

		farmingLegs = new ItemRequirement(
			"Farmer's boro trousers", ItemID.TITHE_REWARD_LEGS_MALE, 1, true).isNotConsumed();
		farmingLegs.addAlternates(ItemID.TITHE_REWARD_LEGS_FEMALE);

		farmingBoots = new ItemRequirement(
			"Graceful cape", ItemID.TITHE_REWARD_FEET_MALE, 1, true).isNotConsumed();
		farmingBoots.addAlternates(ItemID.TITHE_REWARD_FEET_FEMALE);

		farmersOutfit = new ItemRequirements(
				"Farmer's outfit (equipped)",
				farmingHat, farmingTop, farmingLegs, farmingBoots).isNotConsumed()
				.showConditioned(
						new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	public void setupSteps() {
		waitForHerbs = new DetailedQuestStep(this, "Wait for your herbs to grow.");
		ardougnePatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_3, new WorldPoint(2670, 3374, 0), "Harvest your herbs from the Ardougne patch.", ardyCloak2);
		catherbyPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_2, new WorldPoint(2813, 3463, 0), "Harvest your herbs from the Catherby patch.", catherbyTeleport);
		faladorPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_1, new WorldPoint(3058, 3311, 0), "Harvest your herbs from the Falador patch.", explorerRing2);
		hosidiusPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_6, new WorldPoint(1738, 3550, 0), "Harvest your herbs from the Hosidius patch.", xericsTalisman);

		farmingGuildPatch = new ObjectStep(this, ObjectID.HS_NPT2_WALL_CUTS_03, new WorldPoint(1238, 3726, 0), "Harvest your herbs from the Farming Guild patch.", farmingGuildTeleport);
		farmingGuildPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));

		harmonyPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_5, new WorldPoint(3789, 2837, 0), "Harvest your herbs from the Harmony patch.", ectophial);
		harmonyPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));

		morytaniaPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_4, new WorldPoint(3605, 3529, 0), "Harvest your herbs from the Morytania patch.", ectophial);

		trollStrongholdPatch = new ObjectStep(this, ObjectID.MYARM_HERBPATCH, new WorldPoint(2826, 3694, 0), "Harvest your herbs from the Troll Stronghold patch.",
			trollheimTeleport, stonyBasalt);
		trollStrongholdPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		weissPatch = new ObjectStep(this, ObjectID.MY2ARM_HERBPATCH, new WorldPoint(2848, 3934, 0), "Harvest your herbs from the Weiss patch.", icyBasalt);
		weissPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));

		varlamorePatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_8, new WorldPoint(1582, 3094, 0), "Harvest your herbs from the Varlamore patch.", hunterWhistle);
		varlamorePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));

		ardougnePlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_3, new WorldPoint(2670, 3374, 0), "Plant your seeds into the Ardougne patch.", ardyCloak2);
		ardougnePlant.addIcon(ItemID.RANARR_SEED);
		ardougnePatch.addSubSteps(ardougnePlant);

		catherbyPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_2, new WorldPoint(2813, 3463, 0), "Plant your seeds into the Catherby patch.", catherbyTeleport);
		catherbyPlant.addIcon(ItemID.RANARR_SEED);
		catherbyPatch.addSubSteps(catherbyPlant);

		faladorPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_1, new WorldPoint(3058, 3311, 0), "Plant your seeds into the Falador patch.", explorerRing2);
		faladorPlant.addIcon(ItemID.RANARR_SEED);
		faladorPatch.addSubSteps(faladorPlant);

		hosidiusPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_6, new WorldPoint(1738, 3550, 0), "Plant your seeds into the Hosidius patch.", hosidiusHouseTeleport);
		hosidiusPlant.addIcon(ItemID.RANARR_SEED);
		hosidiusPlant.addSubSteps(hosidiusPlant);

		farmingGuildPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_7, new WorldPoint(1238, 3726, 0), "Plant your seeds into the Farming Guild patch.", farmingGuildTeleport);
		farmingGuildPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));
		farmingGuildPlant.addIcon(ItemID.RANARR_SEED);
		farmingGuildPatch.addSubSteps(farmingGuildPlant);

		harmonyPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_5, new WorldPoint(3789, 2837, 0), "Plant your seeds into the Harmony patch.", ectophial);
		harmonyPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));
		harmonyPlant.addIcon(ItemID.RANARR_SEED);
		harmonyPatch.addSubSteps(harmonyPlant);

		morytaniaPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_4, new WorldPoint(3605, 3529, 0), "Plant your seeds into the Morytania patch.", ectophial);
		morytaniaPlant.addIcon(ItemID.RANARR_SEED);
		morytaniaPatch.addSubSteps(morytaniaPlant);

		trollStrongholdPlant = new ObjectStep(this, ObjectID.MYARM_HERBPATCH, new WorldPoint(2826, 3694, 0), "Plant your seeds into the Troll Stronghold patch.",
			trollheimTeleport, stonyBasalt);
		trollStrongholdPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		trollStrongholdPlant.addIcon(ItemID.RANARR_SEED);
		trollStrongholdPatch.addSubSteps(trollStrongholdPlant);

		weissPlant = new ObjectStep(this, ObjectID.MY2ARM_HERBPATCH, new WorldPoint(2848, 3934, 0), "Plant your seeds into the Weiss patch.", icyBasalt);
		weissPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));
		weissPlant.addIcon(ItemID.RANARR_SEED);
		weissPatch.addSubSteps(weissPlant);

		varlamorePlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_8, new WorldPoint(1582, 3094, 0), "Plant your seeds into the Varlamore patch.", hunterWhistle);
		varlamorePlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		varlamorePlant.addIcon(ItemID.RANARR_SEED);
		varlamorePatch.addSubSteps(varlamorePlant);

		selectingPatchTypeStep = new DetailedQuestStep(this,
				"Select the patch types in the configuration section to see detailed requirements.");

		FarmRunBuilder builder = FarmRunBuilder.builder()
				.withQuestHelper(this)
				.withFarmingHandler(farmingHandler)
				.withFarmingWorld(farmingWorld)
				.withEventBus(eventBus);
		for (PatchImplementation availablePatch : PatchImplementation.values()) {
			builder.withPatchImplementation(availablePatch)
					.withHideCondition(client -> {
						return !this.selectedPatches.contains(availablePatch);
					});
			AbstractFarmRun farmRun = builder.build();
			if (farmRun == null) {
				continue;
			}
			allPatches.putIfAbsent(availablePatch, farmRun);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP)) {
			return;
		}

		if (event.getKey().equals(GRACEFUL_OR_FARMING)) {
			questHelperPlugin.getClientThread().invokeLater(() -> {
				// force the inventory requirements to update.
				ItemAndLastUpdated inventoryData = QuestContainerManager.getInventoryData();
				inventoryData.update(inventoryData.getLastUpdated() + 1, inventoryData.getItems());
			});
			questHelperPlugin.refreshBank();
		}

		if (event.getKey().equals(PATCH_SELECTION)) {
			String valueTest = event.getNewValue();
			if (valueTest != null && !valueTest.isEmpty()) {
				selectedPatches.clear();
				var patches = parsePatchImplementations(valueTest);
				patches.forEach(patch -> {
					selectedPatches.add(patch);
				});

				bPatchesSelected = selectedPatches.size() > 0;

				seedsConfig.refresh(questHelperPlugin.getConfigManager());
			}
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements() {
		allRequiredItems.clear();
		if (bPatchesSelected) {
			for (var patchType : selectedPatches) {
				var patch = allPatches.get(patchType);
				List<ItemRequirement> orderedItems = patch.getRequiredItems();
				allRequiredItems.addAll(orderedItems);
			}
		}
		allRequiredItems.add(farmersOutfit);
		allRequiredItems.add(gracefulOutfit);
		var list = new ArrayList<>(allRequiredItems);
		list.sort(Comparator.comparingInt(item -> {
			for (var patchType : selectedPatches) {
				var patch = allPatches.get(patchType);
				List<ItemRequirement> orderedItems = patch.getRequiredItems();
				int index = orderedItems.indexOf(item);
				if (index != -1) {
					return index;
				}
			}
			return Integer.MAX_VALUE; // Items not found in any patch are placed at the end
		}));
		return list;
	}

	@Override
	public List<ItemRequirement> getItemRecommended() {
		allRecommendedItems.clear();
		if (bPatchesSelected) {
			for (var patchType : selectedPatches) {
				var patch = allPatches.get(patchType);
				List<ItemRequirement> orderedItems = patch.getRecommendedItems();
				allRecommendedItems.addAll(orderedItems);
			}
		}
		var list = new ArrayList<>(allRecommendedItems);
		list.sort(Comparator.comparingInt(item -> {
			for (var patchType : selectedPatches) {
				var patch = allPatches.get(patchType);
				List<ItemRequirement> orderedItems = patch.getRecommendedItems();
				int index = orderedItems.indexOf(item);
				if (index != -1) {
					return index;
				}
			}
			return Integer.MAX_VALUE; // Items not found in any patch are placed at the end
		}));
		return list;
	}

	@Override
	public List<HelperConfig> getConfigs() {
		HelperConfig patchConfig = new HelperConfig("Patches", PATCH_SELECTION, PatchImplementation.values());
		patchConfig.setAllowMultiple(true);
		seedsConfig = new SeedsHelperConfig(farmingSeedFactory, "Seeds", selectedPatches);
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		return Arrays.asList(patchConfig, seedsConfig, outfitConfig);
	}

	@Override
	public List<PanelDetails> getPanels() {

		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Selecting patch types", Arrays.asList(selectingPatchTypeStep)));

		for (var patchKey : selectedPatches) {
			var patch = allPatches.get(patchKey);
			if (!patch.isInitialized()) {
				// this means `loadStep` didn't have any selected patches
				// but still tried to load a selected patch step!
				assert false : "Patch is not initialized";
				continue;
			}
			var panel = patch.getPanelDetails();
			panel.setDisplayCondition(patch.getConditionToHide());
			allSteps.add(panel);
		}

		return allSteps;
	}

	@Override
	public void startUp(QuestHelperConfig helperConfig) {
		farmingHandler = new FarmingHandler(client, configManager);
		String patchSelectionTest = this.getConfigManager()
				.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, PATCH_SELECTION);
		if (patchSelectionTest == null || patchSelectionTest.isEmpty()) {
			bPatchesSelected = false;
		} else {
			var patches = parsePatchImplementations(patchSelectionTest);
			patches.forEach(patch -> {
				selectedPatches.add(patch);
			});
			bPatchesSelected = selectedPatches.size() > 0;
		}
		step = loadStep();
		this.config = helperConfig;
		instantiateSteps(Collections.singletonList(step));
		var = getVar();

		startUpStep(step);
	}

	@Override
	public void shutDown() {
		allPatches.values().forEach(farmRun -> {
			farmRun.shutDown();
			eventBus.unregister(farmRun);
		});
		selectedPatches.clear();
		allPatches.clear();
		allRequiredItems.clear();
		allRecommendedItems.clear();
		bPatchesSelected = false;
	}

	private List<PatchImplementation> parsePatchImplementations(String str) {
		List<PatchImplementation> patches = new ArrayList<>();
		if (str == null || str.isEmpty()) {
			return patches;
		}
		var splits = str.replace("[", "").replace("]", "").split(",");
		for (String split : splits) {
			if (split.isEmpty()) {
				continue;
			}
			patches.add(PatchImplementation.valueOf(split.trim()));
		}

		return patches;
	}
}
