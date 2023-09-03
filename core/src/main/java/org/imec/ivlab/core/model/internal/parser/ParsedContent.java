package org.imec.ivlab.core.model.internal.parser;

import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParsedContent extends AbstractParsedItem<ContentType> {

    public ParsedContent() {
        super("content");
    }

}
