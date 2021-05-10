package org.imec.ivlab.core.model.internal.parser;

import java.util.Set;

public interface ParsedItem<T> {

    Set<String> ignoredNodeNames();

    void setUnparsed(T unparsed);

    T getUnparsed();

    String getUnparsedAsString();



}
