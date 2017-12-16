package ScriptClasses;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "PayPalMeRSGP", name = "Alpha1", info = "Alpha: Stun Alch", version = 0.12, logo = "")
public class StunAlchScriptEntryPoint extends Script {

    PriorityQueueWrapper pqw;
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        ConstantsAndStatics.setHostScriptReference(this);
        pqw = new PriorityQueueWrapper();
    }

    @Override
    public int onLoop() throws InterruptedException {
        return pqw.executeTopNode();
    }
}
