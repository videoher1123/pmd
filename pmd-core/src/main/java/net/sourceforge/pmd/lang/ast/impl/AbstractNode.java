/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * Base class for implementations of the Node interface whose children
 * are stored in an array. This class provides the basic utilities to
 * link children and parent. It's used by most most nodes, but currently
 * not the antlr nodes, so downcasting {@link Node} to this class may fail
 * and is very bad practice.
 *
 * @param <T> Public interface for nodes of this language (eg JavaNode
 *            in the java module).
 */
public abstract class AbstractNode<T extends Node> implements Node {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    @Deprecated
    public static final SimpleDataKey<Object> LEGACY_USER_DATA = DataMap.simpleDataKey("legacy user data");

    // lazy initialized, many nodes don't need it
    private @Nullable DataMap<DataKey<?, ?>> userData;

    // never null, never contains null elements
    protected Node[] children = EMPTY_ARRAY;
    private AbstractNode<T> parent;
    private int childIndex;

    private DataFlowNode dataFlowNode;
    // @Deprecated?
    private String image;

    public AbstractNode() {

    }


    @Override
    public T getParent() {
        return toPublic(parent);
    }

    @Override
    public int getIndexInParent() {
        return childIndex;
    }

    @Override
    public T getChild(final int index) {
        return toPublic(children[index]);
    }

    @Override
    public int getNumChildren() {
        return children.length;
    }

    protected void setParent(final AbstractNode<T> parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    protected T toPublic(Node n) {
        return (T) n;
    }

    /**
     * This method tells the node to add its argument to the node's list of children.
     *
     * @param child The child to add
     * @param index The index to which the child will be added
     */
    protected void addChild(final AbstractNode<T> child, final int index) {
        if (index >= children.length) {
            final Node[] newChildren = new Node[index + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            children = newChildren;
        }
        children[index] = child;
        child.setChildIndex(index);
        child.setParent(this);
    }

    protected void remove() {
        // Detach current node of its parent, if any
        if (parent != null) {
            parent.removeChildAtIndex(getIndexInParent());
            setParent(null);
        }

        // TODO [autofix]: Notify action for handling text edition
    }

    protected void removeChildAtIndex(final int childIndex) {
        if (0 <= childIndex && childIndex < getNumChildren()) {
            // Remove the child at the given index
            children = ArrayUtils.remove(children, childIndex);
            // Update the remaining & left-shifted children indexes
            for (int i = childIndex; i < getNumChildren(); i++) {
                ((AbstractNode<T>) getChild(i)).setChildIndex(i);
            }
        }
    }



    @Override
    @SuppressWarnings("unchecked")
    public NodeStream<? extends T> children() {
        return (NodeStream<T>) Node.super.children();
    }



    /**
     * Sets the index of this node from the perspective of its parent. This
     * means: this.getParent().getChild(index) == this.
     *
     * @param index the child index
     */
    protected void setChildIndex(final int index) {
        childIndex = index;
    }


    @Override
    public String getImage() {
        return image;
    }

    @Override
    @Deprecated
    public void setImage(final String image) {
        this.image = image;
    }

    @Override
    public boolean hasImageEqualTo(final String image) {
        return Objects.equals(this.getImage(), image);
    }

    @Override
    public DataFlowNode getDataFlowNode() {
        if (this.dataFlowNode == null) {
            if (this.parent != null) {
                return parent.getDataFlowNode();
            }
            return null; // TODO wise?
        }
        return dataFlowNode;
    }

    @Override
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }

    /**
     * Returns true if this node has a descendant of any type among the provided types.
     *
     * @param types Types to test
     */
    public final boolean hasDescendantOfAnyType(final Class<? extends Node>... types) {
        // TODO consider implementing that with a single traversal!
        // -> this is done if you use node streams
        for (final Class<? extends Node> type : types) {
            if (hasDescendantOfType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        if (userData == null) {
            userData = DataMap.newDataMap();
        }
        return userData;
    }


    @Override
    public String toString() {
        return getXPathNodeName();
    }

}
