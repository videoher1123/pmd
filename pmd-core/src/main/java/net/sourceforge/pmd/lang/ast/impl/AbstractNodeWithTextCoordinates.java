/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for imple
 */
public abstract class AbstractNodeWithTextCoordinates<T extends Node> extends AbstractNode<T> {

    protected int beginLine = -1;
    protected int endLine = -1;
    protected int beginColumn = -1;
    protected int endColumn = -1;

    protected AbstractNodeWithTextCoordinates() {
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    protected void setCoords(int bline, int bcol, int eline, int ecol) {
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
    }

}
