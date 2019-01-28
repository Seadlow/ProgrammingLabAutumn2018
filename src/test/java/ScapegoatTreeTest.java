import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ScapegoatTreeTest {


    @Test
    public void testInsertSorted() {
        ScapegoatTree tree = new ScapegoatTree();
        tree.setAlpha(0.5);
        for (int i = 1; i <= 18; i++) {
            tree.insert(i);
        }
        Assert.assertEquals(18, tree.getSize());
        Assert.assertEquals(8, tree.getRoot().getKey());
        testTreeBSTProperties(tree.getRoot());
    }

    @Test
    public void testInsertSortedDesc() {
        ScapegoatTree tree = new ScapegoatTree();
        tree.setAlpha(0.57);
        for (int i = 18; i > 0; i--) {
            tree.insert(i);
        }
        Assert.assertEquals(18, tree.getSize());
        Assert.assertEquals(12, tree.getRoot().getKey());
    }

    @Test
    public void testDeleteSortedDesc() {
        ScapegoatTree tree = new ScapegoatTree();
        tree.setAlpha(0.57);
        for (int i = 18; i > 0; i--) {
            tree.insert(i);
        }
        List<Integer> values = Arrays.asList(18, 17, 15, 14, 12, 10, 8);
        values.forEach(tree::delete);
        Assert.assertEquals(11, tree.getSize());
        Assert.assertEquals(13, tree.getRoot().getKey());
    }

    @Test
    public void testBuildBigTree() {
        ScapegoatTree tree = new ScapegoatTree();
        tree.setAlpha(1);
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }
        Assert.assertEquals(100, tree.getSize());

        for (int i = 0; i < 50; i++) {
            tree.delete(i);
        }
        Assert.assertEquals(50, tree.getSize());
    }

    @Test
    public void testGetSubtreeSize() {
        Node root = new Node(4);
        Node rightChild = new Node(6);
        rightChild.setParent(root);
        root.setRight(rightChild);
        Node leftGrandChild = new Node(5);
        leftGrandChild.setParent(rightChild);
        Node rightGrandChild = new Node(7);
        rightGrandChild.setParent(rightChild);
        rightChild.setLeft(leftGrandChild);
        rightChild.setRight(rightGrandChild);
        ScapegoatTree tree = new ScapegoatTree();
        Assert.assertEquals(tree.getSubtreeSize(root), 4);
    }

    private void testTreeBSTProperties(Node entry) {
        if (entry != null) {
            if (entry.getLeft() != null) {
                Assert.assertTrue(entry.getKey().compareTo(entry.getLeft().getKey()) > 0);
            }
            if (entry.getRight() != null) {
                Assert.assertTrue(entry.getKey().compareTo(entry.getRight().getKey()) <= 0);
            }
            testTreeBSTProperties(entry.getLeft());
            testTreeBSTProperties(entry.getRight());
        }
    }
}