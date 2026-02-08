package org.example.arabicsearchengine.repositories;

import org.example.arabicsearchengine.datastructures.tree.AVLTree;
import org.example.arabicsearchengine.models.Root;

import java.util.List;

public class RootRepository {

    private final AVLTree<Root> rootTree;

    public RootRepository() {
        this.rootTree = new AVLTree<>();
    }

    /**Saves a root to the AVL tree.*/
    public void save(Root root) {
        rootTree.insert(root);
    }

    /**
     *Finds a root by its letters.
     * Creates a temporary Root for comparison.
     */
    public Root findByLetters(String letters) {
        if (letters == null || letters.length() != 3) {
            return null;
        }
        Root searchKey = new Root(letters);
        return rootTree.search(searchKey);
    }

    /**Checks if a root exists.*/
    public boolean exists(String letters) {

        return findByLetters(letters) != null;
    }

    /**Deletes a root by its letters.*/
    public void delete(String letters) {
        if (letters != null && letters.length() == 3) {
            Root searchKey = new Root(letters);
            rootTree.delete(searchKey);
        }
    }

    /*** Returns all roots in sorted order.*/
    public List<Root> findAll() {
        return rootTree.toList();
    }

    /**Returns the number of stored roots.*/
    public int count() {
        return rootTree.getSize();
    }
    /**
     * Checks if repository is empty.
     */
    public boolean isEmpty() {
        return rootTree.isEmpty();
    }

    /**Prints the tree structure for debugging.*/
    public void printTree() {
        rootTree.printTree();
    }

    /**Returns the height of the AVL tree.*/
    public int getTreeHeight() {
        return rootTree.getTreeHeight();
    }



}

