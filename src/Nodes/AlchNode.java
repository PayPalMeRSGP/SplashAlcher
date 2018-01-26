package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

public class AlchNode implements ExecutableNode{

    private final static String NODE_STATUS = "Alching";
    public static final int HIGH_ALCH_SPRITE_ID = 41;

    private int alchingItemID;
    private WidgetDestination highAlchWidgetDestination;
    private Script hostScriptReference;

    public AlchNode(int alchingItemID, Script hostScriptReference){
        this.alchingItemID = alchingItemID;
        this.hostScriptReference = hostScriptReference;
        setHighAlchWidgetDestination();
    }

    private void setHighAlchWidgetDestination(){
        hostScriptReference.getMagic().open();
        if(hostScriptReference != null){
            highAlchWidgetDestination = PublicStaticFinalConstants.getWidgetDestinationForSpell(HIGH_ALCH_SPRITE_ID);
        }
        else{
            PublicStaticFinalConstants.throwIllegalStateException("hostScriptReference is null");
        }
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
        if(!m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){ //sometimes this will fail, but the next onLoop call should fix
            PublicStaticFinalConstants.hostScriptReference.log("error: could not find high alch");
            return 0;
        }
        MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(PublicStaticFinalConstants.BETWEEN_ALCH_MEAN_MS, PublicStaticFinalConstants.BETWEEN_ALCH_STDDEV_MS));
        if(m.isSpellSelected() && m.getSelectedSpellName().equals("High Level Alchemy")){
            Inventory inv = PublicStaticFinalConstants.hostScriptReference.getInventory();
            if(inv.contains(alchingItemID)){
                inv.interact("Cast", alchingItemID);
            }
            else{
                PublicStaticFinalConstants.hostScriptReference.log("ran out of alching item");
                PublicStaticFinalConstants.hostScriptReference.stop();
            }

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
        return NODE_STATUS;
    }

}
