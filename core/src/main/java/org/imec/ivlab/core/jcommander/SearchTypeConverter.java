package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;
import org.imec.ivlab.core.model.hub.SearchType;


public class SearchTypeConverter implements IStringConverter<SearchType>
{
    //@Override
    public SearchType convert(String searchType) {

        return SearchType.valueOf(searchType);

    }

}