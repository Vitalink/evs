package org.imec.ivlab.core.vaccination;

import org.apache.commons.lang3.StringUtils;

public class VaccinationEnricher {

  public static String getProtectsAgainstByCnk(String cnk) {
    VaccinationMappingReader vaccinationMappingReader = VaccinationMappingReader.getInstance();
    return vaccinationMappingReader.getVaccinationMappings()
                                   .stream()
                                   .filter(vaccinationMapping -> matchesCnk(cnk, vaccinationMapping))
                                   .map(VaccinationMapping::getProtectsAgainst)
                                   .findFirst()
                                   .orElse(null);
  }

  public static String getProtectsAgainstByAtc(String atc) {
    VaccinationMappingReader vaccinationMappingReader = VaccinationMappingReader.getInstance();
    return vaccinationMappingReader.getVaccinationMappings()
                                   .stream()
                                   .filter(vaccinationMapping -> matchesAtc(atc, vaccinationMapping))
                                   .map(VaccinationMapping::getProtectsAgainst)
                                   .findFirst()
                                   .orElse(null);
  }

  public static String getProtectsAgainstByVaccinnetCode(String vaccinnetCode) {
    VaccinationMappingReader vaccinationMappingReader = VaccinationMappingReader.getInstance();
    return vaccinationMappingReader.getVaccinationMappings()
                                   .stream()
                                   .filter(vaccinationMapping -> matchesVaccinnetCode(vaccinnetCode, vaccinationMapping))
                                   .map(VaccinationMapping::getProtectsAgainst)
                                   .findFirst()
                                   .orElse(null);
  }

  private static boolean matchesCnk(String cnk, VaccinationMapping vaccinationMapping) {
    return vaccinationMapping != null && StringUtils.trimToNull(cnk) != null && StringUtils.trimToNull(vaccinationMapping.getCnk()) != null && StringUtils.equalsIgnoreCase(cnk, vaccinationMapping.getCnk());
  }

  private static boolean matchesAtc(String atc, VaccinationMapping vaccinationMapping) {
    return vaccinationMapping != null && StringUtils.trimToNull(atc) != null && StringUtils.trimToNull(vaccinationMapping.getAtc()) != null && StringUtils.equalsIgnoreCase(atc, vaccinationMapping.getAtc());
  }

  private static boolean matchesVaccinnetCode(String vaccinnetCode, VaccinationMapping vaccinationMapping) {
    return vaccinationMapping != null && StringUtils.trimToNull(vaccinnetCode) != null && StringUtils.trimToNull(vaccinationMapping.getVaccinnetCode()) != null && StringUtils.equalsIgnoreCase(vaccinnetCode, vaccinationMapping.getVaccinnetCode());
  }

}
