package Nodes;

import GUI.SwingGUI;
import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;


public class SplashNode implements ExecutableNode{

    private final static String NODE_STATUS = "Splashing";
    private Script hostScriptReference;

    private Spells.NormalSpells splashingSpell;
    private SwingGUI.SplashingSpellTypes splashingSpellType;

    private static final Rectangle CONFUSE_RECTANGLE = new Rectangle(607, 220, 24, 24);
    private static final Rectangle WEAKEN_RECTANGLE = new Rectangle(559, 244, 24, 24);
    private static final Rectangle CURSE_RECTANGLE = new Rectangle(655, 244, 24, 24);
    private static final Rectangle VULNERABILITY_RECTANGLE = new Rectangle(583, 388, 24, 24);
    private static final Rectangle ENFEEBLE_RECTANGLE = new Rectangle(679, 388, 24, 24);
    private static final Rectangle STUN_RECTANGLE = new Rectangle(607, 412, 24, 24);
    private static RectangleDestination splashDestination;

    private int targetNPCID;
    private NPC targetNPC;
    private boolean splashOnly;

    private static ExecutableNode singleton;

    public static ExecutableNode getInstance(SwingGUI.SplashingSpellTypes splashingSpell, int targetNPCID, boolean splashOnly, Script hostScriptReference) {
        if(singleton == null){
            singleton = new SplashNode(splashingSpell, targetNPCID, splashOnly, hostScriptReference);
        }
        return singleton;
    }

    public static ExecutableNode getInstance(){
        if(singleton == null){
            Statics.throwIllegalStateException("need to call other overloaded getInstance first to instantiate");
        }
        return singleton;
    }

    private SplashNode(SwingGUI.SplashingSpellTypes splashingSpellType, int targetNPCID, boolean splashOnly, Script hostScriptReference){
        this.splashingSpellType = splashingSpellType;
        this.hostScriptReference = hostScriptReference;
        this.targetNPCID = targetNPCID;
        this.splashOnly = splashOnly;
        hostScriptReference.log("SplashNode recieved: " + splashingSpellType);
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        handleProgressiveSpellSwitch();
        if(hoverOverAndSelectSplashSpell()){
            Magic m = hostScriptReference.getMagic();
            targetNPC = hostScriptReference.getNpcs().closest(targetNPCID);
            if(splashOnly){ //different sleep for splash only
                MethodProvider.sleep(Statics.randomNormalDist(Statics.RS_GAME_TICK_MS*3, 500));
            }
            String selectedSpellName = m.getSelectedSpellName().toLowerCase();
            String correctSpellName = splashingSpell.name().toLowerCase();
            if(m.isSpellSelected() && !selectedSpellName.equals(correctSpellName)){ //failsafe, if a spell is selected other spells cannot be cast.
                m.deselectSpell();
                Statics.shortRandomSleep();
            }
            waitForMagicTab();

            if(m.castSpellOnEntity(splashingSpell, targetNPC)){
                if(Statics.hostScriptReference instanceof MainScript){
                    ((MainScript) Statics.hostScriptReference).incrementSpellCycles();
                }
                return (int) Statics.randomNormalDist(500, 100);
            }

        }
        return (int) Statics.randomNormalDist(1500, 300);
    }

    private boolean isSplashDestinationIncorrect(Rectangle correct){
        if(splashDestination == null){
            return true;
        }
        Rectangle boundingBox = splashDestination.getBoundingBox();
        return boundingBox.x != correct.x || boundingBox.y != correct.y || boundingBox.height != correct.height || boundingBox.width != correct.width;
    }

    private void handleProgressiveSpellSwitch() { //19, 11, 3, 80, 73, 66
        int mageLvl = hostScriptReference.getSkills().getStatic(Skill.MAGIC);
        if (splashingSpellType == SwingGUI.SplashingSpellTypes.BODY_RUNE_SPELLS) {
            //cannot do if(mageLvl >= # && isSplashDestinationIncorrect(RECT)) because 2nd statement will resolve to false, then spell will incorrectly switch.
            //the next if-else statement will resolve to true, therefore incorrectly switching the spell
            if(mageLvl >= 19){
                if(isSplashDestinationIncorrect(CURSE_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), CURSE_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.CURSE;
                }
            }
            else if(mageLvl >= 11){
                if(isSplashDestinationIncorrect(WEAKEN_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), WEAKEN_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.WEAKEN;
                }
            }
            else if(mageLvl >= 3){
                if(isSplashDestinationIncorrect(CONFUSE_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), CONFUSE_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.CONFUSE;
                }
            }
        }
        else{
            if(mageLvl >= 80){
                if(isSplashDestinationIncorrect(STUN_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), STUN_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.STUN;
                }
            }
            else if(mageLvl >= 73){
                if(isSplashDestinationIncorrect(ENFEEBLE_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), ENFEEBLE_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.ENFEEBLE;
                }
            }
            else if(mageLvl >= 66){
                if(isSplashDestinationIncorrect(VULNERABILITY_RECTANGLE)){
                    splashDestination = new RectangleDestination(hostScriptReference.getBot(), VULNERABILITY_RECTANGLE);
                    splashingSpell = Spells.NormalSpells.VULNERABILITY;
                }
            }
        }

    }


    private boolean hoverOverAndSelectSplashSpell() throws InterruptedException {
        if(splashDestination != null){
            if(hostScriptReference.getMouse().move(splashDestination)){
                return true;
            }
        }
        Statics.throwIllegalStateException("splashDestination is null");
        return false;
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
        if(Statics.hostScriptReference instanceof MainScript){
            ((MainScript) Statics.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }
}
