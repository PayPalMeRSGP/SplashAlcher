import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "PayPalMeRSGP", name = "Script1: StunAlch", info = "Alpha: Stun Alch", version = 0.1, logo = "")
public class StunAlchScriptEntryPoint extends Script {

    PQWrapper pqw;
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        pqw = new PQWrapper(this);
    }

    @Override
    public int onLoop() throws InterruptedException {
        return pqw.executeNodeAction();
    }
}
