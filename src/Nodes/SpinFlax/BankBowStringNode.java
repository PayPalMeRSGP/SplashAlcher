package Nodes.SpinFlax;

import Nodes.ExecutableNode;
import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Bank;
import org.osbot.rs07.script.MethodProvider;

public class BankBowStringNode implements ExecutableNode{

    private static final String BOWSTRING = "Bow string";
    private static final String FLAX = "Flax";

    private final int BASE_STARTING_KEY = 0;
    private int currentKey = BASE_STARTING_KEY;

    private static BankBowStringNode bankBowStringNodeSingleton;

    private BankBowStringNode(){}

    public static BankBowStringNode getBankBowStringNodeInstance(){
        if(bankBowStringNodeSingleton == null){
            bankBowStringNodeSingleton = new BankBowStringNode();
        }
        return bankBowStringNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Bank bank = ConstantsAndStatics.hostScriptReference.getBank();
        bank.open();
        bank.depositAll(BOWSTRING);

        bank.withdrawAll(FLAX);
        bank.close();

        return (int) ConstantsAndStatics.randomNormalDist(300, 30);
    }

    @Override
    public void increaseKey() {
        currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        currentKey -=2 ;
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
