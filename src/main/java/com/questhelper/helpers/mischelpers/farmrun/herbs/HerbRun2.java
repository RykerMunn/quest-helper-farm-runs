package com.questhelper.helpers.mischelpers.farmrun.herbs;

import com.questhelper.helpers.mischelpers.farmrun.FarmRun;
import com.questhelper.helpers.mischelpers.farmrun.FarmingSeed;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;

import lombok.Getter;

/**
 * Manages herb runs.
 */
public class HerbRun2 extends FarmRun {

    ManualRequirement ardougneEmpty, catherbyEmpty, faladorEmpty, farmingGuildEmpty, harmonyEmpty, morytaniaEmpty,
            trollStrongholdEmpty, weissEmpty, hosidiusEmpty, varlamoreEmpty;
    ManualRequirement ardougneReady, catherbyReady, faladorReady, farmingGuildReady, harmonyReady, morytaniaReady,
            trollStrongholdReady, weissReady, hosidiusReady, varlamoreReady;

    public HerbRun2(QuestHelper questHelper) {
        super(questHelper);
    }

    @Override
    protected ConditionalStep loadStep() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadStep'");
    }

    @Override
    protected void setupConditions() {
        ardougneReady = new ManualRequirement();
        catherbyReady = new ManualRequirement();
        faladorReady = new ManualRequirement();
        farmingGuildReady = new ManualRequirement();
        harmonyReady = new ManualRequirement();
        morytaniaReady = new ManualRequirement();
        trollStrongholdReady = new ManualRequirement();
        weissReady = new ManualRequirement();
        hosidiusReady = new ManualRequirement();
        varlamoreReady = new ManualRequirement();

        ardougneEmpty = new ManualRequirement();
        catherbyEmpty = new ManualRequirement();
        faladorEmpty = new ManualRequirement();
        farmingGuildEmpty = new ManualRequirement();
        harmonyEmpty = new ManualRequirement();
        morytaniaEmpty = new ManualRequirement();
        trollStrongholdEmpty = new ManualRequirement();
        weissEmpty = new ManualRequirement();
        hosidiusEmpty = new ManualRequirement();
        varlamoreEmpty = new ManualRequirement();
    }

    @Override
    protected void setupSteps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setupSteps'");
    }

    @Override
    protected void addSteps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addSteps'");
    }

    @Override
    protected PanelDetails getPanelDetails() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPanelDetails'");
    }

}
