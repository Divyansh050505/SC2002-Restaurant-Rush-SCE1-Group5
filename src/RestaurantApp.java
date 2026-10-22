import boundary.RestaurantUI;
import control.RestaurantController;

/**
 * Stage One application entry point: creates the controller and the
 * console UI, then starts the game.
 */
public class RestaurantApp {
    public static void main(String[] args) {
        RestaurantController controller = new RestaurantController();
        RestaurantUI ui = new RestaurantUI(controller);
        ui.start();
    }
}
