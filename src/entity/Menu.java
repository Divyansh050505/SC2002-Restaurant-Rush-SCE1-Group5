package entity;

import java.util.ArrayList;

/**
 * Stage One menu skeleton.
 */
public class Menu {
    private ArrayList<MenuItem> items;

    public Menu() {
        

        items=new ArrayList<>();

        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));
        
    }

    public MenuItem getItem(int index) {
        

        return items.get(index);
    }

    public int getItemCount() {
        
        return items.size();
    }
}
