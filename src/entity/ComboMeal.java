package entity;

public class ComboMeal extends MenuItem{





    public ComboMeal(MenuItem item1, MenuItem item2) {

        String t1= "ComboMeal "+"("+item1.getName()+" + "+item2.getName()+")";

        int t2 = (int)(0.9*(item1.getPriceCents()+item2.getPriceCents()));

        int t3 = item1.getPrepUnits()+item2.getPrepUnits();

        super(t1, t2, t3);

       
    }
    
}
