package GUI.WrapperClasses;

import org.osbot.rs07.api.model.NPC;

//these wrapper classes are for displaying the proper text in Jcombobox by providing a custom toString method.
public class NPCWrapper {
    private final NPC npc;

    public NPCWrapper(NPC npc){
        this.npc = npc;
    }

    @Override
    public String toString(){
        return npc.getName() + " id: " + npc.getId();
    }

    public int getItemID(){
        return npc.getId();
    }
}
