package entity;

import java.util.ArrayList;



public class Menu {
    private ArrayList<MenuItem> items;

    // Default constructor for existing tests
    public Menu() {
        this(0L);
    }

    public Menu(long seed) {
    
        
        

        items=new ArrayList<>();

        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));


        int choice = (int) Math.floorMod(seed, 3L);

        
        switch (choice) {
        
             
            case 0: items.add(
                    new ComboMeal(items.get(0), items.get(1))
            );

            break;

            case 1:  items.add(
                    new ComboMeal(items.get(1), items.get(2))
            );
            
            break;

            case 2: items.add(
                    new ComboMeal(items.get(0), items.get(2))
            );
            
            break;
        
        }
        
        
        
    }

    public MenuItem getItem(int index) {
        

        return items.get(index);
    }

    public int getItemCount() {
        
        return items.size();
    }
}
