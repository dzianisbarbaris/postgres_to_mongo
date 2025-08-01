package by.savik;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.service.FurnitureStreamService;
import by.savik.ui.ConsoleMenu;

import java.sql.SQLException;
import java.util.List;

public class ApplicationMain {
    public static void main(String[] args) {
        ConsoleMenu consoleMenu = new ConsoleMenu();
        /*consoleMenu.startFurnitureMenu();*/
        consoleMenu.startFurnitureStreamMenu();

    }
}
