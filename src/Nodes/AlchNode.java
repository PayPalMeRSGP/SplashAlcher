package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;

public class AlchNode implements ExecutableNode{

    private final static String NODE_STATUS = "Alching";
    private static AlchNode alchNodeSingleton;

    private AlchNode(){

    }

    public static AlchNode getAlchNodeInstance() {
        if(alchNodeSingleton == null){
            alchNodeSingleton = new AlchNode();
        }
        return alchNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        if(!m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){ //sometimes this will fail, but the next onLoop call should fix
            PublicStaticFinalConstants.hostScriptReference.log("error: could not find high alch");
        }
        MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(PublicStaticFinalConstants.BETWEEN_ALCH_MEAN_MS, PublicStaticFinalConstants.BETWEEN_ALCH_STDDEV_MS));
        if(m.isSpellSelected() && m.getSelectedSpellName().equals("High Level Alchemy")){
            Inventory inv = PublicStaticFinalConstants.hostScriptReference.getInventory();
            if(inv.contains(PublicStaticFinalConstants.targetItem)){
                inv.interact("Cast", PublicStaticFinalConstants.targetItem);
            }
            else{
                PublicStaticFinalConstants.hostScriptReference.log("ran out of alching item");
                PublicStaticFinalConstants.hostScriptReference.stop();
            }

        }
        if(PublicStaticFinalConstants.hoverOverSplashSpell()){
            return (int) PublicStaticFinalConstants.randomNormalDist(100, 10);
        }


        return 0;
    }

    private void setScriptStatus(){
        if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
            ((MainScript) PublicStaticFinalConstants.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }

    @Override
    public String getStatus() {
        return "Alching";
    }

}
