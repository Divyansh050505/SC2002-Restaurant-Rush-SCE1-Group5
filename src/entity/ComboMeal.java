package entity;

public class ComboMeal extends MenuItem{





    public ComboMeal(MenuItem item1, MenuItem item2) {

        super(
            "ComboMeal (" + item1.getName() + " + " + item2.getName() + ")",
            ((item1.getPriceCents() + item2.getPriceCents())*90+50)/100,
            (item1.getPrepUnits() + item2.getPrepUnits())
        );

       
    }
    
}
