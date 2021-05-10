package org.imec.ivlab.datagenerator.exporter.export;

import org.imec.ivlab.datagenerator.exporter.writer.Writer;

public interface Writable<T> {

    Writer<T> getWriter();

}
