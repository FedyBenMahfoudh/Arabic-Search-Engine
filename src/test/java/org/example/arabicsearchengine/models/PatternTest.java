package org.example.arabicsearchengine.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternTest {

    private Pattern p;
    @BeforeEach
    void setUp() {
        p = new Pattern("فاعل" , "فاعل");
    }

    @Test
    @DisplayName("Apply to root")
    void applyToRoot() {
        Root r = new Root("كتب");
        String derivedWord = p.applyToRoot(r);

        assertEquals("كاتب", derivedWord);
    }

    @Test
    @DisplayName("Get Pattern Id")
    void getPatternId() {
        assertEquals("فاعل", p.getPatternId());
    }

    @Test
    @DisplayName("Get Structure Id")
    void getStructure() {
        assertEquals("فاعل", p.getStructure());
    }

    @Test
    @DisplayName("Equals")
    void testEquals() {
        assertNotEquals(null, p);
        assertEquals(new Pattern("فاعل" , "فاعل"),p);
        assertNotEquals(new Pattern("مفعول" , "مفعول"),p);
    }

}