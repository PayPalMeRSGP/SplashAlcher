package GUI;

import org.osbot.rs07.api.ui.Spells;

public class UserSelectedResults {
    private int npcID;
    private int itemID;
    private Spells.NormalSpells splashingSpell;
    private boolean parametersSet = false;

    public void setParameters(int npcID, int itemID, Spells.NormalSpells splashingSpell) {
        this.npcID = npcID;
        this.itemID = itemID;
        this.splashingSpell = splashingSpell;
        this.parametersSet = true;
    }

    public int getNpcID() {
        return npcID;
    }

    public int getItemID() {
        return itemID;
    }

    public Spells.NormalSpells getSplashingSpell() {
        return splashingSpell;
    }

    public boolean isParametersSet() {
        return parametersSet;
    }
}
