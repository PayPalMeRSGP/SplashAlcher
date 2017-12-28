package Nodes.SpinFlax;

import Nodes.ExecutableNode;
import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;

public class SpinFlaxNode implements ExecutableNode{

    private static final String BOWSTRING = "Bow string";
    private static final String FLAX = "Flax";

    private final int BASE_STARTING_KEY = 1;
    private int currentKey = BASE_STARTING_KEY;

    private static SpinFlaxNode spinFlaxNodeInstnace;

    private SpinFlaxNode(){}

    public static SpinFlaxNode getSpinFlaxNodeInstnace(){
        if(spinFlaxNodeInstnace == null){
            spinFlaxNodeInstnace = new SpinFlaxNode();
        }
        return spinFlaxNodeInstnace;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Inventory inv = ConstantsAndStatics.hostScriptReference.getInventory();
        int numFlaxLeft = (int) inv.getAmount(FLAX);
        Magic magic = ConstantsAndStatics.hostScriptReference.getMagic();
        while(numFlaxLeft >= 0){
            magic.castSpell(Spells.LunarSpells.SPIN_FLAX);
            MethodProvider.sleep((int) ConstantsAndStatics.randomNormalDist(ConstantsAndStatics.RS_GAME_TICK_MS, 80));
        }

        return 0;
    }

    @Override
    public void increaseKey() {
        currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        currentKey -= 2;
    }

    @Override
    public void resetKey() {
        currentKey = BASE_STARTING_KEY;
    }

    @Override
    public void setKey(int key) {
        currentKey = key;
    }

    @Override
    public int getKey() {
        return currentKey;
    }
}
