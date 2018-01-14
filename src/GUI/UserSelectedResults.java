package GUI;

import org.osbot.rs07.api.ui.Spells;

public class UserSelectedResults {
    private int npcID;
    private int itemID;
    private Spells.NormalSpells splashingSpell;

    public UserSelectedResults(int npcID, int itemID, Spells.NormalSpells splashingSpell) {
        this.npcID = npcID;
        this.itemID = itemID;
        this.splashingSpell = splashingSpell;
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
}
