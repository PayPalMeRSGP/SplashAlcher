package Nodes.Splashing;

import GUI.SwingGUI;
import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

public class HoverSplashingNPC extends AbstractSplashNode {
    private final static String NODE_STATUS = "Splashing - HoverNPC";

    public HoverSplashingNPC(SwingGUI.SplashingSpellTypes splashingSpellType, int targetNPCID, boolean splashOnly, Script script) {
        super(splashingSpellType, targetNPCID, splashOnly, script);
    }

    @Override
    void postHoverSpellAction() throws InterruptedException {
        targetNPC.hover();
        if(script.myPlayer().getAnimation() == ALCH_ANIMATION)
            MethodProvider.sleep(Statics.randomNormalDist(300, 100));
        else
            MethodProvider.sleep(Statics.randomNormalDist(1800, 400));
    }

    @Override
    void setScriptStatusWrapper() {
        if(script instanceof MainScript)
            ((MainScript) script).setScriptStatus(NODE_STATUS);
    }


}
