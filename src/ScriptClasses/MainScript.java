package ScriptClasses;

import GUI.SwingGUI;
import Nodes.AlchErrorNode;
import Nodes.AlchNode;
import Nodes.SplashErrorNode;
import Nodes.SplashNode;
import org.osbot.rs07.api.Inventory;

import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import static ScriptClasses.PublicStaticFinalConstants.SCRIPT_NAME;


@ScriptManifest(author = "PayPalMeRSGP", name = SCRIPT_NAME, info = "splashes a debuff spell while alching for high xph", version = 0.4, logo = "")
public class MainScript extends Script implements MouseListener, MouseMotionListener, MessageListener{

    private NodeExecutor executor;
    private long startTime;
    private int spellCycles = 0;
    private String scriptStatus = "";

    //for draggable paint
    private int xOffset = 0;
    private int yOffset = 0;
    private int paintRectangleTopLeftX = 315;
    private int paintRectangleTopLeftY = 0;
    private Rectangle paintArea = new Rectangle(paintRectangleTopLeftX, paintRectangleTopLeftY, 200, 115);
    private boolean movingPaint = false;

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
        this.bot.removeMouseListener(this);
        this.bot.getCanvas().removeMouseMotionListener(this);
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
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

    private void setUp() throws InterruptedException {
        //for draggable paint
        this.bot.addMouseListener(this);
        this.bot.getCanvas().addMouseMotionListener(this);
        PublicStaticFinalConstants.setHostScriptReference(this);
        this.bot.addMessageListener(this);

        //start gui, upon exit, user arguments will become set
        SwingGUI gui = new SwingGUI();
        try{
            while(gui.isVisable()){
                sleep(500);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        //prepare graph for main loop execution
        executor = new NodeExecutor(AlchNode.getAlchNodeInstance());
        executor.addEdgeToNode(AlchNode.getAlchNodeInstance(), SplashNode.getSplashNodeInstance(), 1);
        executor.addEdgeToNode(SplashNode.getSplashNodeInstance(), AlchNode.getAlchNodeInstance(), 95);
        executor.addEdgeToNode(SplashNode.getSplashNodeInstance(), AlchErrorNode.getAlchErrorNodeInstance(), 5);

        executor.addEdgeToNode(SplashErrorNode.getSplashErrorNodeInstance(), AlchNode.getAlchNodeInstance(), 1);
        executor.addEdgeToNode(AlchErrorNode.getAlchErrorNodeInstance(), SplashNode.getSplashNodeInstance(), 1);


        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MAGIC);
        getBot().addPainter(MainScript.this);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        //not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point clickPt = e.getPoint();
        if(paintArea.contains(clickPt)){
            movingPaint = true;
            xOffset = clickPt.x - paintArea.x;
            yOffset = clickPt.y - paintArea.y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        movingPaint = false;
        xOffset = 0;
        yOffset = 0;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //not used
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if(movingPaint){
            Point mousePos = e.getPoint();
            paintArea.x = mousePos.x - xOffset;
            paintArea.y = mousePos.y - yOffset;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //not used
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
