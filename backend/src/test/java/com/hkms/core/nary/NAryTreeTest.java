package com.hkms.core.nary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("N-Ary Tree Core Algorithm Tests")
class NAryTreeTest {

    private NAryTree tree;
    private NAryNode rootNode;

    @BeforeEach
    void setUp() {
        tree = new NAryTree();
        rootNode = new NAryNode("root", "Computer Science", NodeType.ROOT);
        tree.insert(null, rootNode);
    }

    @Test
    @DisplayName("Should successfully insert hierarchical nodes across multiple levels")
    void testHierarchicalInsert() {
        NAryNode ai = new NAryNode("subj-ai", "Artificial Intelligence", NodeType.SUBJECT);
        NAryNode systems = new NAryNode("subj-sys", "Systems & Networks", NodeType.SUBJECT);

        tree.insert("root", ai);
        tree.insert("root", systems);

        NAryNode ml = new NAryNode("top-ml", "Machine Learning", NodeType.TOPIC);
        NAryNode dl = new NAryNode("sub-dl", "Deep Learning", NodeType.SUBTOPIC);
        NAryNode paper = new NAryNode("p-1", "Attention Is All You Need", NodeType.PAPER, 101L);

        tree.insert("subj-ai", ml);
        tree.insert("top-ml", dl);
        tree.insert("sub-dl", paper);

        assertEquals(6, tree.size());
        assertEquals(5, tree.getDepth());

        NAryNode retrieved = tree.findById("sub-dl");
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getChildCount());
        assertEquals("p-1", retrieved.getChildren().get(0).getId());
    }

    @Test
    @DisplayName("Should detect and prevent cyclic graph relationships when moving nodes")
    void testCycleDetectionDuringMove() {
        NAryNode ai = new NAryNode("ai", "AI", NodeType.SUBJECT);
        NAryNode ml = new NAryNode("ml", "ML", NodeType.TOPIC);
        NAryNode dl = new NAryNode("dl", "DL", NodeType.SUBTOPIC);

        tree.insert("root", ai);
        tree.insert("ai", ml);
        tree.insert("ml", dl);

        // Attempting to move 'ai' to be a child of its grandchild 'dl' must throw IllegalArgumentException
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tree.move("ai", "dl")
        );
        assertTrue(ex.getMessage().contains("Cyclic relationship detected"));

        // Moving to self should also be prevented
        assertThrows(IllegalArgumentException.class, () -> tree.move("ml", "ml"));
    }

    @Test
    @DisplayName("Should legally move a branch to another parent")
    void testLegalMove() {
        NAryNode ai = new NAryNode("ai", "AI", NodeType.SUBJECT);
        NAryNode ds = new NAryNode("ds", "Data Science", NodeType.SUBJECT);
        NAryNode stats = new NAryNode("stats", "Statistics", NodeType.TOPIC);

        tree.insert("root", ai);
        tree.insert("root", ds);
        tree.insert("ai", stats);

        assertEquals("ai", tree.findById("stats").getParentId());

        // Move 'stats' under 'ds'
        assertTrue(tree.move("stats", "ds"));

        assertEquals("ds", tree.findById("stats").getParentId());
        assertEquals(0, tree.findById("ai").getChildCount());
        assertEquals(1, tree.findById("ds").getChildCount());
    }

    @Test
    @DisplayName("Should execute correct Pre-order and Post-order DFS traversals")
    void testDfsTraversals() {
        NAryNode n1 = new NAryNode("1", "A", NodeType.SUBJECT);
        NAryNode n2 = new NAryNode("2", "B", NodeType.SUBJECT);
        NAryNode n3 = new NAryNode("3", "C", NodeType.TOPIC);

        tree.insert("root", n1);
        tree.insert("root", n2);
        tree.insert("1", n3);

        TraversalResult preOrder = tree.traverseDFS(true);
        assertEquals(4, preOrder.size());
        assertEquals("root", preOrder.getNodes().get(0).getId());

        TraversalResult postOrder = tree.traverseDFS(false);
        assertEquals(4, postOrder.size());
        // Root must be visited last in post-order
        assertEquals("root", postOrder.getNodes().get(postOrder.size() - 1).getId());
    }

    @Test
    @DisplayName("Should execute correct BFS Level-order traversal")
    void testBfsTraversal() {
        NAryNode n1 = new NAryNode("1", "Level 1A", NodeType.SUBJECT);
        NAryNode n2 = new NAryNode("2", "Level 1B", NodeType.SUBJECT);
        NAryNode n3 = new NAryNode("3", "Level 2A", NodeType.TOPIC);

        tree.insert("root", n1);
        tree.insert("root", n2);
        tree.insert("1", n3);

        TraversalResult bfs = tree.traverseBFS();
        assertEquals(4, bfs.size());
        assertEquals("root", bfs.getNodes().get(0).getId());
        assertEquals("1", bfs.getNodes().get(1).getId());
        assertEquals("2", bfs.getNodes().get(2).getId());
        assertEquals("3", bfs.getNodes().get(3).getId());
    }

    @Test
    @DisplayName("Should cascade delete subtree and collect deleted node IDs")
    void testCascadingDelete() {
        NAryNode ai = new NAryNode("ai", "AI", NodeType.SUBJECT);
        NAryNode ml = new NAryNode("ml", "ML", NodeType.TOPIC);
        NAryNode p1 = new NAryNode("p1", "Paper 1", NodeType.PAPER, 500L);

        tree.insert("root", ai);
        tree.insert("ai", ml);
        tree.insert("ml", p1);

        assertEquals(4, tree.size());

        List<String> deleted = tree.delete("ai");
        assertEquals(3, deleted.size());
        assertTrue(deleted.contains("ai"));
        assertTrue(deleted.contains("ml"));
        assertTrue(deleted.contains("p1"));

        assertEquals(1, tree.size());
        assertNull(tree.findById("ai"));
        assertNull(tree.findById("ml"));
        assertNull(tree.findById("p1"));
    }

    @Test
    @DisplayName("Should collect all paper IDs under a given subtree")
    void testSubtreePaperExtraction() {
        NAryNode ai = new NAryNode("ai", "AI", NodeType.SUBJECT);
        NAryNode p1 = new NAryNode("p1", "Paper 1", NodeType.PAPER, 1001L);
        NAryNode p2 = new NAryNode("p2", "Paper 2", NodeType.PAPER, 1002L);

        tree.insert("root", ai);
        tree.insert("ai", p1);
        tree.insert("ai", p2);

        List<Long> paperIds = tree.getSubtreePaperIds("ai");
        assertEquals(2, paperIds.size());
        assertTrue(paperIds.contains(1001L));
        assertTrue(paperIds.contains(1002L));
    }
}
