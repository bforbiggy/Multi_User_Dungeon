//https://docs.google.com/document/d/1UTft7OPYdylNPbLzVUBkhq7FZCh83NydE_A8kotuw7w/edit
import controller.*;

public class Main {
    public static final String MAP_SAVE_LOCATION = "src/main/resources/map.save";
    public static final String PLAYER_SAVE_LOCATION = "src/main/resources/player.save";

    public static void main(String[] args) {
        MenuController mainController = new MenuController();
        Controller controller = null;
        while (true) {
            controller = controller == null ? mainController : controller.run();
        }
    }
}
