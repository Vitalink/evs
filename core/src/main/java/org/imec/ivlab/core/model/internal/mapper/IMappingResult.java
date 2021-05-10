package org.imec.ivlab.core.model.internal.mapper;

public interface IMappingResult<T> {

  boolean isSuccessful();

  boolean isNotSuccessful();

  T getMappedOrThrow();

  Object getUnmappedOrThrow();

}
