# Animal Island Simulation 🏝️
#### This project is a Java-based simulation of an ecosystem on an island. The island is represented as a grid of cells (e.g., 30x30, 100x20), populated with plants and animals. Animals can eat plants or other animals, move to neighboring cells, reproduce if a suitable partner is available, and die from hunger or predation. The simulation runs for a specified number of days, logging events and providing a summary of the remaining animals at the end.
#### The project includes predators (e.g., wolves, bears) and herbivores (e.g., rabbits, deer), each with configurable attributes such as weight, speed, and food requirements. Plants grow periodically, and the ecosystem evolves dynamically based on the interactions between animals and their environment.
## Features 💡
- **Dynamic Ecosystem** 🌍: Animals eat, move, reproduce, and die based on their characteristics and environment.
- **Configurable Settings** ⚙️: Island size, animal attributes, and initial populations can be customized via a JSON configuration file.
- **Logging** 📜: All events (e.g., eating, reproduction, movement) are logged into a timestamped file for analysis.
- **Extensible Design** 🛠️: The codebase is modular, with abstract classes for animals, making it easy to add new species or behaviors.

## Project Structure 🏗️
- *__src/main/java/island/config/__* 📂: Contains configuration classes for loading settings from config.json.

- *__src/main/java/island/model/__* 📂: Core simulation logic, including the Island and Cell classes.

- *__src/main/java/island/model/animals/__* 📂: Base Animal class and interfaces (Eatable, Reproducible, Movable).

- *__src/main/java/island/model/herbivores/__* 📂: Classes for herbivorous animals (e.g., Rabbit, Deer).

- *__src/main/java/island/model/predators/__* 📂: Classes for predatory animals (e.g., Wolf, Bear).

- *__src/main/java/island/model/plants/__* 📂: Classes for plants.

- *__src/main/java/island/main/__* 📂: Entry point (Main.java) to start the simulation.

## Setup Instructions 🗺️
1. ### Clone the Repository 📥
Clone this repository to your local machine using Git:
```
git clone https://github.com/kulachynskyioleksandr/animal_island.kulachynskyi.oleksandr.git
cd animal_island.kulachynskyi.oleksandr
```
2. ### Open in Your IDE 🖥️
- Open IntelliJ IDEA.
- Click File > Open and select the animal_island.kulachynskyi.oleksandr folder.
- IntelliJ will automatically detect the project structure. If prompted, allow it to set up the project as a Maven project (if applicable).
- Ensure the JDK is set to version 11 or higher (File > Project Structure > SDK).

3. ### Add Dependencies 📦

This project uses the Jackson library for JSON parsing. If you're using Maven, the dependency is already included in the pom.xml:

```
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.19.0-rc2</version>
    </dependency>
```

4. ### Update Configuration Path 📁

The Island class loads the configuration from a hardcoded file path:

```
GlobalConfig config = ConfigLoader.loadGlobalConfig("E:\\java projects\\animal_island\\src\\main\\java\\island\\config\\config.json");
```

Before running the project, update this path to match the location of config.json on your system.

## How to Run the Simulation 🚀

1. **Start the Program** 🧑‍💻:
Run the Main.java file (src/main/java/island/main/Main.java). 
   - In your IDE, right-click Main.java and select Run.
<br> </br>
2. **Input Simulation Parameters** ⌨️:
   - The program will prompt you to enter the number of days to simulate.
   - Next, it will ask for the initial number of each animal type (e.g., "Enter number of Wolves:").
<br> </br>
3. **View Results** 📊:
   - The simulation will run for the specified number of days.
   - Events (e.g., animal movements, eating, reproduction) are logged into a file named simulation_log_YYYY-MM-DD_HH-mm-ss.txt in the project root.
   - At the end, a summary of remaining animals will be printed to the console and logged in the file.

## Customizing the Simulation 🎨
1. ### Modify Island Size and Animal Properties 🎚️

The config.json file (src/main/java/island/config/config.json) defines the island dimensions and animal properties:
```
{
  "island": {
    "width": 30,
    "height": 30
  },
  "animals": [
    {"type": "Wolf", "weight": 50.0, "maxAmountPerCell": 30, "speed": 3, "requiredFood": 8.0},
    {"type": "Python", "weight": 15.0, "maxAmountPerCell": 30, "speed": 1, "requiredFood": 3.0},
    ...
  ]
}
```
* Island Size 📏: Change the width and height to adjust the grid size (e.g., "width": 50, "height": 20).
  <br> </br>
* Animal Properties 🐺:
  - weight: The animal’s weight (affects how much food it provides when eaten).
  - maxAmountPerCell: Maximum number of this animal type per cell.
  - speed: How many cells the animal can move in one turn.
  - requiredFood: How much food the animal needs to survive.

2. ### Adjust Initial Animal Populations 🐾
When running the simulation, you can specify the initial number of each animal type via the console prompts. For example:
```
Enter number of days to simulate: 10
Enter number of Wolves: 5
Enter number of Pythons: 3
...
```

## Example Output 📋
#### Console Output
```
Simulation results after 10 days:
Remaining bears: 2
Remaining rabbits: 15
Remaining wolves: 3
...
```

#### Log File (simulation_log_YYYY-MM-DD_HH-mm-ss.txt)

```
Simulation started.
Wolf (ID: 0) placed at (12, 15)
Day 1 started.
Rabbit (ID: 3) ate a plant at (5, 7)
Wolf (ID: 0) ate Rabbit (ID: 4) at (12, 15)
Day 1 ended.
...
Simulation results after 10 days:
Remaining bears: 2
Remaining rabbits: 15
Remaining wolves: 3
...
```