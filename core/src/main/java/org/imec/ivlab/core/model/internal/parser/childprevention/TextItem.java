package org.imec.ivlab.core.model.internal.parser.childprevention;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.imec.ivlab.core.model.internal.parser.ParsedContent;

@AllArgsConstructor
@Getter
public class TextItem extends ParsedContent {

    private String text;

}

