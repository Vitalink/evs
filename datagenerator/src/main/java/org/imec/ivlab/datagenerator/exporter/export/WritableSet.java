package org.imec.ivlab.datagenerator.exporter.export;

import org.imec.ivlab.datagenerator.exporter.writer.SetWriter;

public interface WritableSet<T> {

    SetWriter<T> getWriter();

}
