package GUI;

import GUI.WrapperClasses.ItemWrapper;
import GUI.WrapperClasses.NPCWrapper;
import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SwingGUI {
    private final JFrame mainFrame;
    private final JPanel mainPanel;
    private static final String instructions =
            "BODY RUNE SPELLS and SOUL RUNE SPELLS automatically selects the highest lvl debuff spell" +
            "\nto cast." +
            "\nThe script is also progressive, upon getting the necessary level" +
            "\nfor a higher debuff spell, it switches to it." +
            "\nHowever this is ONLY TRUE for debuff spells of the same rune type" +
            "\n(body rune spells vs soul rune spells)" +
            "\n\nEnsure that you are in the normal spellbook." +
            "\n\nRECOMMENDED: place the item to alch under where the alching icon is in the spellbook tab.";

    private static final String REFRESH_NPC = "REFRESH_NPC";
    private static final String REFRESH_ITEM = "REFRESH_ITEM";
    private static final String CONFIRM = "CONFIRM";
    private static final String CANCEL = "CANCEL";

    private Vector<NPCWrapper> nearbyNPCs;
    private Vector<ItemWrapper> inventoryItems;

    private JComboBox<NPCWrapper> dropDownNPCs;
    private JComboBox<ItemWrapper> dropDownItems;
    private JCheckBox splashOnlyCheckbox;

    private JComboBox<SplashingSpellTypes> dropDownSplashingSpells;
    public enum SplashingSpellTypes {
        BODY_RUNE_SPELLS, SOUL_RUNE_SPELLS
    }

    private boolean isVisable;

    private UserSelectedResults results;
    private Script script;

    public SwingGUI(UserSelectedResults results, Script script){
        this.results = results;
        this.script = script;
        mainFrame = new JFrame(MainScript.SCRIPT_NAME);
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
        splashOnlyCheckbox = new JCheckBox("splash only", false);

        dropDownSplashingSpells = new JComboBox<>(SplashingSpellTypes.values());
        dropDownSplashingSpells.setSelectedIndex(0);
        dropDownHolder.add(dropDownSplashingSpells);

        labelDropDownAndRefreshHolder.add(targetSpellLabel);
        labelDropDownAndRefreshHolder.add(dropDownHolder);
        labelDropDownAndRefreshHolder.add(splashOnlyCheckbox);
        labelDropDownAndRefreshHolder.add(Box.createRigidArea(new Dimension(50,0)));

        mainPanel.add(labelDropDownAndRefreshHolder);
    }

    private void setUpTargetNPCSelector(){
        JPanel labelDropDownAndRefreshHolder = new JPanel();
        labelDropDownAndRefreshHolder.setLayout(new BoxLayout(labelDropDownAndRefreshHolder, BoxLayout.X_AXIS));
        JLabel targetNPCLabel = new JLabel("target NPC");
        JPanel dropDownHolder = new JPanel();
        JButton itemRefreshBtn = new JButton("refresh NPCs");

        if(script != null){
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

        if(script != null){
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
                    if(script != null){
                        inventoryItems = new Vector<>(getItems());
                        dropDownItems = new JComboBox<>(inventoryItems);
                    }
                    break;
                case REFRESH_NPC:
                    if(script != null){
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

                    SplashingSpellTypes spell = (SplashingSpellTypes) dropDownSplashingSpells.getSelectedItem();
                    assert npc != null;
                    assert item != null;
                    assert spell != null;

                    results.setParameters(npc.getItemID(), item.getItemID(), spell, splashOnlyCheckbox.isSelected());

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
        if(script != null){
            List<NPC> attackAbleNPCs = script.getNpcs().getAll();
            attackAbleNPCs.removeIf(n -> !n.isAttackable());
            Vector<NPCWrapper> wrappedNPCs = new Vector<>();
            for(NPC npc: attackAbleNPCs){
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
        if(script != null){
            Item alchSpotItem = script.getInventory().getItemInSlot(15);
            List<Item> items = new Vector<>();
            if(alchSpotItem != null)
                items.add(alchSpotItem);
            items.addAll(Arrays.asList(script.getInventory().getItems()));

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

    private void closeAndStopScript(){
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if(script != null){
            script.stop(false);
        }

        SwingGUI.this.mainFrame.dispose();
    }

    public boolean isVisable() {
        return isVisable;
    }

}
