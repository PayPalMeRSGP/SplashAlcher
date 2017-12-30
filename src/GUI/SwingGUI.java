package GUI;

import ScriptClasses.WrapperClasses.ItemWrapper;
import ScriptClasses.WrapperClasses.NPCWrapper;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Spells;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SwingGUI {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private static final String instructions =
            "Select a target NPC and item. Refresh buttons re-polls " +
            "\nsurroundings and inventory for new NPC targets and " +
            "\ninventory items." +
            "\n\nEnsure that you are in the normal spellbook." +
            "\n\nRECOMMENDED: place the item to alch under where the \nalching icon is in the spellbook tab.";

    private static final String REFRESH_NPC = "REFRESH_NPC";
    private static final String REFRESH_ITEM = "REFRESH_ITEM";
    private static final String CONFIRM = "CONFIRM";
    private static final String CANCEL = "CANCEL";

    private Vector<NPCWrapper> nearbyNPCs;
    private Vector<ItemWrapper> inventoryItems;

    private JComboBox<NPCWrapper> dropDownNPCs;
    private JComboBox<ItemWrapper> dropDownItems;
    private JComboBox<Spells.NormalSpells> dropDownSplashingSpells;

    private static final HashMap<String, MagicSpell> magicSpellMapper = new HashMap<>();

    private boolean isVisable;

    public SwingGUI(){
        mainFrame = new JFrame(PublicStaticFinalConstants.SCRIPT_NAME);
        mainFrame.setSize(600, 400);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25,25,25,25));

        setUpInstructions();
        setUpSplashSpellSelector();
        setUpTargetNPCSelector();
        setUpAlchingItemSelector();
        setUpConfirmOrCancelBtns();


        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                closeAndStopScript();
            }
        });

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        isVisable = true;

    }

    private void setUpInstructions(){
        JTextArea instructions  = new JTextArea(SwingGUI.instructions);
        instructions.setLineWrap(true);
        instructions.setEditable(false);
        instructions.setFocusable(false);

        mainPanel.add(instructions);
    }

    private void setUpSplashSpellSelector(){
        JPanel labelDropDownAndRefreshHolder = new JPanel();
        labelDropDownAndRefreshHolder.setLayout(new BoxLayout(labelDropDownAndRefreshHolder, BoxLayout.X_AXIS));
        JLabel targetSpellLabel = new JLabel("splashing spell");
        JPanel dropDownHolder = new JPanel();

        Spells.NormalSpells[] spells = {Spells.NormalSpells.CURSE, Spells.NormalSpells.VULNERABILITY, Spells.NormalSpells.ENFEEBLE, Spells.NormalSpells.STUN};

        dropDownSplashingSpells = new JComboBox<>(spells);
        dropDownSplashingSpells.setSelectedIndex(0);
        dropDownHolder.add(dropDownSplashingSpells);

        labelDropDownAndRefreshHolder.add(targetSpellLabel);
        labelDropDownAndRefreshHolder.add(dropDownHolder);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(50,0)));

        mainPanel.add(labelDropDownAndRefreshHolder);
    }

    private void setUpTargetNPCSelector(){
        JPanel labelDropDownAndRefreshHolder = new JPanel();
        labelDropDownAndRefreshHolder.setLayout(new BoxLayout(labelDropDownAndRefreshHolder, BoxLayout.X_AXIS));
        JLabel targetNPCLabel = new JLabel("target NPC");
        JPanel dropDownHolder = new JPanel();
        JButton itemRefreshBtn = new JButton("refresh NPCs");

        if(PublicStaticFinalConstants.hostScriptReference != null){
            nearbyNPCs = getNPCs();
            dropDownNPCs = new JComboBox<>(nearbyNPCs);
        }
        else{
            dropDownNPCs = new JComboBox<>();
        }

        dropDownHolder.add(dropDownNPCs);

        labelDropDownAndRefreshHolder.add(targetNPCLabel);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(40,0)));
        labelDropDownAndRefreshHolder.add(dropDownHolder);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(40,0)));
        labelDropDownAndRefreshHolder.add(itemRefreshBtn);

        itemRefreshBtn.setActionCommand(REFRESH_NPC);
        itemRefreshBtn.addActionListener(new ButtonClickListener());

        mainPanel.add(labelDropDownAndRefreshHolder);
    }

    private void setUpAlchingItemSelector(){
        JPanel labelDropDownAndRefreshHolder = new JPanel();
        labelDropDownAndRefreshHolder.setLayout(new BoxLayout(labelDropDownAndRefreshHolder, BoxLayout.X_AXIS));
        JLabel targetItem = new JLabel("target Item");
        JPanel dropDownHolder = new JPanel();
        JButton itemRefreshBtn = new JButton("refresh Items");

        if(PublicStaticFinalConstants.hostScriptReference != null){
            inventoryItems = new Vector<>(getItems());
            dropDownItems = new JComboBox<>(inventoryItems);
        }
        else{
            dropDownItems = new JComboBox<>();
        }

        dropDownHolder.add(dropDownItems);

        labelDropDownAndRefreshHolder.add(targetItem);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(40,0)));
        labelDropDownAndRefreshHolder.add(dropDownHolder);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(40,0)));
        labelDropDownAndRefreshHolder.add(itemRefreshBtn);

        itemRefreshBtn.setActionCommand(REFRESH_ITEM);
        itemRefreshBtn.addActionListener(new ButtonClickListener());

        mainPanel.add(labelDropDownAndRefreshHolder);
    }

    private void setUpConfirmOrCancelBtns(){
        JPanel btnHolder = new JPanel();
        btnHolder.setLayout(new BoxLayout(btnHolder, BoxLayout.LINE_AXIS));
        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");
        btnHolder.add(confirmBtn);
        btnHolder.add(Box.createRigidArea(new Dimension(50,0)));
        btnHolder.add(cancelBtn);

        confirmBtn.setActionCommand(CONFIRM);
        cancelBtn.setActionCommand(CANCEL);
        confirmBtn.addActionListener(new ButtonClickListener());
        cancelBtn.addActionListener(new ButtonClickListener());

        mainPanel.add(btnHolder);
    }

    private class ButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch(command){
                case REFRESH_ITEM:
                    if(PublicStaticFinalConstants.hostScriptReference != null){
                        inventoryItems = new Vector<>(getItems());
                        dropDownItems = new JComboBox<>(inventoryItems);
                    }
                    break;
                case REFRESH_NPC:
                    if(PublicStaticFinalConstants.hostScriptReference != null){
                        nearbyNPCs = new Vector<>();
                        dropDownNPCs = new JComboBox<>(nearbyNPCs);
                    }
                    break;
                case CONFIRM:
                    ItemWrapper item = null;
                    if(dropDownItems.getSelectedItem() instanceof ItemWrapper){
                        item = (ItemWrapper) dropDownItems.getSelectedItem();
                    }

                    NPCWrapper npc = null;
                    if(dropDownNPCs.getSelectedItem() instanceof NPCWrapper){
                        npc = (NPCWrapper) dropDownNPCs.getSelectedItem();
                    }

                    Spells.NormalSpells spell = (Spells.NormalSpells) dropDownSplashingSpells.getSelectedItem();
                    assert npc != null;
                    assert item != null;
                    passParametersBack(npc.getItemID(), item.getItemID(), spell);
                    isVisable = false;
                    mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    SwingGUI.this.mainFrame.dispose();
                    break;
                case CANCEL:
                    closeAndStopScript();
                    break;
            }
        }
    }

    private Vector<NPCWrapper> getNPCs(){
        if(PublicStaticFinalConstants.hostScriptReference != null){
            List<NPC> npcs = PublicStaticFinalConstants.hostScriptReference.getNpcs().getAll();
            Vector<NPCWrapper> wrappedNPCs = new Vector<>();
            for(NPC npc: npcs){
                if(npc == null){
                    continue;
                }
                wrappedNPCs.add(new NPCWrapper(npc));
            }
            return wrappedNPCs;
        }
        return new Vector<>();

    }

    private Vector<ItemWrapper> getItems(){
        if(PublicStaticFinalConstants.hostScriptReference != null){
            List<Item> items = Arrays.asList(PublicStaticFinalConstants.hostScriptReference.getInventory().getItems());
            Vector<ItemWrapper> wrappedItems = new Vector<>();
            for(Item item: items){
                if(item == null){
                    continue;
                }
                wrappedItems.add(new ItemWrapper(item));
            }
            return wrappedItems;
        }
        return new Vector<>();
    }

    private void passParametersBack(int npc, int item, Spells.NormalSpells spell){
        PublicStaticFinalConstants.setTargetNPC(npc);
        PublicStaticFinalConstants.setTargetItem(item);
        PublicStaticFinalConstants.setSplashingSpell(spell);
    }

    private void closeAndStopScript(){
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if(PublicStaticFinalConstants.hostScriptReference != null){
            PublicStaticFinalConstants.hostScriptReference.stop(false);
        }

        SwingGUI.this.mainFrame.dispose();
    }

    public boolean isVisable() {
        return isVisable;
    }

    public static void main(String[] args){ //testing gui
        SwingGUI swingLayoutDemo = new SwingGUI();
    }

}
