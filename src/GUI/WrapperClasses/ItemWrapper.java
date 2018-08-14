package GUI.WrapperClasses;

import org.osbot.rs07.api.model.Item;

//these wrapper classes are for displaying the proper text in Jcombobox by providing a custom toString method.
public class ItemWrapper {
    private final Item item;

    public ItemWrapper(Item item){
        this.item = item;
    }

    @Override
    public String toString(){ //utilized by Jcombobox to get its entries.
        return item.getName() + " id: " + item.getId();
    }

    public int getItemID(){
        return item.getId();
    }
}
