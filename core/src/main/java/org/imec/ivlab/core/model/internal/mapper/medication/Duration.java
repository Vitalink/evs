package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;
import java.math.BigInteger;
import org.imec.ivlab.core.model.internal.mapper.MappingResult;

public class Duration implements Serializable {

    private MappingResult<BigInteger> value;
    private MappingResult<TimeUnit> unit;

    public MappingResult<BigInteger> getValue() {
        return value;
    }

    public void setValue(MappingResult<BigInteger> value) {
        this.value = value;
    }

    public MappingResult<TimeUnit> getUnit() {
        return unit;
    }

    public void setUnit(MappingResult<TimeUnit> unit) {
        this.unit = unit;
    }

}
