package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;


public class SplashNode implements ExecutableNode{

    private final static String NODE_STATUS = "Splashing";
    private Script hostScriptReference;
    private Spells.NormalSpells splashingSpell;
    private WidgetDestination splashSpellWidgetDestination;
    private int targetNPCID;

    public SplashNode(Spells.NormalSpells splashingSpell, int targetNPCID, Script hostScriptReference){
        this.splashingSpell = splashingSpell;
        this.splashSpellWidgetDestination = PublicStaticFinalConstants.getSplashSpellWidgetDestination(splashingSpell);
        this.hostScriptReference = hostScriptReference;
        this.targetNPCID = targetNPCID;

    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        hoverOverSplashSpell();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        NPC npc = PublicStaticFinalConstants.hostScriptReference.getNpcs().closest(targetNPCID);
        if(m.isSpellSelected()){ //failsafe, if a spell is selected other spells cannot be cast.
            m.deselectSpell();
        }
        waitForMagicTab();

        int failcount = 0;
        while(!m.castSpellOnEntity(splashingSpell, npc)){
            failcount++;
            hostScriptReference.log("error: could not find npc");
            if(failcount > 10){
                hostScriptReference.log("error: failed to find npc over 10 times, stopping script");
                hostScriptReference.stop();
            }
        }

        if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
            ((MainScript) PublicStaticFinalConstants.hostScriptReference).incrementSpellCycles();
        }
        return 0;

    }

    private boolean hoverOverSplashSpell(){
        if(splashSpellWidgetDestination != null){
            return hostScriptReference.getMouse().move(splashSpellWidgetDestination);
        }
        else{
            PublicStaticFinalConstants.throwIllegalStateException("splashSpellWidgetDestination is null");
            return false;
        }
    }

    private void waitForMagicTab(){
        boolean magicTabOpen = new ConditionalSleep(7000, 250) {
            @Override
            public boolean condition(){
                return hostScriptReference.getTabs().getOpen() == Tab.MAGIC;
            }
        }.sleep();
        if(!magicTabOpen){
            hostScriptReference.getTabs().open(Tab.MAGIC);
        }
    }

    private void setScriptStatus(){
        if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
            ((MainScript) PublicStaticFinalConstants.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }

    @Override
    public String getStatus() {
        return NODE_STATUS;
    }

}
