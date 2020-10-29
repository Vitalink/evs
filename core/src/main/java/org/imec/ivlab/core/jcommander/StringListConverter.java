package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Arrays;
import java.util.List;


/**
 * Convert a comma separated list into a list of Strings.
 *
 * @author cbeust
 */
public class StringListConverter implements IStringConverter<String>, IParameterSplitter {

    public String convert(String value) {
        return value;
    }

    @Override
    public List<String> split(String value) {
        return Arrays.asList(value.split(";"));
    }
}