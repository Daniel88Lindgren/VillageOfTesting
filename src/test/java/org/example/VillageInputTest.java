package org.example;

import org.example.Village;
import org.example.VillageInput;
import org.example.DatabaseConnection;
import org.example.objects.Building;
import org.example.objects.Project;
import org.example.objects.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VillageInputTest {


    @Nested
    class AllSaveAndLoadTests {


        // Used to automatic close Mockito's after the test.
        private AutoCloseable mockitoClosable;

        // Copies original "system.in" for restore at the end.
        private final InputStream originalSystemIn = System.in;


        // Mocking the "DatabaseConnection".
        @Mock
        private DatabaseConnection mockDatabaseConnection;

        // Set up before each test.

        @BeforeEach
        public void setUp() {
            mockitoClosable = MockitoAnnotations.openMocks(this);
        }

        // Closes the mocking after each test.
        @AfterEach
        public void tearDown() throws Exception {
            mockitoClosable.close();
            System.setIn(originalSystemIn);
        }


        @Nested
        class SaveTests {


            // Test: Input incorrect saving word.
            @Test
            public void saveWithIncorrectName_ShallFail() {

                // Create a user input for cancel.
                String userInput = "laholm\ny\n";
                System.setIn(new ByteArrayInputStream(userInput.getBytes()));

                ArrayList<String> existingVillages = new ArrayList<>();
                existingVillages.add("halmstad");

                // Configure mocking
                when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);

                Village village = new Village();
                VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);

                // Save action
                villageInput.Save();

                // Verify
                verify(mockDatabaseConnection, never()).SaveVillage(any(Village.class), eq("halmstad"));

            }


            // Test: Not use "y" on save. Results in cancel.
            @Test
            public void saveAndUseCancel_ShallFail() {

                // Create a user input for cancel.
                String userInput = "laholmVillage\nÖ\n";
                System.setIn(new ByteArrayInputStream(userInput.getBytes()));

                ArrayList<String> existingVillages = new ArrayList<>();
                existingVillages.add("laholmVillage");

                // Configure mocking
                when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);

                Village village = new Village();
                VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);

                // Save action
                villageInput.Save();

                // Verify
                verify(mockDatabaseConnection, never()).SaveVillage(any(Village.class), eq("laholmVillage"));

            }


            // Test: Error when user try to save.
            @Test
            public void saveFalseReturn_ShallFail() {

                // Simulate user input.
                String input = "MyLaholm\ny\n";
                System.setIn(new ByteArrayInputStream(input.getBytes()));

                // Mock "GetTownNames".
                when(mockDatabaseConnection.GetTownNames()).thenReturn(new ArrayList<>());

                // Mock "SaveVillage" to simulate a false return of save.
                when(mockDatabaseConnection.SaveVillage(any(Village.class), eq("MyLaholm"))).thenReturn(false);

                // New Instances.
                Village village = new Village();
                VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);

                // Save action.
                villageInput.Save();

                // Verify.
                verify(mockDatabaseConnection, times(1)).SaveVillage(any(Village.class), eq("MyLaholm"));

            }


        }


        @Nested
        class LoadTests {


            // Test: Load non existing.
            @Test
            public void loadWithIncorrectName_ShallFail() {

                // Simulate user input.
                String input = "laholm\n";
                System.setIn(new ByteArrayInputStream(input.getBytes()));

                // Configure mocking to simulate village that's not on the list.
                ArrayList<String> existingVillages = new ArrayList<>();
                existingVillages.add("halmstad");

                // Mock "GetTownNames".
                when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);

                // New instances with mocked "databaseConnection".
                Village village = new Village();
                VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);

                // Load action.
                villageInput.Load();

                // Verify that load was never succeeded.
                verify(mockDatabaseConnection, never()).LoadVillage(anyString());


            }

            @Test
            public void loadFalseReturn_ShallFail() {

                // Simulate user input.
                String input = "laholm\n";
                System.setIn(new ByteArrayInputStream(input.getBytes()));

                // Configure mock to return a list containing "laholm".
                ArrayList<String> existingVillages = new ArrayList<>();
                existingVillages.add("laholm");

                when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);

                // Mock "LoadVillage" to return "null".
                when(mockDatabaseConnection.LoadVillage("laholm")).thenReturn(null);

                // Create instances.
                Village village = new Village();
                VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);

                // Load action.
                villageInput.Load();

                // Verify
                verify(mockDatabaseConnection, times(1)).LoadVillage("laholm");


            }


        }


        // Test: Save and load test of village.
        @Test
        public void saveAndLoadVillage_ShallSucceed() {

            // Create input for save and load operation in a string.
            String input = "MyLaholm\ny\nMyLaholm\n";
            // Replace "System.in" with input.
            System.setIn(new ByteArrayInputStream(input.getBytes()));


            // Configure mocking.
            ArrayList<String> existingVillages = new ArrayList<>();
            existingVillages.add("MyLaholm");


            // Mocking "TownNames" to return a list with town names.
            when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);

            // Mocking "SaveVillage" to return a boolean.
            when(mockDatabaseConnection.SaveVillage(any(Village.class), eq("MyLaholm"))).thenReturn(true);

            // Mocking "LoadVillage" for a return of a new village instance.
            when(mockDatabaseConnection.LoadVillage("MyLaholm")).thenReturn(new Village());


            // New village instance.
            Village village = new Village();
            // New villageInput instance with mocked "databaseConnection".
            VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);


            // Preform a save.
            villageInput.Save();
            // Verify that "SaveVillage" was called once with expected argument.
            verify(mockDatabaseConnection, times(1)).SaveVillage(any(Village.class), eq("MyLaholm"));


            // Preform a load.
            villageInput.Load();
            // Verify that "LoadVillage" was called once with expected argument.
            verify(mockDatabaseConnection, times(1)).LoadVillage("MyLaholm");


        }


    }

}
