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


        int r =(int)(Math.random()*3);

        if (r==0) {
        
                items.add(new ComboMeal(items.get(0), items.get(1)));
        }

        else if (r==1) {
        
                items.add(new ComboMeal(items.get(1), items.get(2)));
        }

        else if (r==2) {
        
                items.add(new ComboMeal(items.get(0), items.get(2)));
        }
        
        
    }

    public MenuItem getItem(int index) {
        

        return items.get(index);
    }

    public int getItemCount() {
        
        return items.size();
    }
}
