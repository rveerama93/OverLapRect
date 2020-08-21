package iu;

import java.util.Iterator;


public class IntervalTreeImplementation<T extends IntervalVal>  {
    private Node rootNode;
    private Node nill;
    private int treeSize;
    public IntervalTreeImplementation() {
        nill = new Node();
        rootNode = nill;
        treeSize = 0;
    }
    public int size() {
        return treeSize;
    }
    private Node search(T t) {
        return rootNode.search(t);
    }
    public Iterator<T> overlappers(T t) {
        return rootNode.overlappers(t);
    }
    //Inserts value into Interval Tree
    public void insert(T t) {
        Node z = new Node(t);
        Node y = nill;
        Node x = rootNode;
        while (!x.isNilNode()) {
            y = x;
            x.maxEndVal = Math.max(x.maxEndVal, z.maxEndVal);
            int comp = z.compareTo(x);
            x = comp == -1 ? x.leftChild : x.rightChild;
        }
        z.parentNode = y;
        if (y.isNilNode()) {
            rootNode = z;
            rootNode.isNodeBlack = true;
        } else {
            int comp = z.compareTo(y);
            if (comp < 0) {
                y.leftChild = z;
            } else {
                assert(comp == 1);
                y.rightChild = z;
            }
            z.leftChild = nill;
            z.rightChild = nill;
            z.isNodeBlack = false;
            z.insertNodeFixup();
        }
        treeSize++;
    }

    public void deleteNode(T t) {
        search(t).deleteNode();
    }


    public class Node implements IntervalVal {
        private T interval;
        private Node parentNode;
        private Node leftChild;
        private Node rightChild;
        private boolean isNodeBlack;
        private double maxEndVal;

        private Node() {
            parentNode = this;
            leftChild = this;
            rightChild = this;
            isNodeBlack = true;
        }
        public Node(T interval) {
            this.interval = interval;
            parentNode = nill;
            leftChild = nill;
            rightChild = nill;
            maxEndVal = interval.intervalEnd();
            isNodeBlack = false;
        }
        @Override
        public double intervalStart() {
            return interval.intervalStart();
        }

        @Override
        public double intervalEnd() {
            return interval.intervalEnd();
        }


        private Node search(T t) {
            Node n = this;
            while (!n.isNilNode() && t.compareTo(n) != 0) {
                n = t.compareTo(n) < 0 ? n.leftChild : n.rightChild;
            }
            return n;
        }


        private Node minimumNode() {
            Node n = this;
            while (!n.leftChild.isNilNode()) {
                n = n.leftChild;
            }
            return n;
        }



        private Node successor() {

            if (!rightChild.isNilNode()) {
                return rightChild.minimumNode();
            }

            Node x = this;
            Node y = parentNode;
            while (!y.isNilNode() && x == y.rightChild) {
                x = y;
                y = y.parentNode;
            }

            return y;
        }


        private Node minimumOverlappingNode(T t) {

            Node result = nill;
            Node n = this;

            if (!n.isNilNode() && n.maxEndVal > t.intervalStart()) {
                while (true) {
                    if (n.overlaps(t)) {
                        result = n;
                        n = n.leftChild;
                        if (n.isNilNode() || n.maxEndVal <= t.intervalStart()) {
                            break;
                        }
                    } else {
                        Node left = n.leftChild;
                        if (!left.isNilNode() && left.maxEndVal > t.intervalStart()) {
                            n = left;
                        } else {
                            if (n.intervalStart() >= t.intervalEnd()) {
                                break;
                            }
                            n = n.rightChild;
                            if (n.isNilNode() || n.maxEndVal <= t.intervalStart()) {
                                break;
                            }
                        }
                    }
                }
            }

            return result;
        }


        private Iterator<T> overlappers(T t) {
            return new OverlapperIterator(this, t);
        }


