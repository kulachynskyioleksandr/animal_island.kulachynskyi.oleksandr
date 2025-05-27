package island.main;

import island.config.SimulationSettings;
import island.model.Island;

public class Main {
    public static void main(String[] args) {
        SimulationSettings settings = new SimulationSettings();
        Island island = new Island();
        island.randomAddAnimals(settings.getAnimalsToAdd());
        island.simulate(settings.getSimulationDays());
    }
}