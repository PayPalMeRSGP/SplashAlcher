package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.util.concurrent.ThreadLocalRandom;

public class IdleAntiban implements ExecutableNode {

    private Script hostScriptReference;
    private final static String NODE_STATUS = "Idle AntiBan";
    private final static int MAGIC_SKILL_ROOT_ID = 320;
    private final static int MAGIC_SKILL_CHILD_ID = 6;
    private WidgetDestination magicSkillDestination;

    public IdleAntiban(Script hostScriptReference){
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
            MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(5000, 1500));
        }

        return 0;
    }

    private void checkMageXp() throws InterruptedException {
        if(hostScriptReference.getTabs().open(Tab.SKILLS)){
            if(hostScriptReference.getMouse().move(magicSkillDestination)){
                MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(3000, 1000));
            }
            hostScriptReference.getTabs().open(Tab.INVENTORY);
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
