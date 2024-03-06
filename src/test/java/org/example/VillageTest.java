package org.example;

import com.sun.jdi.connect.Connector;
import org.example.objects.Building;
import org.example.objects.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLOutput;
import java.util.ArrayList;
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

            assertEquals(4, village.getWorkers().size(), "There shall be 4 workers.");
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
            assertEquals(initialWorkerCount, newWorkerCount, "There shall not be any workers");
            assertEquals(0, village.getWorkers().size(), "There shall not be any workers");
        }


        // Test: Maximum amount of workers ca be added.
        @Test
        public void addMaximumWorkersAtStart_ShouldSucceed() {

            // Store default value of maximum workers.
            int allWorkersAtStart = village.getMaxWorkers();

            // Adding maximum of workers.
            village.AddWorker("Worker1", "farmer");
            village.AddWorker("Worker2", "farmer");
            village.AddWorker("Worker3", "farmer");
            village.AddWorker("Worker4", "farmer");
            village.AddWorker("Worker5", "farmer");
            village.AddWorker("Worker6", "farmer");

            // Store value of all workers.
            int workersAddedToMaximum = village.getWorkers().size();

            assertEquals(allWorkersAtStart, workersAddedToMaximum, "There shall be 6 workers.");
            assertEquals(6, village.getWorkers().size(), "There shall be 6 workers.");
        }



        // Test: Add 6 workers to make sure "isFull" function works that is controlled by "maxWorkers" of 6 by default.
        @Test
        public void overrideAddingMaximumWorkerAtStart_ShallSucceed(){ // ÄNDRA I VILLAGE KLASSEN MED IF SATS FÖR ATT KUNNA STOPPA ADDING AV WORKERS EFTER 6ST.

            // Add maximum amount of workers.
            village.AddWorker("Worker1", "farmer");
            village.AddWorker("Worker2", "farmer");
            village.AddWorker("Worker3", "farmer");
            village.AddWorker("Worker4", "farmer");
            village.AddWorker("Worker5", "farmer");
            village.AddWorker("Worker6", "farmer");

            // Adding one worker to much
            village.AddWorker("One_worker_to_much_Kalle", "farmer");

            assertTrue(village.isFull(), "Maximum workers at start shall be full at 6.");
            assertEquals(6, village.getWorkers().size(), "Maximum workers at start shall be full at 6.");

        }
    }




    // Test: Workers occupation is after 10 days.
    @ParameterizedTest
    @ValueSource(strings =  {"farmer", "builder", "miner", "lumberjack"})
    public void workerContinueWithOccupation_10Days_ShouldSucceed(String occupation){

        // Store task "farmer" in new variable "occupation".
        String occupationDefault = occupation;
        // Add worker for farming.
        village.AddWorker("Henry", occupation);

        // Simulate 10 days.
        for (int i = 0; i < 10; i++){
            village.Day();
        }

        // Get workers occupation after 10 days.
        Worker worker = village.getWorkers().get(0);
        String actualOccupation = worker.getOccupation();

        assertEquals(occupationDefault, actualOccupation, "Worker should remain with" + occupation +  "after 10 days");
    }



    // Nested to categorize tests with increased resources when using "Day" method.
    @Nested
    class WorkersEffect_1Day_Test {


        // Test: Resources value after using "Day" method 1 tíme.
        @Test
        public void withoutAddedWorkers_1Day_ShouldSucceed(){

            // Store resources in new variables AND one worker.
            int initialWorker = 0;

            // Simulate 1 day
            village.Day();

            assertEquals(0, village.getWood(), "Wood should be by default 10");
            assertEquals(0, village.getMetal(), "Metal should be by default 10");
            assertEquals(10, village.getFood(), "Food should be by default 10");
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


            assertTrue(initialFood < actualFood, "Food shall be 14");
            assertEquals(14, village.getFood(), "Food shall be 14");

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

            int actualWood = village.getWood();

            assertTrue(initialWood < actualWood, "Wood shall be 11");
            assertEquals(1, village.getWood(), "Wood shall be 11");

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

            int actualMetal = village.getMetal();

            assertTrue(initialMetal < actualMetal, "Metal shall be 11");
            assertEquals(1, village.getMetal(), "Metal shall be 11");
        }
    }


    // Test: 5 days with no food for worker game shall be over.
    @Test
    public void workerNoFoodUntilGameOver_6Days_ShouldSucceed(){

        // Set food to 0.
        village.setFood(0);

        // Add worker.
        village.AddWorker("worker","miner");

        // Simulate 6 day until game over due to starvation of the worker.
        for (int i = 0; i < 6; i++){
            village.Day();
        }

        // "gameOver" function is true then all workers have died due to no food and game will be over.
        assertTrue(village.isGameOver(), "After 6 days of no food game shall be over");
    }






    // Test: All project and building.
    @Nested
    class ProjectTest {

        // Parameter stream on resources for easier and more efficient coding. "Wood", "metal".
        static Stream<Arguments>provideResourcesForProjects(){//LÄGG TILL DAYS TO COMPLETE

            return Stream.of(
                    Arguments.of("House", 5, 0, 3),
                    Arguments.of("Woodmill", 5, 1, 5),
                    Arguments.of("Quarry", 3, 5, 7),
                    Arguments.of("Farm", 5, 2, 5),
                    Arguments.of("Castle", 50, 50, 50)
            );


        }


        // Test: Add all project with right recourses.
        @ParameterizedTest
        @MethodSource("provideResourcesForProjects")
        public void addNewProject_WithEnoughRecourses_ShallSucceed (String project, int wood, int metal) {// LÄGG TILL DAYS TO COMPLETE FÖR ATT FÅ BORT WHILE LOOP

            // Setup materials for building.
            village.setWood(wood);
            village.setMetal(metal);

            // Store default projects in variable
            int initialProjects = village.getProjects().size();

            // Add farm project.
            village.AddProject(project);


            assertTrue(village.getProjects().size() > initialProjects, "Project queue shall be increased by 1");
            assertEquals(1, village.getProjects().size(), "Project queue shall be increased by 1");

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

            village.setWood(3);
            village.setMetal(3);

            // Add a project farm.
            village.AddProject(project);


            assertFalse(village.getProjects().size() > initialProjects, "No project shall be in queue");
            assertEquals(0, village.getProjects().size(), "No project shall be in queue");

        }


        // Test: Build all buildings complete with right amount of recourses.
        @ParameterizedTest
        @MethodSource("provideResourcesForProjects")
        public void addNewProject_withEnoughResourcesAndABuilder_shallSucceed(String project, int wood, int metal, int daysToComplete) {

            // Store default projects in variable
            int initialBuildings = village.getBuildings().size();

            village.AddWorker("Bob", "builder");

            // Setup resources for buildings.
            village.setWood(wood);
            village.setMetal(metal);
            village.setFood(100);

            // Add a project from stream "provideResourcesForProjects".
            village.AddProject(project);

            // Simulate each day until the project is supposed to be complete.
            for (int i = 0; i < daysToComplete; i++) {
                village.Day();
            }

            // Check that exactly 1 more building is complete after the specified days.
            int expectedQuantityBuildings = initialBuildings + 1;

            assertTrue(village.getBuildings().size() > initialBuildings, "Project shall be increased when '" + project + "' is added");
            assertEquals(expectedQuantityBuildings, village.getBuildings().size(), "Project shall be increased when '" + project + "' is added");
            assertEquals(daysToComplete, village.getDaysGone(), "days to complete a building shall be same as daysToComplete for a correct gameplay");
        }

    }


    @Nested
    class BuildingsEffect_3Days_Test{


        @Test
        public void completeWoodmill_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setWood(0);
            int initialWood = village.getWood();

            // Add worker to complete woodmill.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Woodmill");

            // 5 days to complete woodmill.
            village.Day();
            village.Day();
            village.Day();
            village.Day();
            village.Day();

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(20);

            // Add a lumberjack so resources shall increase.
            village.AddWorker("Fredd", "lumberjack");

            // Run 3 days
            village.Day();
            village.Day();
            village.Day();

            // Store new values and expected from game description.
            int actual3DaysWood = village.getWood();
            int expectedWoodValue = 6;

            assertEquals(expectedWoodValue, actual3DaysWood, "Expected amount of wood: 6");
            assertTrue(initialWood < actual3DaysWood, "Expected amount of wood: 6");
        }


        @Test
        public void completeQuarry_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setMetal(0);
            int initialMetal = village.getMetal();

            // Add worker to complete Quarry.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Quarry");

            // 7 days to complete quarry.
            for (int i = 0; i < 7; i++){
                village.Day();
            }

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(20);

            // Add a miner so resources shall increase.
            village.AddWorker("Fredd", "miner");

            // Run 3 days.
            village.Day();
            village.Day();
            village.Day();

            // Store new values and expected from game description.
            int actual3DaysMetal= village.getMetal();
            int expectedMetalValue = 6;

            assertEquals(expectedMetalValue, actual3DaysMetal, "Expected amount of metal: 6");
            assertTrue(initialMetal < actual3DaysMetal, "Expected amount of metal: 6");
        }

        @Test
        public void completeFarm_ResourcesShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            village.setFood(0);
            int initialFood = village.getFood();

            // Add worker to complete Farm.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("Farm");

            // 5 days to complete farm.
            village.Day();
            village.Day();
            village.Day();
            village.Day();
            village.Day();

            // Reset resources to be able to count new ones when building is complete.
            village.setWood(0);
            village.setMetal(0);
            village.setFood(6); // Food set to 6 so 2 workers can eat for 3 days = 0 Food when measure result.

            // Add a miner so resources shall increase.
            village.AddWorker("Fredd", "farmer");

            // Run 3 days
            village.Day();
            village.Day();
            village.Day();

            // Store new values and expected from game description.
            int actual3DaysFood = village.getFood();
            int expectedFoodValue = 30;

            assertEquals(expectedFoodValue, actual3DaysFood, "Expected amount food: 30");
            assertTrue(initialFood < actual3DaysFood, "Expected amount food: 30");
        }

        @Test
        public void completeHouse_RoomForWorkerShallIncrease_ShallSucceed() {

            // Set and reset values and store in a variables.
            int initialMaxAmountWorkers = village.getMaxWorkers();

            // Add worker to complete Farm.
            village.AddWorker("Bengt", "builder");

            // Set resources to complete building.
            village.setWood(100);
            village.setMetal(100);
            village.setFood(100);

            // Add project.
            village.AddProject("House");

            // Loops though days until new building is complete.
            village.Day();
            village.Day();
            village.Day();


            // Store new values and expected from game description. 6 by default + 2 per house = 8 maxWorkers.
            int actualMaximumAmountWorkers = village.getMaxWorkers();
            int expectedMaximumWorkers = 8;

            assertEquals(expectedMaximumWorkers, actualMaximumAmountWorkers, "Expected amount of workers: 8.");
            assertTrue(initialMaxAmountWorkers < actualMaximumAmountWorkers, "Expected amount of workers: 8.");
        }

    }


    @Test
    public void fullGameRunTest_shallSucceed(){


        // Sequence 1. Set up workers and house for resources.

        System.out.println("---------------------------// Sequence 1. Set up workers and house for resources.------------------------------------");
        village.AddWorker("James", "farmer");
        village.AddWorker("Bengt", "farmer");
        village.AddWorker("Henry", "builder");
        village.AddWorker("Sten", "miner");
        village.AddWorker("Iris", "lumberjack");
        village.AddWorker("Clara", "lumberjack");

        // Tot 10 days
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

        System.out.println("---------------------------// Sequence 2. Set up all buildings except castle.------------------------------------");


        village.AddProject("Woodmill");
        village.AddProject("Quarry");
        village.AddProject("Farm");

        for (int i = 0; i < 11; i++){
            village.Day();
        }

        // Check: workers values, buildings values, days passed, game is over.
        assertEquals(10, village.getWorkers().size(), "Expected 10 workers");
        assertEquals(7, village.getBuildings().size(), "Expected 7 buildings");
        assertEquals(21, village.getDaysGone(), "Expected days gone: 21");
        assertFalse(village.isGameOver(), "Game shall be finish after castle is complete.");



        // Sequence 3. Collect enough wood and metal and build castle to win the game.
        System.out.println("---------------------------// Sequence 3. Collect enough wood and metal and build castle to win the game..------------------------------------");


        // Check that resources is enough for castle to be built.
        assertTrue(50 <= village.getWood());
        assertTrue(50 <= village.getMetal());


        village.AddProject("Castle");

        for (int i = 0; i < 25; i++){
            village.Day();
        }


        // Check: workers values, buildings values, days passed, game is over.
        assertEquals(10, village.getWorkers().size(), "Expected 10 workers");
        assertEquals(8, village.getBuildings().size(), "Expected 8 buildings");
        assertEquals(46, village.getDaysGone(), "Expected days gone: 46");
        assertTrue(village.isGameOver(), "Game shall be finish after castle is complete.");




    }

    @Nested
    class GettersAndSetters_ValueTests {

        @Test
        public void setWood_NewValue_ShallSucced() {

            village.setWood(50);
            int newValue = village.getWood();

            assertEquals(50, newValue, "Wood shall be 50");
        }

        @Test
        public void setFood_NewValue_ShallSucced() {

            village.setFood(50);
            int newValue = village.getFood();

            assertEquals(50, newValue, "Food shall be 50");
        }

        @Test
        public void setMetal_NewValue_ShallSucced() {

            village.setMetal(50);
            int newValue = village.getMetal();

            assertEquals(50, newValue, "Metal shall be 50");
        }

        @Test
        public void setMaxWorkers_NewValue_ShallSucceed(){

            village.setMaxWorkers(50);
            int newValue = village.getMaxWorkers();

            assertEquals(50, newValue , "Max workers shall be 50");
        }

        @Test
        public void setDaysGone(){

            village.setDaysGone(50);
            int newValue = village.getDaysGone();

            assertEquals(50, newValue, "Days shall be 50");
        }

        @Test
        public void setGameOver(){

            village.setGameOver(true);
            boolean newValue = village.isGameOver();

            assertEquals(true, newValue);
        }

        @Test
        public void setWoodPerDay(){

            village.setWoodPerDay(50);
            int newValue = village.getWoodPerDay();

            assertEquals(50, newValue);
        }

        @Test
        public void setFoodPerDay(){

            village.setFoodPerDay(50);
            int newValue = village.getFoodPerDay();

            assertEquals(50, newValue);
        }

        @Test
        public void setMetalPerDay(){

            village.setMetalPerDay(50);
            int newValue = village.getMetalPerDay();

            assertEquals(50, newValue);
        }

        @Test
        public void getPrintInto(){



        }









    }




}












