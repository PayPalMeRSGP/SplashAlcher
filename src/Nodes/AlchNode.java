package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;


public class AlchNode implements ExecutableNode{

    private final static String NODE_STATUS = "Alching";
    private static final double BETWEEN_ALCH_STDDEV_MS = 20;
    private static final double BETWEEN_ALCH_MEAN_MS = 215;

    //ignore, I may need these later
    /*
    private static final Rectangle ALCHING_RECTANGLE = new Rectangle(703, 316, 24, 24);
    private static RectangleDestination ALCHING_DESTINATION;
    */

    private int alchingItemID;
    private Script hostScriptReference;

    private static ExecutableNode singleton;

    public static ExecutableNode getInstance(int alchingItemID, Script hostScriptReference){
        if(singleton == null){
            singleton = new AlchNode(alchingItemID, hostScriptReference);
        }
        return singleton;
    }

    public static ExecutableNode getInstance(){
        if(singleton == null){
            Statics.throwIllegalStateException("need to call other overloaded getInstance first to instantiate");
            return null;
        }
        return singleton;
    }

    private AlchNode(int alchingItemID, Script hostScriptReference){
        this.alchingItemID = alchingItemID;
        this.hostScriptReference = hostScriptReference;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {

        setScriptStatus();
        Magic m = Statics.hostScriptReference.getMagic();
        if(m.isSpellSelected()){ //failsafe, if a spell is selected other spells cannot be cast.
            m.deselectSpell();
        }
        if(hostScriptReference.getTabs().getOpen() != Tab.MAGIC){
            hostScriptReference.getTabs().open(Tab.MAGIC);
        }
        m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
        if(m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
            MethodProvider.sleep(Statics.randomNormalDist(BETWEEN_ALCH_MEAN_MS, BETWEEN_ALCH_STDDEV_MS));
            if(m.isSpellSelected() && m.getSelectedSpellName().equals("High Level Alchemy")){
                Inventory inv = hostScriptReference.getInventory();
                if(inv.contains(alchingItemID)){
                    if(inv.interact("Cast", alchingItemID)){
                        return (int) Statics.randomNormalDist(500, 100);
                    }
                }
                else{
                    hostScriptReference.log("ran out of alching item");
                    hostScriptReference.stop();
                }

            }
        }

        return (int) Statics.randomNormalDist(1500, 300);
    }

    private void setScriptStatus(){
        if(Statics.hostScriptReference instanceof MainScript){
            ((MainScript) hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }
}
