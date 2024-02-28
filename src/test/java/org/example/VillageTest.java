package org.example;

import com.sun.jdi.connect.Connector;
import org.example.objects.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLOutput;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VillageTest {

    private Village village;

    //Runs before all tests in this class.
    @BeforeEach
    public void setUp(){
        village = new Village();
    }



    //Nested to categorize tests using "AddWorker" method.
    @Nested
    class AddingWorkers_Test {


        // Check: 4 new workers can be added. If all worker tasks are ok.
        @Test
        public void add4Worker_UseAllOccupation_ShouldSucceed() {

            village.AddWorker("alice", "farmer");
            village.AddWorker("kalle", "builder");
            village.AddWorker("henry", "miner");
            village.AddWorker("iris", "lumberjack");

            assertEquals(4, village.getWorkers().size(), "Should have exact 4 workers now with all different occupation.");
        }


        // Test: Unaccepted occupations shall not add to worker.
        @Test
        public void addWorker_UnacceptedOccupation_ShallFail(){

            // Store quantity of workers from start (0).
            int initialWorkerCount = village.getWorkers().size();

            // Adding a worker with unacceptable occupation.
            village.AddWorker("Knut", "plumber");

            // Store quantity after adding a worker.
            int newWorkerCount = village.getWorkers().size();

            // Comparing to make sure no new workers been added with unacceptable occupation.
            assertEquals(initialWorkerCount, newWorkerCount, "Worker with unacceptable occupation shall not be added.");
        }


        // Test: Maximum amount of workers ca be added.
        @Test
        public void addMaximumWorkersAtStart_ShouldSucceed() {

            // Store default value of maximum workers.
            int allWorkersAtStart = village.getMaxWorkers();

            // Adding maximum of workers.
            for (int i = village.getWorkers().size() ; i < village.getMaxWorkers(); i++) {
                village.AddWorker("Worker" + i, "builder");
            }

            // Store value of all workers.
            int workersAddedToMaximum = village.getWorkers().size();

            assertEquals(allWorkersAtStart, workersAddedToMaximum, "Maximum allowed workers at start is 6.");
        }



        // Test: Add 6 workers to make sure "isFull" function works that is controlled by "maxWorkers" of 6 by default.
        @Test
        public void overrideAddingMaximumWorkerAtStart_ShallSucceed(){ // ÄNDRA I VILLAGE KLASSEN MED IF SATS FÖR ATT KUNNA STOPPA ADDING AV WORKERS EFTER 6ST.

            int initialWorkers = village.getMaxWorkers();

            // Add maximum amount of workers.
            for (int i = 0; i < village.getMaxWorkers(); i++) {
                village.AddWorker("Worker" + i, "builder");
            }

            village.AddWorker("One_worker_to_much_Kalle", "farmer");


            // "isFull" shall be true when value is 6 or above. NOTE! More workers can be added due to function in "VillageInput" class that's not accessible in this test for now.
            assertTrue(village.isFull(), "Maximum workers at start shall be full at 6.");
            assertEquals(village.getMaxWorkers(), village.getWorkers().size());

        }
    }




    // Test: Workers occupation is "farming" after using method "Day" 10 times.
    @Test
    public void workerContinueWithOccupation_After10Day_ShouldSucceed(){

        // Store task "farmer" in new variable "occupation".
        String occupation = "farmer";
        // Add worker for farming.
        village.AddWorker("Henry", occupation);

        // Simulate 50 days.
        for (int i = 0; i < 10; i++){
            village.Day();
        }

        // Get workers occupation after 10 days.
        Worker worker = village.getWorkers().get(0);
        String actualOccupation = worker.getOccupation();

        assertEquals(occupation, actualOccupation, "Worker should remain farming after 50 days");
    }



    // Nested to categorize tests with increased resources when using "Day" method.
    @Nested
    class WorkersEffect_1Day_Test {


        // Test: Resources value after using "Day" method 1 tíme.
        @Test
        public void withoutAddedWorkers_1Day_ShouldSucceed(){

            // Store resources in new variables AND one worker.
            int initialWorker = 0;
            int initialFood = village.getFood();
            int initialMetal = village.getMetal();
            int initialWood = village.getWood();

            // Simulate 1 day
            village.Day();

            assertEquals(initialWorker, village.getWorkers().size());
            assertEquals(initialWood, village.getWood(), "Wood should be by default 10");
            assertEquals(initialMetal, village.getMetal(), "Metal should be by default 0");
            assertEquals(initialFood, village.getFood(), "Food should be by default 10");
        }


        // Test: Food resources increases after 1 day.
        @Test
        public void farmerOneDay_FoodResourceShallIncrease_ShouldSucceed() {

            // Add worker for food resource.
            village.AddWorker("Greta", "farmer");

            // Store start value of food in new "initialFood".
            int initialFood = village.getFood();

            // Simulate 1 day.
            village.Day();

            int actualFood = village.getFood();

           // assertEquals(expectedFood, actualFood, "Food shall be 14. 10 at start + 5 per day - 1 worker eats.");
            assertTrue(initialFood < actualFood, "Food shall increased");

        }


        // Test: Wood resources increase after 1 day.
        @Test
        public void woodOneDay_WoodResourceShallIncrease_ShouldSucceed() {

            // Add worker for stone resource.
            village.AddWorker("Sven", "lumberjack");

            // Store start value of wood in "initialWood"
            int initialWood = village.getWood();

            // Simulate 1 day.
            village.Day();

            // 1 wood added per day. "initialWood" 0 + "Wood" each day 1 = 1.
            int actualWood = village.getWood();

            assertTrue(initialWood < actualWood, "Wood shall increase.");

        }


        // Test: Metal resources increase after 1 day.
        @Test
        public void metalOneDay_MetalResourceShallIncrease_ShouldSucceed() {

            // Add worker for metal resource.
            village.AddWorker("Rune", "miner");

            // Store start value of metal in "initialMetal".
            int initialMetal = village.getMetal();

            // Simulate 1 day.
            village.Day();

            // 1 metal added per day. "initialMetal" 0 + "metal" each day 1 = 1.
            int actualMetal = village.getMetal();

            assertTrue(initialMetal < actualMetal, "Metal shall increase.");
        }
    }


    // Test: 5 days with no food for worker game shall be over.
    @Test
    public void workerNoFoodUntilGameOver_6Days_ShouldSucceed(){

        // Set food to 0.
        village.setFood(0);

        // Add worker.
        village.AddWorker("worker","miner");

        // Simulate 1 day until game over due to starvation of the mining worker.
        for (int i = 0; i < 6; i++) {
            village.Day();
        }

        // "gameOver" function is true then all workers have died due to no food and game will be over.
        assertTrue(village.isGameOver(), "After 6 days of no food game shall be over");
    }



    // Test: All project and building.
    @Nested
    class ProjectTest {

        // Parameter stream on resources for easier and more efficient coding. "Wood", "metal".
        static Stream<Arguments>provideResourcesForProjects(){

            return Stream.of(
                    Arguments.of("House", 5, 0),
                    Arguments.of("Woodmill", 5, 1),
                    Arguments.of("Quarry", 3, 5),
                    Arguments.of("Farm", 5, 2),
                    Arguments.of("Castle", 50, 50)
            );


        }


        // Test: Add all project with right recourses.
        @ParameterizedTest
        @MethodSource("provideResourcesForProjects")
        public void addNewProject_WithEnoughRecourses_ShallSucceed (String project, int wood, int metal) {

            // Setup materials for building.
            village.setWood(wood);
            village.setMetal(metal);

            // Store default projects in variable
            int initialProjects = village.getProjects().size();

            // Add farm project.
            village.AddProject(project);


            assertTrue(village.getProjects().size() > initialProjects, "Project shall be increased when 'Farm' is added");

        }


        // Test: Add all projects without enough recourses.
        @ParameterizedTest
        @MethodSource("provideResourcesForProjects")
        public void addNewProject_NotEnoughRecourses_ShallFail (String project, int wood, int metal) {

            // Setup materials for building.
            village.setWood(wood);
            village.setMetal(metal);

            // Store default projects in variable
            int initialProjects = village.getProjects().size();

            village.setWood(-70);
            village.setMetal(-70);

            // Add a project farm.
            village.AddProject(project);


            assertFalse(village.getProjects().size() > initialProjects, "Project shall be increased when 'Farm' is added");

        }


        // Test: Build all buildings complete with right amount of recourses.
        @ParameterizedTest
        @MethodSource("provideResourcesForProjects")
        public void addNewProject_withEnoughResourcesAndABuilder_shallSucceed(String project, int wood, int metal) {


            // Store default projects in variable
            int initialBuildings = village.getBuildings().size();

            village.AddWorker("Bob", "builder");

            // Setup resources for buildings.
            village.setWood(wood);
            village.setMetal(metal);
            village.setFood(100);

            // Add a project from stream "provideResourcesForProjects".
            village.AddProject(project);

            // Counter for days to attempt building the project.
            int dayCounter = 0;


            // Loops until "village buildings" is greater than "default buildings" AND "daycounter" not is over 60.
            while (village.getBuildings().size() <= initialBuildings) {
                System.out.println("Day passed: " + dayCounter);
                dayCounter++;
                village.Day();
            }

            // Extra check that exactly 1 more building is complete each run.
            int expectedQuantityBuildings = initialBuildings + 1;


            System.out.println("Daycounter total: " + dayCounter);
            assertTrue(village.getBuildings().size() > initialBuildings, "Project shall be increased when '" + project + "' is added");
            assertEquals(expectedQuantityBuildings, village.getBuildings().size());


        }



    }

    @Nested
    class BuildingsEffect_3Days_Test{


        @Test
        public void completeWoodmill_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setWood(0);
            int initialWood = village.getWood();
            int initialBuilding = village.getBuildings().size();

            // Add worker to complete woodmill.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Woodmill");

            // Loops though days until new building is complete.
            while (village.getBuildings().size() <= initialBuilding){
                village.Day();
            }

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(20);

            // Add a lumberjack so resources shall increase.
            village.AddWorker("Fredd", "lumberjack");

            // Run 3 days
            for (int i = 0; i < 3; i++){
                village.Day();
            }

            // Store new values and expected from game description.
            int actual3DaysWood = village.getWood();
            int expectedWoodValue = 6;

            assertEquals(expectedWoodValue, actual3DaysWood);
            assertTrue(initialWood < actual3DaysWood);
        }


        @Test
        public void completeQuarry_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setMetal(0);
            int initialMetal = village.getMetal();
            int initialBuilding = village.getBuildings().size();

            // Add worker to complete Quarry.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Quarry");

            // Loops though days until new building is complete.
            while (village.getBuildings().size() <= initialBuilding){
                village.Day();
            }

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(20);

            // Add a miner so resources shall increase.
            village.AddWorker("Fredd", "miner");

            // Run 3 days
            for (int i = 0; i < 3; i++){
                village.Day();
            }

            // Store new values and expected from game description.
            int actual3DaysMetal= village.getMetal();
            int expectedMetalValue = 6;

            assertEquals(expectedMetalValue, actual3DaysMetal);
            assertTrue(initialMetal < actual3DaysMetal);
        }

        @Test
        public void completeFarm_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setFood(0);
            int initialFood = village.getFood();
            int initialBuilding = village.getBuildings().size();

            // Add worker to complete Farm.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Farm");

            // Loops though days until new building is complete.
            while (village.getBuildings().size() <= initialBuilding){
                village.Day();
            }

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(6); // Food set to 6 so 2 workers can eat for 3 days = 0 Food when measure result.

            // Add a miner so resources shall increase.
            village.AddWorker("Fredd", "farmer");

            // Run 3 days
            for (int i = 0; i < 3; i++){
                village.Day();
            }

            // Store new values and expected from game description.
            int actual3DaysFood = village.getFood();
            int expectedMetalValue = 30;

            assertEquals(expectedMetalValue, actual3DaysFood);
            assertTrue(initialFood < actual3DaysFood);
        }

        @Test
        public void completeHouse_RoomForWorkerShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            int initialMaxAmountWorkers = village.getMaxWorkers();
            int initialBuilding = village.getBuildings().size();

            // Add worker to complete Farm.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("House");

            // Loops though days until new building is complete.
            while (village.getBuildings().size() <= initialBuilding){
                village.Day();
            }


            // Store new values and expected from game description. 6 by default + 2 per house = 8 maxWorkers.
            int actualMaximumAmountWorkers = village.getMaxWorkers();
            int expectedMaximumWorkers = 8;

            assertEquals(expectedMaximumWorkers, actualMaximumAmountWorkers);
            assertTrue(initialMaxAmountWorkers < actualMaximumAmountWorkers);
        }

    }


    @Test
    public void fullGameRunTest_shallSucceed(){


        // Sequence 1. Set up workers and house for resources.

        System.out.println("----------------------// Sequence 1. Set up workers and house for resources.-------------------------------");
        village.AddWorker("James", "farmer");
        village.AddWorker("Bengt", "farmer");
        village.AddWorker("Henry", "builder");
        village.AddWorker("Sten", "miner");
        village.AddWorker("Iris", "lumberjack");
        village.AddWorker("Clara", "lumberjack");

        village.Day();
        village.Day();
        village.Day();
        village.Day();

        village.AddProject("House");

        village.Day();
        village.Day();
        village.Day();

        village.AddProject("House");

        village.Day();
        village.Day();
        village.Day();

        village.AddWorker("Bob", "builder");
        village.AddWorker("Steffo", "miner");
        village.AddWorker("Knut", "miner");
        village.AddWorker("Pelle", "lumberjack");

        // Check: workers values, buildings values, days passed, game is over.
        assertEquals(10, village.getWorkers().size(), "Expected 10 workers");
        assertEquals(5, village.getBuildings().size(), "Expected 5 buildings");
        assertEquals(10, village.getDaysGone(),"Expected days gone: 10");
        assertFalse(village.isGameOver(), "Game shall be finish after castle is complete.");



        // Sequence 2. Set up all buildings except castle.

        System.out.println("----------------------// Sequence 2. Set up all buildings except castle.-------------------------------");


        village.AddProject("Woodmill");
        village.AddProject("Quarry");
        village.AddProject("Farm");

        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();

        // Check: workers values, buildings values, days passed, game is over.
        assertEquals(10, village.getWorkers().size(), "Expected 10 workers");
        assertEquals(8, village.getBuildings().size(), "Expected 8 buildings");
        assertEquals(19, village.getDaysGone(), "Expected days gone: 19");
        assertFalse(village.isGameOver(), "Game shall be finish after castle is complete.");



        // Sequence 3. Collect enough wood and metal and build castle to win the game.
        System.out.println("----------------------// Sequence 3. Collect enough wood and metal and build castle to win the game..-------------------------------");


        // Check that resources is enough for castle to be built.
        assertTrue(50 <= village.getWood());
        assertTrue(50 <= village.getMetal());


        village.AddProject("Castle");

        // 25 days.
        village.Day();  village.Day();  village.Day(); village.Day(); village.Day();
        village.Day();  village.Day();  village.Day(); village.Day(); village.Day();
        village.Day();  village.Day();  village.Day(); village.Day(); village.Day();
        village.Day();  village.Day();  village.Day(); village.Day(); village.Day();
        village.Day();  village.Day();  village.Day(); village.Day(); village.Day();

        // Check: workers values, buildings values, days passed, game is over.
        assertEquals(10, village.getWorkers().size(), "Expected 10 workers");
        assertEquals(9, village.getBuildings().size(), "Expected 8 buildings");
        assertEquals(44, village.getDaysGone(), "Expected days gone: 44");
        assertTrue(village.isGameOver(), "Game shall be finish after castle is complete.");








    }


}












