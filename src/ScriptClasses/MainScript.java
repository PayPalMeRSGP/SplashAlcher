package ScriptClasses;

import GUI.SwingGUI;
import GUI.UserSelectedResults;
import Nodes.*;

import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import static ScriptClasses.PublicStaticFinalConstants.SCRIPT_NAME;


@ScriptManifest(author = "PayPalMeRSGP", name = SCRIPT_NAME, info = "splashes a debuff spell while alching for high xph", version = 0.5, logo = "https://i.imgur.com/6WL3ad2.png")
public class MainScript extends Script implements MessageListener{

    private GraphBasedNodeExecutor executor;
    private long startTime;
    private int spellCycles = 0;
    private String scriptStatus = "";

    private DraggablePaintHandler paintHandler;

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        //debugWidgets();
        setUp();
    }

    @Override
    public int onLoop() throws InterruptedException {
        return executor.executeNodeThenTraverse();
    }

    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
        this.bot.removeMouseListener(paintHandler);
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        Rectangle paintArea = paintHandler.getPaintArea();
        long runTime = System.currentTimeMillis() - startTime;
        int gainedXp = this.getExperienceTracker().getGainedXP(Skill.MAGIC);
        int XPH = this.getExperienceTracker().getGainedXPPerHour(Skill.MAGIC);
        long TTL = this.getExperienceTracker().getTimeToLevel(Skill.MAGIC);
        int currentLevel = this.getSkills().getDynamic(Skill.MAGIC);

        //cursor
        Point mP = getMouse().getPosition();
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);

        //stats
        g.setColor(new Color(156,156,156));
        g.fillRect(paintArea.x, paintArea.y, paintArea.width, paintArea.height);
        g.setColor(new Color(255, 255, 255));
        g.drawString("Current Level: " + formatValue(currentLevel), paintArea.x + 10, paintArea.y + 15);
        g.drawString("casted " + spellCycles + " splash -> alchs", paintArea.x + 10, paintArea.y + 30);
        g.drawString("gainedXp: " + formatValue(gainedXp), paintArea.x + 10, paintArea.y + 45);
        g.drawString("XP/H: " + formatValue(XPH), paintArea.x + 10, paintArea.y + 60);
        g.drawString("TTL: " + formatTime(TTL), paintArea.x + 10, paintArea.y + 75);
        g.drawString("runtime: " + formatTime(runTime), paintArea.x + 10, paintArea.y + 90);
        g.drawString("status: " + scriptStatus, paintArea.x + 10, paintArea.y + 105);


    }

    private void setUp() {
        //for draggable paint
        paintHandler = new DraggablePaintHandler();
        this.bot.addMouseListener(paintHandler);
        PublicStaticFinalConstants.setHostScriptReference(this);
        this.bot.addMessageListener(this);

        //start gui, upon exit, user arguments will become set
        UserSelectedResults results = new UserSelectedResults();
        SwingGUI gui = new SwingGUI(results); //results object is modified by SwingGUI class
        try{
            while(gui.isVisable()){
                sleep(500);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        //prepare graph for main loop execution
        if(results.isParametersSet()){
            //set up graph differently based on if user is only splashing or not
            if(results.isSplashOnly()){
                SplashNode splash = new SplashNode(results.getSplashingSpell(), results.getNpcID(), true, this);
                IdleAntiban antiban = new IdleAntiban(this);
                executor = new GraphBasedNodeExecutor(splash);

                executor.addEdgeToNode(splash, splash, 99);
                executor.addEdgeToNode(splash, antiban, 1);
                executor.addEdgeToNode(antiban, splash, 1);
            }
            else{
                AlchNode alch = new AlchNode(results.getItemID(), this);
                SplashNode splash = new SplashNode(results.getSplashingSpell(), results.getNpcID(), false, this);
                AlchErrorNode alchError = new AlchErrorNode(this);
                IdleAntiban antiban = new IdleAntiban(this);

                executor = new GraphBasedNodeExecutor(alch);
                //99% to transition to splash, 1% to do generic antiban
                executor.addEdgeToNode(alch, splash, 99);
                executor.addEdgeToNode(alch, antiban, 1);

                //99% to transition to alch, 4% to do an alching error, 1% to generic antiban
                executor.addEdgeToNode(splash, alch, 95);
                executor.addEdgeToNode(splash, alchError, 4);
                executor.addEdgeToNode(splash, antiban, 1);

                //after generic antiban, 50% to either alch or splash
                executor.addEdgeToNode(antiban, alch, 50);
                executor.addEdgeToNode(antiban, splash, 50);

                //100% to splash after alch error
                executor.addEdgeToNode(alchError, splash, 1);
            }

            startTime = System.currentTimeMillis();
            getExperienceTracker().start(Skill.MAGIC);
            getBot().addPainter(MainScript.this);
        }
        else{
            PublicStaticFinalConstants.throwIllegalStateException("did not recieve user arguements from GUI , stopping script");
            stop();
        }


    }

    @Override
    public void onMessage(Message msg) throws InterruptedException {
        super.onMessage(msg);
        if(msg.getType() == Message.MessageType.GAME){
            if(msg.getMessage().contains("You do not have enough")){
                log("recieved rune shortage msg.");
                stop();
            }
        }
    }

    private String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private String formatValue(final long l) {
        return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
                : (l > 1000) ? String.format("%.1fk", ((double) l / 1000))
                : l + "";
    }

    public void incrementSpellCycles(){
        this.spellCycles++;
    }

    public void setScriptStatus(String scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    private void debugWidgets(){
        getMagic().open();
        List<RS2Widget> highAlch1 = getWidgets().containingSprite(218, 41);
        List<RS2Widget> curse1 = getWidgets().containingSprite(218, 24);
        List<RS2Widget> vul1 = getWidgets().containingSprite(218, 56);
        List<RS2Widget> enf1 = getWidgets().containingSprite(218, 57);
        List<RS2Widget> stun1 = getWidgets().containingSprite(218, 58);

        log(highAlch1);
        log(curse1);
        log(vul1);
        log(enf1);
        log(stun1);

        List<RS2Widget> highAlch0 = getWidgets().containingSprite(218, 91);
        List<RS2Widget> curse0 = getWidgets().containingSprite(218, 74);
        List<RS2Widget> vul0 = getWidgets().containingSprite(218, 106);
        List<RS2Widget> enf0 = getWidgets().containingSprite(218, 107);
        List<RS2Widget> stun0 = getWidgets().containingSprite(218, 108);

        log(highAlch0);
        log(curse0);
        log(vul0);
        log(enf0);
        log(stun0);
    }
}
