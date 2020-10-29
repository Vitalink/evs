package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;
import org.imec.ivlab.core.model.hub.Hub;


public class HubConverter implements IStringConverter<Hub>
{
    //@Override
    public Hub convert(String hub) {

        return Hub.valueOf(hub);

    }

}