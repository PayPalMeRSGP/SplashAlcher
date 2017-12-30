package ScriptClasses;

import GUI.SwingGUI;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;

import static ScriptClasses.PublicStaticFinalConstants.SCRIPT_NAME;

@ScriptManifest(author = "PayPalMeRSGP", name = SCRIPT_NAME, info = "cast stun and alchs for high xph", version = 0.2, logo = "")
public class MainScript extends Script {

    private PriorityQueueWrapper pqw;
    private long startTime;
    private int spellCycles = 0;

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
    }

    @Override
    public int onLoop() throws InterruptedException {
        return pqw.executeTopNode();
    }

    @Override
    public void onPaint(Graphics2D iiIiiiiiIiIi) {
        super.onPaint(iiIiiiiiIiIi);
        iiIiiiiiIiIi.setColor(Color.GREEN);
        long runTime = System.currentTimeMillis() - startTime;
        int gainedXp = this.getExperienceTracker().getGainedXP(Skill.MAGIC);
        int XPH = this.getExperienceTracker().getGainedXPPerHour(Skill.MAGIC);
        long TTL = this.getExperienceTracker().getTimeToLevel(Skill.MAGIC);
        int currentLevel = this.getSkills().getDynamic(Skill.MAGIC);

        iiIiiiiiIiIi.drawString("currentLevel: " + formatValue(currentLevel), 10, 225);
        iiIiiiiiIiIi.drawString("casted " + spellCycles + " splash -> alchs", 10, 245);
        iiIiiiiiIiIi.drawString("gainedXp: " + formatValue(gainedXp), 10, 265);
        iiIiiiiiIiIi.drawString("XP/H: " + formatValue(XPH), 10, 285);
        iiIiiiiiIiIi.drawString("TTL: " + formatTime(TTL), 10, 305);
        iiIiiiiiIiIi.drawString("runtime: " + formatTime(runTime), 10, 325);
    }

    private int calculateMaxSpellCyclesPossible(int alchingItemID) throws InterruptedException {
        Inventory inv = getInventory();
        int natureCount = (int) inv.getAmount(561);
        int alchingItemCount = (int) inv.getAmount(alchingItemID);
        boolean usingSoulRune = PublicStaticFinalConstants.splashingSpell != Spells.NormalSpells.CURSE;
        int bodyOrSoulCount = (int) (usingSoulRune ? inv.getAmount(566) : inv.getAmount(559));
        int fireCount = (int) inv.getAmount(554);
        //checking if player has earth and water runes or a staff
        boolean canCastSplashSpell = getMagic().canCast(Spells.NormalSpells.CURSE, true);

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


}
