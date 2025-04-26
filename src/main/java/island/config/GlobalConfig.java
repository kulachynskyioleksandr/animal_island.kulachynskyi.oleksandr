package island.config;

import java.util.List;

public class GlobalConfig {
    private IslandConfig island;
    private List<AnimalConfig> animals;

    public IslandConfig getIsland() {
        return island;
    }

    public List<AnimalConfig> getAnimals() {
        return animals;
    }
}
