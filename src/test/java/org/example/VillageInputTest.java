package org.example;

import org.example.Village;
import org.example.VillageInput;
import org.example.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VillageInputTest {

    // Used to automatic close Mockito's after the test.
    private AutoCloseable mockitoClosable;

    // Copies original "system.in" for restore at the end.
    private final InputStream originalSystemIn = System.in;

    // Mocking the "DatabaseConnection".
    @Mock
    private DatabaseConnection mockDatabaseConnection;

    // Set up before each test.
    @BeforeEach
    public void setUp(){
        mockitoClosable = MockitoAnnotations.openMocks(this);
    }

    // Closes the mocking after each test.
    @AfterEach
    public void tearDown() throws Exception{
        mockitoClosable.close();
        System.setIn(originalSystemIn);
    }



    // Save and load test of village
    @Test
    public void saveAndLoadVillage_Test(){

        // Create input for save and load operation in a string.
        String input = "MyVillage\ny\nMyVillage\n";
        // Replace "System.in" with input.
        System.setIn(new ByteArrayInputStream(input.getBytes()));


        // Configure mocking.
        ArrayList<String> existingVillages = new ArrayList<>();
        existingVillages.add("MyVillage");


        // Mocking "TowNames" to return a list with town names.
        when(mockDatabaseConnection.GetTownNames()).thenReturn(existingVillages);
        // Mocking "SaveVillage" to return a boolean.
        when(mockDatabaseConnection.SaveVillage(any(Village.class),eq("MyVillage"))).thenReturn(true);
        // Mocking "LoadVillage" for a return of a new village instance.
        when(mockDatabaseConnection.LoadVillage("MyVillage")).thenReturn(new Village());


        // New village instance.
        Village village = new Village();
        // New villageInput instance with mocked "databaseConnection".
        VillageInput villageInput = new VillageInput(village, mockDatabaseConnection);


        // Preform a save.
        villageInput.Save();
        // Verify that "SaveVillage" was called once with expected argument.
        verify(mockDatabaseConnection, times(1)).SaveVillage(any(Village.class),eq("MyVillage"));


        // Preform a load.
        villageInput.Load();
        // Verify that "LoadVillage" was called once with expected argument.
        verify(mockDatabaseConnection, times(1)).LoadVillage("MyVillage");



    }












}
