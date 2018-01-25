package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;


public class SplashNode implements ExecutableNode{

    private static SplashNode splashNodeSingleton = null;
    private final static String NODE_STATUS = "Splashing";

    private SplashNode(){

    }

    public static SplashNode getSplashNodeInstance() {
        if(splashNodeSingleton == null){
            //PublicStaticFinalConstants.hostScriptReference.log("creating new splashNodeSingleton");
            splashNodeSingleton = new SplashNode();
        }

        return splashNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        NPC npc = PublicStaticFinalConstants.hostScriptReference.getNpcs().closest(PublicStaticFinalConstants.targetNPC);
        if(PublicStaticFinalConstants.canCast()){
            waitForMagicTab();
            if(!m.castSpellOnEntity(PublicStaticFinalConstants.splashingSpell, npc)){ //sometimes this will fail, but the next onLoop call should fix
                PublicStaticFinalConstants.hostScriptReference.log("error: could not find npc");
            }

            if(m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){ //set up mouse on alch
                if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
                    ((MainScript) PublicStaticFinalConstants.hostScriptReference).incrementSpellCycles();
                }
                return (int) PublicStaticFinalConstants.randomNormalDist(50, 5);
            }
        }
        else{
            PublicStaticFinalConstants.hostScriptReference.log("Ran out of casts, stopping script");
            PublicStaticFinalConstants.hostScriptReference.stop();
        }

        return 0;

    }

    private void waitForMagicTab() throws InterruptedException {
        for(int i = 0; i < 5; i++){ //wait until magic tab reopens from alching
            if(!(PublicStaticFinalConstants.hostScriptReference.getTabs().getOpen() == Tab.MAGIC)){
                MethodProvider.sleep(PublicStaticFinalConstants.RS_GAME_TICK_MS); //rs game tick ms
            }
            else{
                break;
            }
        }
    }

    private void setScriptStatus(){
        if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
            ((MainScript) PublicStaticFinalConstants.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }

    @Override
    public String getStatus() {
        return "Splashing";
    }

}
