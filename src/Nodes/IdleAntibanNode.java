package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.util.concurrent.ThreadLocalRandom;

public class IdleAntibanNode implements ExecutableNode {

    private Script hostScriptReference;
    private final static String NODE_STATUS = "Idle AntiBan";
    private final static int MAGIC_SKILL_ROOT_ID = 320;
    private final static int MAGIC_SKILL_CHILD_ID = 6;
    private WidgetDestination magicSkillDestination;

    private static ExecutableNode singleton;

    public static ExecutableNode getInstance(Script hostScriptReference){
        if(singleton == null){
            singleton = new IdleAntibanNode(hostScriptReference);
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

    private IdleAntibanNode(Script hostScriptReference){
        this.hostScriptReference = hostScriptReference;
        RS2Widget magicSkillWidget = hostScriptReference.getWidgets().get(MAGIC_SKILL_ROOT_ID, MAGIC_SKILL_CHILD_ID);
        this.magicSkillDestination = new WidgetDestination(hostScriptReference.getBot(), magicSkillWidget);
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        int rand = ThreadLocalRandom.current().nextInt(0,2);
        if(rand == 0){
            checkMageXp();
        }
        else{
            MethodProvider.sleep(Statics.randomNormalDist(5000, 1500));
        }

        return 0;
    }

    private void checkMageXp() throws InterruptedException {
        if(hostScriptReference.getTabs().open(Tab.SKILLS)){
            if(hostScriptReference.getMouse().move(magicSkillDestination)){
                MethodProvider.sleep(Statics.randomNormalDist(3000, 1000));
            }
            hostScriptReference.getTabs().open(Tab.INVENTORY);
        }
    }

    private void setScriptStatus(){
        if(Statics.hostScriptReference instanceof MainScript){
            ((MainScript) Statics.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }
}
