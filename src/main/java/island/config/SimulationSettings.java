package island.config;

import island.model.predators.Wolf;
import island.model.predators.Python;
import island.model.predators.Fox;
import island.model.predators.Bear;
import island.model.predators.Eagle;
import island.model.herbivores.Horse;
import island.model.herbivores.Deer;
import island.model.herbivores.Rabbit;
import island.model.herbivores.Mouse;
import island.model.herbivores.Goat;
import island.model.herbivores.Sheep;
import island.model.herbivores.Boar;
import island.model.herbivores.Buffalo;
import island.model.herbivores.Duck;
import island.model.herbivores.Caterpillar;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimulationSettings {
    private final int simulationDays;
    private final Map<Class<? extends island.model.animals.Animal>, Integer> animalsToAdd;
    private final Scanner scanner;

    public SimulationSettings() {
        scanner = new Scanner(System.in);
        animalsToAdd = new HashMap<>();

        simulationDays = getValidInput("Enter number of days to simulate: ");

        animalsToAdd.put(Wolf.class, getValidInput("Enter number of Wolves: "));
        animalsToAdd.put(Python.class, getValidInput("Enter number of Pythons: "));
        animalsToAdd.put(Fox.class, getValidInput("Enter number of Foxes: "));
        animalsToAdd.put(Bear.class, getValidInput("Enter number of Bears: "));
        animalsToAdd.put(Eagle.class, getValidInput("Enter number of Eagles: "));
        animalsToAdd.put(Horse.class, getValidInput("Enter number of Horses: "));
        animalsToAdd.put(Deer.class, getValidInput("Enter number of Deer: "));
        animalsToAdd.put(Rabbit.class, getValidInput("Enter number of Rabbits: "));
        animalsToAdd.put(Mouse.class, getValidInput("Enter number of Mice: "));
        animalsToAdd.put(Goat.class, getValidInput("Enter number of Goats: "));
        animalsToAdd.put(Sheep.class, getValidInput("Enter number of Sheep: "));
        animalsToAdd.put(Boar.class, getValidInput("Enter number of Boars: "));
        animalsToAdd.put(Buffalo.class, getValidInput("Enter number of Buffaloes: "));
        animalsToAdd.put(Duck.class, getValidInput("Enter number of Ducks: "));
        animalsToAdd.put(Caterpillar.class, getValidInput("Enter number of Caterpillars: "));
    }

    private int getValidInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                return Math.max(value, 0);
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    public int getSimulationDays() {
        return simulationDays;
    }

    public Map<Class<? extends island.model.animals.Animal>, Integer> getAnimalsToAdd() {
        return animalsToAdd;
    }
}