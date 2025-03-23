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
import java.util.List;

import javax.inject.Inject;

import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.mischelpers.farmrun.herbs.HerbRun2;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmrun.utils.FarmingWorld;
import com.questhelper.helpers.mischelpers.farmrun.utils.PatchImplementation;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.RequirementBuilder;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;

import net.runelite.api.ItemID;
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
	private List<PatchImplementation> selectedPatches = new ArrayList<>();

	DetailedQuestStep waitForHerbs, ardougnePatch, catherbyPatch, faladorPatch, farmingGuildPatch, harmonyPatch,
			morytaniaPatch, trollStrongholdPatch, weissPatch, hosidiusPatch, varlamorePatch;

	DetailedQuestStep ardougnePlant, catherbyPlant, faladorPlant, farmingGuildPlant, harmonyPlant, morytaniaPlant,
			trollStrongholdPlant, weissPlant, hosidiusPlant, varlamorePlant;
	ItemRequirement spade, dibber, rake, seed, compost;
	ItemRequirement ectophial, magicSec, explorerRing2, ardyCloak2, xericsTalisman, catherbyTeleport, trollheimTeleport,
			icyBasalt, stonyBasalt, farmingGuildTeleport, hosidiusHouseTeleport, hunterWhistle;
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape,
			gracefulOutfit;
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	Requirement accessToHarmony, accessToWeiss, accessToTrollStronghold, accessToFarmingGuildPatch, accessToVarlamore;

	ManualRequirement ardougneEmpty, catherbyEmpty, faladorEmpty, farmingGuildEmpty, harmonyEmpty, morytaniaEmpty,
			trollStrongholdEmpty, weissEmpty, hosidiusEmpty, varlamoreEmpty;
	ManualRequirement ardougneReady, catherbyReady, faladorReady, farmingGuildReady, harmonyReady, morytaniaReady,
			trollStrongholdReady, weissReady, hosidiusReady, varlamoreReady;

	Requirement patchTypesSelected, herbPatchSelected;
	DetailedQuestStep selectingPatchTypeStep;

	SeedsHelperConfig seedsConfig;

	private AbstractFarmRun herbRunManager;

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
		var herbStep = herbRunManager.loadStep();
		// when no other step condition is met, `waitForHerbs` is the default step.
		ConditionalStep steps = new ConditionalStep(this, selectingPatchTypeStep);
		steps.addStep(herbPatchSelected, herbStep);

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
		herbPatchSelected = RequirementBuilder
				.builder()
				.check(client -> {
					return this.selectedPatches.contains(PatchImplementation.HERB);
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
		gracefulBoots.addAlternates(ItemID.BOOTS_OF_LIGHTNESS);

		gracefulOutfit = new ItemRequirements(
				"Graceful outfit (equipped)",
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape).isNotConsumed()
				.showConditioned(
						new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.GRACEFUL.name()));

		farmingHat = new ItemRequirement(
				"Farmer's strawhat", ItemID.FARMERS_STRAWHAT, 1, true).isNotConsumed();
		farmingHat.addAlternates(ItemID.FARMERS_STRAWHAT_13647, ItemID.FARMERS_STRAWHAT_21253,
				ItemID.FARMERS_STRAWHAT_21254);

		farmingTop = new ItemRequirement(
				"Farmer's top", ItemID.FARMERS_JACKET, 1, true).isNotConsumed();
		farmingTop.addAlternates(ItemID.FARMERS_SHIRT);

		farmingLegs = new ItemRequirement(
				"Farmer's boro trousers", ItemID.FARMERS_BORO_TROUSERS, 1, true).isNotConsumed();
		farmingLegs.addAlternates(ItemID.FARMERS_BORO_TROUSERS_13641);

		farmingBoots = new ItemRequirement(
				"Graceful cape", ItemID.FARMERS_BOOTS, 1, true).isNotConsumed();
		farmingBoots.addAlternates(ItemID.FARMERS_BOOTS_13645);

		farmersOutfit = new ItemRequirements(
				"Farmer's outfit (equipped)",
				farmingHat, farmingTop, farmingLegs, farmingBoots).isNotConsumed()
				.showConditioned(
						new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	public void setupSteps() {
		selectingPatchTypeStep = new DetailedQuestStep(this,
				"Select the patch types in the configuration section to see detailed requirements.");
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
				selectedPatches.addAll(parsePatchImplementations(valueTest));
				bPatchesSelected = selectedPatches.size() > 0;
				seedsConfig.setSelectedPatches(selectedPatches);
				seedsConfig.refresh(questHelperPlugin.getConfigManager());
			}
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements() {
		if (bPatchesSelected) {

			// TODO: Iterate all FarmRun implementations and get their required items
			return herbRunManager.getRequiredItems();

		}
		return List.of();
	}

	@Override
	public List<ItemRequirement> getItemRecommended() {
		if (bPatchesSelected) {
			// TODO: Iterate all FarmRun implementations and get their recommended items
			return herbRunManager.getRecommendedItems();
		}
		return List.of();
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
		PanelDetails herbPanel = herbRunManager.getPanelDetails();
		herbPanel.setDisplayCondition(herbPatchSelected);
		allSteps.add(herbPanel);

		return allSteps;
	}

	@Override
	public void startUp(QuestHelperConfig helperConfig) {
		farmingHandler = new FarmingHandler(client, configManager);
		herbRunManager = new HerbRun2(client, this, farmingWorld, farmingHandler);
		herbRunManager.addRequiredItems(gracefulOutfit, farmersOutfit);
		eventBus.register(herbRunManager);
		step = loadStep();
		this.config = helperConfig;
		instantiateSteps(Collections.singletonList(step));
		var = getVar();
		String patchSelectionTest = this.getConfigManager()
				.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, PATCH_SELECTION);
		if (patchSelectionTest == null || patchSelectionTest.isEmpty()) {
			bPatchesSelected = false;
		} else {
			selectedPatches.clear();
			selectedPatches.addAll(parsePatchImplementations(patchSelectionTest));
			bPatchesSelected = selectedPatches.size() > 0;
		}

		startUpStep(step);
	}

	private static List<PatchImplementation> parsePatchImplementations(String str) {
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
