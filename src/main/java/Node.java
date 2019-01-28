import java.util.Objects;

public class Node<T extends Comparable<T>> {

    private T key;
    private Node parent;
    private Node left;
    private Node right;

    public Node(T key) {
        this.key = key;
    }

    public T getKey() {

        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}

