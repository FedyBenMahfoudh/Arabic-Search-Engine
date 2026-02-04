package org.example.arabicsearchengine.datastructures.tree;

/**
 * Node for AVL Tree containing Root data.
 * @param <T> The type of data stored (must be Comparable)
 */
public class AVLNode<T extends Comparable<T>> {
    T data;
    AVLNode<T> left;
    AVLNode<T> right;
    int height;

    public AVLNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.height = 1;
    }

    public T getData() {
        return data;
    }

    public AVLNode<T> getLeft() {
        return left;
    }

    public AVLNode<T> getRight() {
        return right;
    }

    public int getHeight() {
        return height;
    }
}


