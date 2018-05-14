package ScriptClasses;

import GUI.SwingGUI;
import GUI.UserSelectedResults;
import Nodes.*;

import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

import static ScriptClasses.Statics.SCRIPT_NAME;


@ScriptManifest(author = "PayPalMeRSGP", name = SCRIPT_NAME, info = "splashes a debuff spell while alching for high xph", version = 0.5, logo = "https://i.imgur.com/6WL3ad2.png")
public class MainScript extends Script implements MessageListener{

    private GraphBasedNodeExecutor executor;
    private long startTime;
    private int spellCycles = 0;
    private String scriptStatus = "";
    private UserSelectedResults results;
    private static final Color TRANS_GRAY = new Color(156,156,156, 127);

    private DraggablePaintHandler paintHandler;

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        setUp();
    }

    @Override
    public int onLoop() throws InterruptedException {
        if(executor != null){
            return executor.executeNodeThenTraverse();
        }
        return 1000;
    }

    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
        this.results = null;
        if(paintHandler != null){
            this.bot.removeMouseListener(paintHandler);
        }

    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        if(paintHandler != null){
            Rectangle paintArea = paintHandler.getPaintArea();
            long runTime = System.currentTimeMillis() - startTime;
            int gainedXp = this.getExperienceTracker().getGainedXP(Skill.MAGIC);
            int XPH = this.getExperienceTracker().getGainedXPPerHour(Skill.MAGIC);
            long TTL = this.getExperienceTracker().getTimeToLevel(Skill.MAGIC);
            int currentLevel = this.getSkills().getStatic(Skill.MAGIC);

            //cursor
            Point mP = getMouse().getPosition();
            g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
            g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);

            //stats
            g.setColor(TRANS_GRAY);
            g.fillRect(paintArea.x, paintArea.y, paintArea.width, paintArea.height);
            g.setColor(new Color(255, 255, 255));
            g.drawString("Current Level: " + formatValue(currentLevel), paintArea.x + 10, paintArea.y + 15);
            g.drawString("casted " + spellCycles + " splash and/or alchs", paintArea.x + 10, paintArea.y + 30);
            g.drawString("gainedXp: " + formatValue(gainedXp), paintArea.x + 10, paintArea.y + 45);
            g.drawString("XP/H: " + formatValue(XPH), paintArea.x + 10, paintArea.y + 60);
            g.drawString("TTL: " + formatTime(TTL), paintArea.x + 10, paintArea.y + 75);
            g.drawString("runtime: " + formatTime(runTime), paintArea.x + 10, paintArea.y + 90);
            g.drawString("status: " + scriptStatus, paintArea.x + 10, paintArea.y + 105);

            paintReset(g);
        }
    }

    private void paintReset(Graphics2D g){
        g.setColor(TRANS_GRAY);
        Rectangle resetArea = paintHandler.getResetPaint();
        g.fillRect(resetArea.x, resetArea.y, resetArea.width, resetArea.height);
        g.setColor(Color.WHITE);
        g.drawString("Reset Paint", resetArea.x + 10, resetArea.y + 15);
    }

    private void setUp() {
        //for draggable paint
        paintHandler = new DraggablePaintHandler();
        this.bot.addMouseListener(paintHandler);
        Statics.setHostScriptReference(this);
        this.bot.addMessageListener(this);

        //start gui, upon exit, user arguments will become set
        results = new UserSelectedResults();
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
            this.log(results.toString());
            if(results.isSplashOnly()){
                ExecutableNode splash = SplashNode.getInstance(results.getSplashingSpell(), results.getNpcID(), true, this);
                ExecutableNode antiban = IdleAntibanNode.getInstance(this);
                executor = new GraphBasedNodeExecutor(splash);

                executor.addEdgeToNode(splash, splash, 99);
                executor.addEdgeToNode(splash, antiban, 1);
                executor.addEdgeToNode(antiban, splash, 1);
            }
            else{
                ExecutableNode alch = AlchNode.getInstance(results.getItemID(), this);
                ExecutableNode splash = SplashNode.getInstance(results.getSplashingSpell(), results.getNpcID(), false, this);
                ExecutableNode alchError = AlchErrorNode.getInstance(this);
                ExecutableNode antiban = IdleAntibanNode.getInstance(this);

                executor = new GraphBasedNodeExecutor(alch);
                //99%to splash, 1% to do generic antiban
                executor.addEdgeToNode(alch, splash, 99);
                executor.addEdgeToNode(alch, antiban, 1);

                //99% to alch, 4% to do an alching error, 1% to generic antiban
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
            Statics.throwIllegalStateException("did not receive user arguments from GUI , stopping script");
            stop();
        }


    }

    @Override
    public void onMessage(Message msg) throws InterruptedException {
        super.onMessage(msg);
        if(msg.getType() == Message.MessageType.GAME){
            if(msg.getMessage().toLowerCase().contains("you do not have enough")){
                log("received rune shortage msg.");
                stop();
            }

            if(msg.getMessage().toLowerCase().contains("someone else")){
                log("someone else is fighting that NPC. TODO: world hop");
                stop(false);
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

}
