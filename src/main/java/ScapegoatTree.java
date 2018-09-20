import sun.rmi.runtime.Log;

import java.util.Scanner;
import java.lang.Math.*;

public class ScapegoatTree {

    private double alpha;

    private int treeSize;

    private Node root;

    private int height;

    private int totalSize;

    public void setRoot(Node elem) {
        this.root = elem;
    }

    public class Node {
        private int key;
        private Node left;
        private Node right;
        private Node parent;


        Node(int value) {
            this.key = value;
        }

        void setValue(int key) {
            this.key = key;
        }

        int getValue() {
            return key;
        }

        Node getLeft() {
            return left;
        }

        void setLeft(Node left) {
            this.left = left;
        }

        Node getRight() {
            return right;
        }

        void setRight(Node right) {
            this.right = right;
        }

        Node getParent() {
            return parent;
        }

        void setParent(Node parent) {
            this.parent = parent;
        }
    }

    public void nodeFinder(int key) {
        treeSize = 1;
        Node newNode = new Node(key);
        Node treeRoot = root;
        while (newNode.getParent() != null) {
            height++;
            totalSize = 1+ treeSize;
            if (height> Math.log(1)/alpha * treeSize){
                newNode.setParent();
            }
        }
    }
}
