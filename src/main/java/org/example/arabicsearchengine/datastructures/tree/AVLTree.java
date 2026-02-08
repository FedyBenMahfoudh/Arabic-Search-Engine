package org.example.arabicsearchengine.datastructures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Self-balancing AVL Tree implementation for storing Arabic roots.
 * Provides O(log n) search, insert, and delete operations.
 * 
 * @param <T> The type of data stored (must be Comparable)
 */
public class AVLTree<T extends Comparable<T>> {
    private AVLNode<T> root;
    private int size;

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    private int height(AVLNode<T> node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(AVLNode<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    public AVLNode<T> getRoot() {
        return root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getTreeHeight() {
        return height(this.getRoot());
    }

    // --- Rotations ---

    /**
     * Right rotation around node y.
     *        y                x
     *       / \              / \
     *      x   T3    -->   T1   y
     *     / \                  / \
     *   T1   T2              T2   T3
     */
    private AVLNode<T> rotateRight(AVLNode<T> y) {
        AVLNode<T> x = y.left;
        AVLNode<T> T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    /**
     * Left rotation around node x.
     *      x                   y
     *     / \                 / \
     *   T1   y      -->     x   T3
     *       / \            / \
     *     T2   T3        T1   T2
     */
    private AVLNode<T> rotateLeft(AVLNode<T> x) {
        AVLNode<T> y = x.right;
        AVLNode<T> T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // --- Balance node after insertion/deletion ---

    private AVLNode<T> balance(AVLNode<T> node) {
        updateHeight(node);
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // --- Insert ---

    public void insert(T data) {
        root = insertRec(root, data);
        size++;
    }

    private AVLNode<T> insertRec(AVLNode<T> node, T data) {
        if (node == null) {
            return new AVLNode<>(data);
        }

        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = insertRec(node.left, data);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, data);
        } else {
            // Duplicate - don't insert, decrement size to compensate
            size--;
            return node;
        }

        return balance(node);
    }

    // --- Search ---

    public T search(T key) {
        AVLNode<T> result = searchRec(root, key);
        return result != null ? result.data : null;
    }

    private AVLNode<T> searchRec(AVLNode<T> node, T key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.data);
        if (cmp < 0) {
            return searchRec(node.left, key);
        } else if (cmp > 0) {
            return searchRec(node.right, key);
        } else {
            return node;
        }
    }

    public boolean contains(T key) {
        return search(key) != null;
    }

    // --- Delete ---

    public void delete(T key) {
        if (contains(key)) {
            root = deleteRec(root, key);
            size--;
        }
    }

    private AVLNode<T> deleteRec(AVLNode<T> node, T key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.data);
        if (cmp < 0) {
            node.left = deleteRec(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, key);
        } else {
            // Node to delete found
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                // Node with two children: Get inorder successor
                AVLNode<T> successor = findMin(node.right);
                node.data = successor.data;
                node.right = deleteRec(node.right, successor.data);
            }
        }

        if (node == null) {
            return null;
        }

        return balance(node);
    }

    private AVLNode<T> findMin(AVLNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }


    public List<T> toList() {
        List<T> list = new ArrayList<>();
        inorderTraversal(list::add);
        return list;
    }



    public void printTree() {
        printTreeRec(root, "", true);
    }

    private void printTreeRec(AVLNode<T> node, String prefix, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "├── " : "└── ") + node.data + " (h=" + node.height + ")");
            printTreeRec(node.left, prefix + (isLeft ? "│   " : "    "), true);
            printTreeRec(node.right, prefix + (isLeft ? "│   " : "    "), false);
        }
    }

    public void inorderTraversal(Consumer<T> action) {
        inorderRec(root, action);
    }

    private void inorderRec(AVLNode<T> node, Consumer<T> action) {
        if (node != null) {
            inorderRec(node.left, action);
            action.accept(node.data);
            inorderRec(node.right, action);
        }
    }


    private void inOrderTraversal(AVLNode<T> node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.println(node.data);
            inOrderTraversal(node.right);
        }
    }

    private void postOrderTraversal(AVLNode<T> node) {
        if (node != null) {
            postOrderTraversal(node.left);
            postOrderTraversal(node.right);
            System.out.println(node.data);
        }
    }

    private void preOrderTraversal(AVLNode<T> node) {
        if (node != null) {
            System.out.println(node.data);
            preOrderTraversal(node.left);
            preOrderTraversal(node.right);
        }
    }
}
