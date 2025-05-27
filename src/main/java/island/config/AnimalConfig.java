package island.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimalConfig {
    private final double weight;
    private final int maxAmountPerCell;
    private final int speed;
    private final double requiredFood;
    private final String type;

    @JsonCreator
    public AnimalConfig(
            @JsonProperty("weight") double weight,
            @JsonProperty("maxAmountPerCell") int maxAmountPerCell,
            @JsonProperty("speed") int speed,
            @JsonProperty("requiredFood") double requiredFood,
            @JsonProperty("type") String type) {
        this.weight = weight;
        this.maxAmountPerCell = maxAmountPerCell;
        this.speed = speed;
        this.requiredFood = requiredFood;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxAmountPerCell() {
        return maxAmountPerCell;
    }

    public int getSpeed() {
        return speed;
    }

    public double getRequiredFood() {
        return requiredFood;
    }
}