        private Node nextOverlappingNode(T t) {
            Node x = this;
            Node rtrn = nill;


            if (!rightChild.isNilNode()) {
                rtrn = x.rightChild.minimumOverlappingNode(t);
            }


            while (!x.parentNode.isNilNode() && rtrn.isNilNode()) {
                if (x == parentNode.leftChild) {
                    rtrn = x.parentNode.overlaps(t) ? x.parentNode
                            : x.parentNode.rightChild.minimumOverlappingNode(t);
                }
                x = x.parentNode;
            }
            return rtrn;
        }



        private void deleteNode() {
            if (isNilNode()) {
                return;
            }
            Node y = this;
            if (!leftChild.isNilNode() && !rightChild.isNilNode()) {
                y = successor();
                copyNodeData(y);
                maxEndFixup();
            }
            Node x = y.leftChild.isNilNode() ? y.rightChild : y.leftChild;
            x.parentNode = y.parentNode;
            if (y.isRootNode()) {
                rootNode = x;
            } else if (y== parentNode.leftChild) {
                y.parentNode.leftChild = x;
                y.maxEndFixup();
            } else {
                y.parentNode.rightChild = x;
                y.maxEndFixup();
            }

            if (y.isNodeBlack) {
                x.deleteFixup();
            }

            treeSize--;
        }



        //Checks if node is root node of tree
        public boolean isRootNode() {
            return (!isNilNode() && parentNode.isNilNode());
        }
        //Checks if node is nill
        public boolean isNilNode() {
            return this == nill;
        }
        //Checks if node is left child of its parent
        public boolean isLeftChildNode() {
            return this == parentNode.leftChild;
        }


        //Checks if node color is red
        public boolean isNodeRed() {
            return !isNodeBlack;
        }

        //Points to grandparent of node
        private Node grandparentNode() {
            return parentNode.parentNode;
        }

        private void resetMaxEnd() {
            double val = interval.intervalEnd();
            if (!leftChild.isNilNode()) {
                val = Math.max(val, leftChild.maxEndVal);
            }
            if (!rightChild.isNilNode()) {
                val = Math.max(val, rightChild.maxEndVal);
            }
            maxEndVal = val;
        }

        private void maxEndFixup() {
            Node n = this;
            n.resetMaxEnd();
            while (!n.parentNode.isNilNode()) {
                n = n.parentNode;
                n.resetMaxEnd();
            }
        }

        //Rotates node to left
        private void leftRotateNode() {
            Node y = rightChild;
            rightChild = y.leftChild;
            if (!y.leftChild.isNilNode()) {
                y.leftChild.parentNode = this;
            }
            y.parentNode = parentNode;
            if (parentNode.isNilNode()) {
                rootNode = y;
            } else if (isLeftChildNode()) {
                parentNode.leftChild = y;
            } else {
                parentNode.rightChild = y;
            }
            y.leftChild = this;
            parentNode = y;
            resetMaxEnd();
            y.resetMaxEnd();
        }

        //Rotates node to right
        private void rightRotateNode() {
            Node y = leftChild;
            leftChild = y.rightChild;
            if (!y.rightChild.isNilNode()) {
                y.rightChild.parentNode = this;
            }
            y.parentNode = parentNode;
            if (parentNode.isNilNode()) {
                rootNode = y;
            } else if (isLeftChildNode()) {
                parentNode.leftChild = y;
            } else {
                parentNode.rightChild = y;
            }
            y.rightChild = this;
            parentNode = y;

            resetMaxEnd();
            y.resetMaxEnd();
        }


        private void copyNodeData(Node o) {
            interval = o.interval;
        }


