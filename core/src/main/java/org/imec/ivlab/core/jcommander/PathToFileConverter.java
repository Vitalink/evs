package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;

import java.io.File;


public class PathToFileConverter implements IStringConverter<File>
{
    //@Override
    public File convert(String path) {

        File file = new File(path);

        return file;
    }

}