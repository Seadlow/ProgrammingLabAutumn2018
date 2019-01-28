import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class ScapegoatTree<T extends Comparable<T>> {

    private Node root;
    private double alpha;
    private int maxSize = 0;
    private int size = 0;


    public Node insert(T element) {
        Node inserted = insertIntoTree(element);
        int height = getNodeHeight(inserted);
        if (height > getHAlpha()) {
            Node scapegoat = findScapegoatNode(inserted);
            Node scapegoatParent = scapegoat.getParent();
            boolean scapegoatOnParentsLeft = scapegoatParent != null && scapegoat.equals(scapegoatParent.getLeft());
            Node rebuiltSubtree = rebuildTree(getSubtreeSize(scapegoat), scapegoat);
            rebuiltSubtree.setParent(scapegoatParent);
            if (scapegoatParent != null) {
                if (scapegoatOnParentsLeft) {
                    scapegoatParent.setLeft(rebuiltSubtree);
                } else {
                    scapegoatParent.setRight(rebuiltSubtree);
                }
            }
            if (scapegoat == root) {
                root = rebuiltSubtree;
            }
            maxSize = size;
        }
        return inserted;
    }

    public Node delete(T element) {
        Node replaceNode = deleteFromTree(element);
        if (size <= alpha * maxSize) {
            root = rebuildTree(size, root);
            maxSize = size;
        }
        return replaceNode;
    }

    protected Node findScapegoatNode(Node node) {
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

    protected Node rebuildTree(int size, Node scapegoat) {
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

    private Node buildTree(List<Node> nodes, int start, int end) {
        int middle = (int) Math.ceil(((double) (start + end)) / 2.0);
        if (start > end) {
            return null;
        }
        Node node = nodes.get(middle);
        Node leftNode = buildTree(nodes, start, middle - 1);
        node.setLeft(leftNode);
        if (leftNode != null) {
            leftNode.setParent(node);
        }
        Node rightNode = buildTree(nodes, middle + 1, end);
        node.setRight(rightNode);
        if (rightNode != null) {
            rightNode.setParent(node);
        }
        return node;
    }

    private Node getSibling(Node node) {
        if (node.getParent() != null) {
            if (node.equals(node.getParent().getLeft())) {
                return node.getParent().getRight();
            } else {
                return node.getParent().getLeft();
            }
        }
        return null;
    }

    protected int getSubtreeSize(Node node) {
        if (node == null) {
            return 0;
        }
        if ((node.getLeft() == null) && (node.getRight() == null)) {
            return 1;
        } else {
            int sum = 1;
            sum += getSubtreeSize(node.getLeft());
            sum += getSubtreeSize(node.getRight());
            return sum;
        }
    }

    protected int getNodeHeight(Node node) {
        if (node == null) {
            return -1;
        } else if (node.getParent() == null) {
            return 0;
        } else {
            return getNodeHeight(node.getParent()) + 1;
        }
    }

    public Node insertIntoTree(T element) {
        if (root == null) {
            root = new Node(element);
            size++;
            return root;
        }
        Node insertParentNode = null;
        Node searchTempNode = root;
        while (searchTempNode != null && searchTempNode.getKey() != null) {
            insertParentNode = searchTempNode;
            if (searchTempNode.getKey().compareTo(element) > 0) {
                searchTempNode = searchTempNode.getLeft();
            } else {
                searchTempNode = searchTempNode.getRight();
            }
        }

        Node newNode = new Node(element);
        newNode.setParent(insertParentNode);
        if (insertParentNode.getKey().compareTo(newNode.getKey()) > 0) {
            insertParentNode.setLeft(newNode);
        } else {
            insertParentNode.setRight(newNode);
        }
        size++;
        return newNode;
    }

    public Node deleteFromTree(T element) {
        Node deleteNode = search(element);
        return Optional.ofNullable(deleteFromTree(deleteNode)).orElse(null);
    }

    public Node search(T element) {
        Node node = root;
        while (node != null && node.getKey() != null && node.getKey() != element) {
            if (node.getKey().compareTo(element) > 0) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }
        return node;
    }

    protected Node deleteFromTree(Node deleteNode) {
        Node nodeToReturn = null;
        if (deleteNode != null) {
            if (deleteNode.getLeft() == null) {
                nodeToReturn = transplant(deleteNode, deleteNode.getRight());
            } else if (deleteNode.getRight() == null) {
                nodeToReturn = transplant(deleteNode, deleteNode.getLeft());
            } else {
                Node successorNode = getMinimum(deleteNode.getRight());
                if (!deleteNode.equals(successorNode.getParent())) {
                    transplant(successorNode, successorNode.getRight());
                    successorNode.setRight(deleteNode.getRight());
                    successorNode.getRight().setParent(successorNode);
                }
                transplant(deleteNode, successorNode);
                successorNode.setLeft(deleteNode.getLeft());
                successorNode.getLeft().setParent(successorNode);
                nodeToReturn = successorNode;
            }
            size--;
        }
        return nodeToReturn;
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

    private Node getMinimum(Node node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private Node transplant(Node nodeToReplace, Node newNode) {
        if (nodeToReplace.getParent() == null) {
            this.root = newNode;
        } else if (nodeToReplace == nodeToReplace.getParent().getLeft()) {
            nodeToReplace.getParent().setLeft(newNode);
        } else {
            nodeToReplace.getParent().setRight(newNode);
        }
        if (newNode != null) {
            newNode.setParent(nodeToReplace.getParent());
        }
        return newNode;
    }

    private int getHAlpha() {
        return (int) Math.floor(logarithm(1 / alpha, (double) size));
    }

    private double logarithm(double base, double value) {
        return Math.log(value) / Math.log(base);
    }
}