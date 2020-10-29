package org.imec.ivlab.datagenerator.uploader.dateshift;

import com.beust.jcommander.IStringConverter;


public class ShiftActionConverter implements IStringConverter<ShiftAction>
{
    //@Override
    public ShiftAction convert(String shiftActionString) {

        ShiftAction shiftAction = ShiftAction.fromValue(shiftActionString);
        return shiftAction;

    }

}