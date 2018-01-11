package ScriptClasses;

import GUI.SwingGUI;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static ScriptClasses.PublicStaticFinalConstants.SCRIPT_NAME;

@ScriptManifest(author = "PayPalMeRSGP", name = "new paint", info = "cast stun and alchs for high xph", version = 0.3, logo = "")
public class MainScript extends Script implements MouseListener{

    private PriorityQueueWrapper pqw;
    private long startTime;
    private int spellCycles = 0;
    private String scriptStatus = "";

    private Rectangle paintArea = new Rectangle(317, 207, 200, 130);
    private boolean movePaint = false;

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();

        PublicStaticFinalConstants.setHostScriptReference(this);

        SwingGUI gui = new SwingGUI();
        try{
            while(gui.isVisable()){
                sleep(500);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        pqw = new PriorityQueueWrapper();
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MAGIC);
        getBot().addPainter(MainScript.this);

        PublicStaticFinalConstants.setTotalCastableSpells(calculateMaxSpellCyclesPossible(PublicStaticFinalConstants.targetItem));
        log("can cast in total: " + PublicStaticFinalConstants.totalCastableSpells);
    }

    @Override
    public int onLoop() throws InterruptedException {
        return pqw.executeTopNode();
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

    private int calculateMaxSpellCyclesPossible(int alchingItemID) throws InterruptedException {
        Inventory inv = getInventory();
        int natureCount = (int) inv.getAmount(561);
        int alchingItemCount = (int) inv.getAmount(alchingItemID);
        boolean usingSoulRune = PublicStaticFinalConstants.splashingSpell != Spells.NormalSpells.CURSE;
        int bodyOrSoulCount = (int) (usingSoulRune ? inv.getAmount(566) : inv.getAmount(559));
        int fireCount = (int) inv.getAmount(554, 4699); //fire and lava
        //checking if player has earth and water runes or a staff
        boolean canCastSplashSpell = false;
        if(PublicStaticFinalConstants.splashingSpell != null){
            canCastSplashSpell = getMagic().canCast(PublicStaticFinalConstants.splashingSpell, true);
        }
        else{
            log("DEBUG0: no spell selected");
            stop();
        }


        int earthCount = (int) inv.getAmount(557);
        if(earthCount == 0 && canCastSplashSpell){
            earthCount = Integer.MAX_VALUE;
        }

        int waterCount = (int) inv.getAmount(555);
        if(waterCount == 0 && canCastSplashSpell){
            waterCount = Integer.MAX_VALUE;
        }

        log("nature: " + natureCount + " item: " + alchingItemCount + " fire: " + fireCount);
        log("body/soul: " + bodyOrSoulCount + " earth: " + earthCount + " water: " + waterCount);

        switch(PublicStaticFinalConstants.splashingSpell){
            case CURSE:
                return min(alchingItemCount, natureCount, fireCount/5, bodyOrSoulCount, waterCount/2, earthCount/3) - 1;
            case VULNERABILITY:
                return min(alchingItemCount, natureCount, fireCount/5, bodyOrSoulCount, waterCount/5, earthCount/5) - 1;
            case ENFEEBLE:
                return min(alchingItemCount, natureCount, fireCount/5, bodyOrSoulCount, waterCount/8, earthCount/8) - 1;
            case STUN:
                return min(alchingItemCount, natureCount, fireCount/5, bodyOrSoulCount, waterCount/12, earthCount/12) - 1;
            default:
                stop();
                log("DEBUG1: no spell selected, in switch statement");
                return 0; //error?
        }
    }

    private int min(int... args){
        int min = args[0];
        for(int num: args){
            if (num < min) {
                min = num;
            }
        }
        return min;
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

    public String getScriptStatus() {
        return scriptStatus;
    }

    public void setScriptStatus(String scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point clickPt = e.getPoint();
        if(paintArea.contains(clickPt)){
            movePaint = true;
        }
        if(movePaint){
            paintArea.x = e.getX() - paintArea.x;
            paintArea.y = e.getY() - paintArea.y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        movePaint = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
