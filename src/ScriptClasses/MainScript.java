package ScriptClasses;

import GUI.SwingGUI;
import GUI.UserSelectedResults;
import MarkovChain.MarkovNodeExecutor;

import Nodes.Alching.AlchErrorNode;
import Nodes.Alching.AlchNode;
import Nodes.Splashing.AbstractSplashNode;
import Nodes.Splashing.HoverSplashingNPC;
import Nodes.Splashing.RegularSplash;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

import static ScriptClasses.MainScript.SCRIPT_NAME;

@ScriptManifest(author = "PayPalMeRSGP", name = SCRIPT_NAME, info = "splashes a debuff spell while alching for high xph", version = 0.5, logo = "https://i.imgur.com/6WL3ad2.png")
public class MainScript extends Script implements MessageListener{

    public static final String SCRIPT_NAME = "Splash_Alcher";
    private MarkovNodeExecutor executor;
    private long startTime;
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
            return executor.executeThenTraverse();
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
            g.drawString("gainedXp: " + formatValue(gainedXp), paintArea.x + 10, paintArea.y + 30);
            g.drawString("XP/H: " + formatValue(XPH), paintArea.x + 10, paintArea.y + 45);
            g.drawString("TTL: " + formatTime(TTL), paintArea.x + 10, paintArea.y + 60);
            g.drawString("runtime: " + formatTime(runTime), paintArea.x + 10, paintArea.y + 75);
            g.drawString("status: " + scriptStatus, paintArea.x + 10, paintArea.y + 90);

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
        this.bot.addMessageListener(this);

        //start gui, upon exit, user arguments will become set
        results = new UserSelectedResults();
        SwingGUI gui = new SwingGUI(results, this); //results object is modified by SwingGUI class
        try{
            while(gui.isVisable()){
                sleep(500);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        if(results.isParametersSet()){
            this.log(results.toString());
            AlchNode alchNode = new AlchNode(results.getItemID(), this);
            RegularSplash regSplashNode = new RegularSplash(results.getSplashingSpell(), results.getNpcID(), results.isSplashOnly(), this);
            HoverSplashingNPC hoverSplashNode = new HoverSplashingNPC(results.getSplashingSpell(), results.getNpcID(), results.isSplashOnly(), this);
            AlchErrorNode alchErrorNode = new AlchErrorNode(this);
            executor = new MarkovNodeExecutor(hoverSplashNode, alchNode, alchErrorNode, regSplashNode);

            startTime = System.currentTimeMillis();
            getExperienceTracker().start(Skill.MAGIC);
            getBot().addPainter(MainScript.this);
        }
        else{
            log("did not receive user arguments from GUI , stopping script");
            stop(false);
        }
    }

    public void setScriptStatus(String scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    @Override
    public void onMessage(Message msg) throws InterruptedException {
        super.onMessage(msg);
        if(msg.getType() == Message.MessageType.GAME){
            if(msg.getMessage().toLowerCase().contains("you do not have enough")){
                log("stopping reason: received rune shortage msg.");
                stop(false);
            }

            if(msg.getMessage().toLowerCase().contains("someone else")){
                log("stopping reason: someone else is fighting that NPC.");
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
}
