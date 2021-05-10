package org.imec.ivlab.core.model.internal.parser;

import java.util.Set;

public abstract class AbstractParsedItem<T> implements ParsedItem<T> {

    private T unparsed = null;
    private String rootElementName = null;

    public AbstractParsedItem() {
    }

    public AbstractParsedItem(String rootElementName) {
        this.rootElementName = rootElementName;
    }

    @Override
    public T getUnparsed() {
        return unparsed;
    }

    @Override
    public String getUnparsedAsString() {
            return ParseHelper.objectToSimplifiedXml(unparsed, rootElementName, ignoredNodeNames());
    }

    @Override
    public Set<String> ignoredNodeNames() {
        return null;
    }

    @Override
    public void setUnparsed(T unparsed) {
        this.unparsed = unparsed;
    }

}
