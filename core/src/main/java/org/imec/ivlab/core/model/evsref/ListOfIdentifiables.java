package org.imec.ivlab.core.model.evsref;

import java.io.Serializable;
import java.util.List;

public interface ListOfIdentifiables extends Serializable {

     <T extends Identifiable & Serializable> List<T> getIdentifiables();

}
