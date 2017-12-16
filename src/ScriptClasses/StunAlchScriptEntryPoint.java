package ScriptClasses;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(author = "PayPalMeRSGP", name = "stunAlcher v_Alpha", info = "Alpha: Stun Alch", version = 0.13, logo = "")
public class StunAlchScriptEntryPoint extends Script {

    PriorityQueueWrapper pqw;
    private long startTime;
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        ConstantsAndStatics.setHostScriptReference(this);
        pqw = new PriorityQueueWrapper();
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MAGIC);
        getBot().addPainter(StunAlchScriptEntryPoint.this);
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

        iiIiiiiiIiIi.drawString("currentLevel: " + formatValue(currentLevel), 10, 245);
        iiIiiiiiIiIi.drawString("gainedXp: " + formatValue(gainedXp), 10, 265);
        iiIiiiiiIiIi.drawString("XP/H: " + formatValue(XPH), 10, 285);
        iiIiiiiiIiIi.drawString("TTL: " + formatTime(TTL), 10, 305);
        iiIiiiiiIiIi.drawString("runtime: " + formatTime(runTime), 10, 325);
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
