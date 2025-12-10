package com.example.cst338_project02_andriodstudio;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.example.cst338_project02_andriodstudio.database.entities.User;

import org.junit.Test;

public class AdminUserTest {


    @Test
    public void adminUser_isRecognizedAsAdmin() {
        User admin = new User("admin1", "admin1");
        admin.setAdmin(true);

        assertTrue("Admin user should return true for isAdmin()", admin.isAdmin());
    }


    @Test
    public void normalUser_isNotAdmin() {
        User testUser = new User("testuser1", "testuser1");
        testUser.setAdmin(false);

        assertFalse("Normal user should NOT be admin", testUser.isAdmin());
    }
}
