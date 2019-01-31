import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


public class ScapegoatTree<T extends Comparable<T>> {

    private Node root;
    private double alpha;
    private int maxSize = 0;
    private int size = 0;

    /**
     * Inserts an element into tree, check it for balance and rebuild if necessary
     * @param element element you want to insert
     */
    void insert(T element) {
        Node inserted = insertIntoTree(element);
        int height = getNodeHeight(inserted);
        if (height > getHAlpha()) {
            Node scapegoat = findScapegoatNode(inserted);
            if (nonNull(scapegoat)) {
                Node scapegoatParent = scapegoat.getParent();
                boolean scapegoatOnParentsLeft = scapegoatParent != null && scapegoat.equals(scapegoatParent.getLeft());
                Node rebuiltSubtree = rebuildTree(getSubtreeSize(scapegoat), scapegoat);
                rebuiltSubtree.setParent(scapegoatParent);
                if (nonNull(scapegoatParent)) {
                    if (scapegoatOnParentsLeft) {
                        scapegoatParent.setLeft(rebuiltSubtree);
                    } else {
                        scapegoatParent.setRight(rebuiltSubtree);
                    }
                }
                if (root.equals(scapegoat)) {
                    root = rebuiltSubtree;
                }
                maxSize = size;
            }
        }
    }

    /**
     * Delete node from a tree and rebuilds tree
     * @param element element you want to delete
     */
    void delete(T element) {
        deleteFromTree(element);
        if (size <= alpha * maxSize) {
            root = rebuildTree(size, root);
            maxSize = size;
        }
    }

    /**
     * Finds a Node, that became a scapegoat
     * @param node given
     */
    private Node findScapegoatNode(Node node) {
        int size = 1;
        int height = 0;
        int totalSize;
        while (node.getParent() != null) {
            height++;
            totalSize = 1 + size + getSubtreeSize(getSibling(node));
            if (height > Math.floor(logarithm(1 / alpha, totalSize))) {
                return node.getParent();
            }
            node = node.getParent();
            size = totalSize;
        }
        return null;
    }


    private Node rebuildTree(int size, Node scapegoat) {
        List<Node> nodes = new ArrayList<>();
        Node currentNode = scapegoat;
        boolean done = false;
        LinkedList<Node> usedNodes = new LinkedList<>();
        while (!done) {
            if (currentNode != null) {
                usedNodes.push(currentNode);
                currentNode = currentNode.getLeft();
            } else {
                if (!usedNodes.isEmpty()) {
                    currentNode = usedNodes.pop();
                    nodes.add(currentNode);
                    currentNode = currentNode.getRight();
                } else {
                    done = true;
                }
            }
        }
        return buildTree(nodes, 0, size - 1);
    }

    /**
     *Builds rebalanced tree
     * @param nodes List of nodes
     * @return rebalanced tree
     */
    private Node buildTree(List<Node> nodes, int start, int end) {
        int middle = (int) Math.ceil(((double) (start + end)) / 2.0);
        if (start > end) {
            return null;
        }
        Node node = nodes.get(middle);
        Node leftNode = buildTree(nodes, start, middle - 1);
        node.setLeft(leftNode);
        if (nonNull(leftNode)) {
            leftNode.setParent(node);
        }
        Node rightNode = buildTree(nodes, middle + 1, end);
        node.setRight(rightNode);
        if (nonNull(rightNode)) {
            rightNode.setParent(node);
        }
        return node;
    }

    /**
     * Returns Node with the same parent as given one
     * @param node given Node
     * @return sibling node
     */
    private Node getSibling(Node node) {
        if (nonNull(node.getParent())) {
            if (node.equals(node.getParent().getLeft())) {
                return node.getParent().getRight();
            } else {
                return node.getParent().getLeft();
            }
        }
        return null;
    }

    /**
     * Finds size of a subtree
     * @param node given node
     * @return size of a subtree
     */
    int getSubtreeSize(Node node) {
        if (isNull(node))
            return 0;
        if ((isNull(node.getLeft())) && (isNull(node.getRight()))) {
            return 1;
        } else {
            return getSubtreeSize(node.getLeft())
            + getSubtreeSize(node.getRight()) + 1;
        }
    }

    /**
     * Finds a distance between root and given node
     * @param node given node
     * @return height
     */
    private int getNodeHeight(Node node) {
        if (isNull(node)) {
            return -1;
        } else if (isNull(node.getParent())) {
            return 0;
        } else {
            return getNodeHeight(node.getParent()) + 1;
        }
    }

    /**
     * Basic insertion of element. Rewrites connections between elements.
     * @param element element you want to insert
     * @return inserted node
     */
    private Node insertIntoTree(T element) {
        Node node = new Node(element);
        Node x = root;
        Node y = null;
        while (nonNull(x)) {
            y = x;
            if (node.getKey().compareTo(x.getKey()) < 0) {
                x = x.getLeft();
            } else {
                x = x.getRight();
            }
            node.setParent(y);
        }
        if (isNull(y)) {
            root = node;
        } else {
            if (node.getKey().compareTo(y.getKey()) < 0) {
                y.setLeft(node);
            } else {
                y.setRight(node);
            }
        }
        size++;
        return node;
    }

    /**
     * Basic search of node in a tree.
     * @param element sought-for key
     * @return node with a sought-for key
     */
    private Node search(T element) {
        Node node = root;
        while (nonNull(node) && nonNull(node.getKey()) && node.getKey() != element) {
            if (node.getKey().compareTo(element) > 0) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }
        return node;
    }

    /**
     * Basic removal of element. Rewrites connections between elements.
     * @param element element you want to delete
     */
    private void deleteFromTree(T element) {
        Node x = null;
        Node y;
        Node node = search(element);
        if (nonNull(node)) {
            if (isNull(node.getLeft()) || isNull(node.getRight())) {
                y = node;
            } else {
                y = treeSuccessor(node);
            }
            if (nonNull(y.getLeft())) {
                x = y.getLeft();
            }
            if (nonNull(x)) {
                x.setParent(y.getParent());
            }
            if (isNull(y.getParent())) {
                root = x;
            } else {
                if (y.equals(y.getParent().getLeft())) {
                    y.getParent().setLeft(x);
                } else {
                    y.getParent().setRight(x);
                }
            }
            if (y != node) {
                node.setKey(y.getKey());
            }
        }
        size--;
    }

    /**
     * Finds Node having the least key from bigger than given
     * @param node given Node
     * @return found Node
     */
    private Node treeSuccessor(Node node) {
        if (isNull(node.getRight())) {
            return node.getParent();
        }
        return getMinimum(node.getRight());
    }

    /**
     * Finds node with minimal value
     * @param node given Node
     * @return minimal Node in a tree
     */
    private Node getMinimum(Node node) {
        while (nonNull(node.getLeft())) {
            node = node.getLeft();
        }
        return node;
    }


    private int getHAlpha() {
        return (int) Math.floor(logarithm(1 / alpha, (double) size));
    }

    private double logarithm(double base, double value) {
        return Math.log(value) / Math.log(base);
    }

    public Node getRoot() {
        return root;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public int getSize() {
        return size;
    }
}