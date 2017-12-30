package ScriptClasses.WrapperClasses;

import org.osbot.rs07.api.model.NPC;

public class NPCWrapper {
    NPC npc;

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
