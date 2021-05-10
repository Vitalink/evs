package org.imec.ivlab.core.model.internal.mapper;

public class MappingResult<T> implements IMappingResult<T> {

    private T mapped;
    private boolean isSuccessful;
    private String unmapped;

    private MappingResult() {
    }

    public static <T> MappingResult<T> successful(T mappedValue) {
        return new MappingResult<T>().asSuccessful(mappedValue);
    }

    public static <T> MappingResult<T> unsuccessful(String unmappedValue) {
        return new MappingResult<T>().asNotSuccessful(unmappedValue);
    }

    private MappingResult<T> asSuccessful(T mappedValue) {
        this.mapped       = mappedValue;
        this.isSuccessful = true;
        return this;
    }

    private MappingResult<T> asNotSuccessful(String unmappedValue) {
        this.unmapped     = unmappedValue;
        this.isSuccessful = false;
        return this;
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public boolean isNotSuccessful() {
        return !isSuccessful;
    }

    @Override
    public T getMappedOrThrow() {
        if (isSuccessful) {
            return mapped;
        } else {
            throw new RuntimeException("The following invalid value could not be mapped: " + unmapped);
        }
    }

    @Override
    public Object getUnmappedOrThrow() {
        if (!isSuccessful) {
            return unmapped;
        } else {
            throw new RuntimeException("Mapping was successful. No unmapped value exists!");
        }
    }

}
