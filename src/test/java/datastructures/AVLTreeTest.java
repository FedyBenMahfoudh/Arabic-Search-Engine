package datastructures;

import org.example.arabicsearchengine.datastructures.tree.AVLTree;
import org.example.arabicsearchengine.models.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AVL Tree implementation.
 */
public class AVLTreeTest {

    private AVLTree<Root> tree;

    @BeforeEach
    public void setUp()
    {
        tree = new AVLTree<>();
    }

    @Test
    @DisplayName("Insert Single root")
    void insertSingleRoot()
    {
        Root root = new Root("كتب");
        tree.insert(root);

        assertEquals(1, tree.getSize());
        assertFalse(tree.isEmpty());
    }

    @Test
    @DisplayName("Insert multiple roots")
    void testInsertMultiple() {
        tree.insert(new Root("كتب"));
        tree.insert(new Root("درس"));
        tree.insert(new Root("علم"));

        assertEquals(3, tree.getSize());
    }

    @Test
    @DisplayName("Duplicate insertion should not increase size")
    void testDuplicateInsert() {
        tree.insert(new Root("كتب"));
        tree.insert(new Root("كتب"));

        assertEquals(1, tree.getSize());
    }

    @Test
    @DisplayName("Search existing root")
    void testSearchExists() {
        Root original = new Root("كتب");
        tree.insert(original);

        Root found = tree.search(new Root("كتب"));
        assertNotNull(found);
        assertEquals("كتب", found.getRootLetters());
    }

    @Test
    @DisplayName("Search non-existing root returns null")
    void testSearchNotExists() {
        tree.insert(new Root("كتب"));

        Root found = tree.search(new Root("قرأ"));
        assertNull(found);
    }

    @Test
    @DisplayName("Contains works correctly")
    void testContainsWorks() {
        tree.insert(new Root("كتب"));

        assertTrue(tree.contains(new Root("كتب")));
        assertFalse(tree.contains(new Root("قرأ")));
    }

    @Test
    @DisplayName("Deleting existing root")
    void testDelete() {
        tree.insert(new Root("كتب"));
        tree.insert(new Root("درس"));

        tree.delete(new Root("درس"));

        assertEquals(1, tree.getSize());
        assertFalse(tree.contains(new Root("درس")));
        assertTrue(tree.contains(new Root("كتب")));
    }

    @Test
    @DisplayName("Delete non-existing root")
    void testDeleteNotExists() {
        tree.insert(new Root("درس"));

        tree.delete(new Root("كتب"));

        assertEquals(1, tree.getSize());
    }

    @Test
    @DisplayName("In-order traversal returns sorted list")
    void testSearchExistsWithRoot() {
        tree.insert(new Root("علم"));
        tree.insert(new Root("كتب"));
        tree.insert(new Root("درس"));

        List<Root> sorted = tree.toList();

        assertEquals(3, sorted.size());

        List<Root> expectedList = List.of(
                new Root("درس"),
                new Root("علم"),
                new Root("كتب")
        );
        assertEquals(expectedList,sorted);
    }

    @Test
    @DisplayName("Tree maintains balance after insertions")
    void testBalanceAfterInsertions() {
        // Insert in order that would unbalance a BST
        tree.insert(new Root("ءءء"));
        tree.insert(new Root("بتث"));
        tree.insert(new Root("جحخ"));
        tree.insert(new Root("دذر"));
        tree.insert(new Root("زسش"));

        // AVL tree should have height of at most log2(n) + 1
        int height = tree.getTreeHeight();
        int size = tree.getSize();

        assertTrue(height <= Math.ceil(Math.log(size + 1) / Math.log(2)) + 1,
                "Tree should be balanced. Height: " + height + ", Size: " + size);
    }

    @Test
    @DisplayName("Empty tree operations")
    void testEmptyTree() {
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.getSize());
        assertNull(tree.search(new Root("كتب")));
    }
}
