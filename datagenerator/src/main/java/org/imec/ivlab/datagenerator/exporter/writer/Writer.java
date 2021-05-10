package org.imec.ivlab.datagenerator.exporter.writer;

import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;

import java.io.File;
import java.util.List;

public interface Writer<T> {

    List<ExportResult<T>> write(Patient patient, List<T> content, List<Latestupdate> latestupdates, File outputDirectory, String filename, String extension);

}
