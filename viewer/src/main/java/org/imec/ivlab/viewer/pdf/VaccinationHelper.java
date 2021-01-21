package org.imec.ivlab.viewer.pdf;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType.Substanceproduct;
import be.fgov.ehealth.standards.kmehr.schema.v1.MedicinalProductType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.imec.ivlab.core.model.internal.parser.vaccination.VaccinationItem;

public class VaccinationHelper {

  public static List<String> getMedicinalDeliveredNames(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getMedicinalProductTypes()
        .stream()
        .filter(Objects::nonNull)
        .map(MedicinalProductType::getDeliveredname)
        .collect(Collectors.toList());
  }

  public static List<CDDRUGCNK> getMedicinalDeliveredCnks(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getMedicinalProductTypes()
        .stream()
        .filter(Objects::nonNull)
        .flatMap(medicinalProductType -> medicinalProductType
            .getDeliveredcds()
            .stream())
        .collect(Collectors.toList());
  }

  public static List<String> getMedicinalIntendedNames(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getMedicinalProductTypes()
        .stream()
        .filter(Objects::nonNull)
        .map(MedicinalProductType::getIntendedname)
        .collect(Collectors.toList());
  }

  public static List<CDDRUGCNK> getMedicinalIntendedCnks(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getMedicinalProductTypes()
        .stream()
        .filter(Objects::nonNull)
        .flatMap(medicinalProductType -> medicinalProductType
            .getIntendedcds()
            .stream())
        .collect(Collectors.toList());
  }

  public static List<String> getSubstanceDeliveredNames(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getSubstanceproducts()
        .stream()
        .filter(Objects::nonNull)
        .map(Substanceproduct::getDeliveredname)
        .collect(Collectors.toList());
  }

  public static List<CDDRUGCNK> getSubstanceDeliveredCnks(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getSubstanceproducts()
        .stream()
        .filter(Objects::nonNull)
        .map(Substanceproduct::getDeliveredcd)
        .collect(Collectors.toList());
  }

  public static List<String> getSubstanceIntendedNames(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getSubstanceproducts()
        .stream()
        .filter(Objects::nonNull)
        .map(Substanceproduct::getIntendedname)
        .collect(Collectors.toList());
  }

  public static List<CDINNCLUSTER> getSubstanceIntendedCnks(VaccinationItem vaccinationItem) {
    return vaccinationItem
        .getSubstanceproducts()
        .stream()
        .filter(Objects::nonNull)
        .map(Substanceproduct::getIntendedcd)
        .collect(Collectors.toList());
  }

}