        //Restores red-black properties and interval-tree properties after node insertion
        private void insertNodeFixup() {
            Node z = this;
            while (z.parentNode.isNodeRed()) {
                if (z.parentNode== parentNode.leftChild) {
                    Node y = z.parentNode.parentNode.rightChild;
                    if (y.isNodeRed()) {
                        z.parentNode.isNodeBlack = true;
                        y.isNodeBlack = true;
                        z.grandparentNode().isNodeBlack = false;
                        z = z.grandparentNode();
                    } else {
                        if (z == parentNode.rightChild) {
                            z = z.parentNode;
                            z.leftRotateNode();
                        }
                        z.parentNode.isNodeBlack = true;
                        z.grandparentNode().isNodeBlack = false;
                        z.grandparentNode().rightRotateNode();
                    }
                } else {
                    Node y = z.grandparentNode().leftChild;
                    if (y.isNodeRed()) {
                        z.parentNode.isNodeBlack = true;
                        y.isNodeBlack = true;
                        z.grandparentNode().isNodeBlack = false;
                        z = z.grandparentNode();
                    } else {
                        if (z== parentNode.leftChild) {
                            z = z.parentNode;
                            z.rightRotateNode();
                        }
                        z.parentNode.isNodeBlack = true;
                        z.grandparentNode().isNodeBlack = false;
                        z.grandparentNode().leftRotateNode();
                    }
                }
            }
            rootNode.isNodeBlack = true;
        }


        private void deleteFixup() {
            Node x = this;
            while (!x.isRootNode() && x.isNodeBlack) {
                if (x== parentNode.leftChild) {
                    Node w = x.parentNode.rightChild;
                    if (w.isNodeRed()) {
                        w.isNodeBlack = true;
                        x.parentNode.isNodeBlack = false;
                        x.parentNode.leftRotateNode();
                        w = x.parentNode.rightChild;
                    }
                    if (w.leftChild.isNodeBlack && w.rightChild.isNodeBlack) {
                        w.isNodeBlack = false;
                        x = x.parentNode;
                    } else {
                        if (w.rightChild.isNodeBlack) {
                            w.leftChild.isNodeBlack = true;
                            w.isNodeBlack = false;
                            w.rightRotateNode();
                            w = x.parentNode.rightChild;
                        }
                        w.isNodeBlack = x.parentNode.isNodeBlack;
                        x.parentNode.isNodeBlack = true;
                        w.rightChild.isNodeBlack = true;
                        x.parentNode.leftRotateNode();
                        x = rootNode;
                    }
                } else {
                    Node w = x.parentNode.leftChild;
                    if (w.isNodeRed()) {
                        w.isNodeBlack = true;
                        x.parentNode.isNodeBlack = false;
                        x.parentNode.rightRotateNode();
                        w = x.parentNode.leftChild;
                    }
                    if (w.leftChild.isNodeBlack && w.rightChild.isNodeBlack) {
                        w.isNodeBlack = false;
                        x = x.parentNode;
                    } else {
                        if (w.leftChild.isNodeBlack) {
                            w.rightChild.isNodeBlack = true;
                            w.isNodeBlack = false;
                            w.leftRotateNode();
                            w = x.parentNode.leftChild;
                        }
                        w.isNodeBlack = x.parentNode.isNodeBlack;
                        x.parentNode.isNodeBlack = true;
                        w.leftChild.isNodeBlack = true;
                        x.parentNode.rightRotateNode();
                        x = rootNode;
                    }
                }
            }
            x.isNodeBlack = true;
        }

    }

    private class OverlappingNodeIterator implements Iterator<Node> {
        private Node next;
        private T interval;
        private OverlappingNodeIterator(Node root, T t) {
            interval = t;
            next = root.minimumOverlappingNode(interval);
        }
        @Override
        public boolean hasNext() {
            return !next.isNilNode();
        }
        @Override
        public Node next() {
            Node rtrn = next;
            next = rtrn.nextOverlappingNode(interval);
            return rtrn;
        }
    }

    private class OverlapperIterator implements Iterator<T> {
        private OverlappingNodeIterator nodeIter;
        private OverlapperIterator(Node root, T t) {
            nodeIter = new OverlappingNodeIterator(root, t);
        }
        @Override
        public boolean hasNext() {
            return nodeIter.hasNext();
        }
        @Override
        public T next() {
            return nodeIter.next().interval;
        }
    }
}

