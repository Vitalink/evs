package org.imec.ivlab.core.vaccination;

import static java.lang.String.valueOf;
import static org.imec.ivlab.core.vaccination.VaccinationMappingReader.RequiredExcelColumn.ATC_CODE;
import static org.imec.ivlab.core.vaccination.VaccinationMappingReader.RequiredExcelColumn.CNK_CODE;
import static org.imec.ivlab.core.vaccination.VaccinationMappingReader.RequiredExcelColumn.COMPANY;
import static org.imec.ivlab.core.vaccination.VaccinationMappingReader.RequiredExcelColumn.PROTECTS_AGAINST;
import static org.imec.ivlab.core.vaccination.VaccinationMappingReader.RequiredExcelColumn.VACCINNET_CODE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.imec.ivlab.core.util.IOUtils;

@Log4j2
@Getter
public class VaccinationMappingReader {

  public static final String VACCINATION_MAPPING_FOLDER = "/vaccination";
  public static final String VACCINATION_VACCINCODES_EXCEL_LOCATION = "/vaccination/vaccincodes.xlsx";
  private static VaccinationMappingReader instance;
  private List<VaccinationMapping> vaccinationMappings;

  public static VaccinationMappingReader getInstance() {
    if (instance == null) {
      instance = new VaccinationMappingReader();
      instance.readMappings();
    }
    return instance;
  }

  public enum RequiredExcelColumn {

    CNK_CODE(1), COMPANY(3), VACCINNET_CODE(4), PROTECTS_AGAINST(5), ATC_CODE(7);

    int positionOneBased;

    RequiredExcelColumn(int positionOneBased) {
      this.positionOneBased = positionOneBased;
    }

    public int getPositionOneBased() {
      return positionOneBased;
    }
  }

  public void readMappings() {

    Workbook workbook = readWorkbook(VACCINATION_VACCINCODES_EXCEL_LOCATION);

    Sheet sheet = workbook.getSheetAt(0);

    Map<Integer, List<String>> data = readSheet(sheet);
    vaccinationMappings = toVaccinationMappings(data);

  }

  private List<VaccinationMapping> toVaccinationMappings(Map<Integer, List<String>> data) {
    return data
        .values()
        .stream()
        .map(this::toVaccinationMapping)
        .collect(Collectors.toList());
  }

  private VaccinationMapping toVaccinationMapping(List<String> data) {
    VaccinationMapping vaccinationMapping = new VaccinationMapping();
    vaccinationMapping.setCnk(mapData(data, CNK_CODE));
    vaccinationMapping.setCompany(mapData(data, COMPANY));
    vaccinationMapping.setVaccinnetCode(mapData(data, VACCINNET_CODE));
    vaccinationMapping.setProtectsAgainst(mapData(data, PROTECTS_AGAINST));
    vaccinationMapping.setAtc(mapData(data, ATC_CODE));
    return vaccinationMapping;
  }

  private String mapData(List<String> data, RequiredExcelColumn column) {
    if (data.size() < column.getPositionOneBased()) {
      return null;
    }
    return data.get(column.getPositionOneBased() - 1);
  }

  private Workbook readWorkbook(String location) {
    Workbook workbook = null;
    try {
      InputStream resourceAsStream = IOUtils.getResourceAsStream(location);
      workbook = new XSSFWorkbook(resourceAsStream);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read vaccination mapping file: ", e);
    }
    return workbook;
  }

  private Map<Integer, List<String>> readSheet(Sheet sheet) {
    Map<Integer, List<String>> data = new HashMap<>();
    int i = 0;
    for (Row row : sheet) {
      data.put(i, new ArrayList<>());
      for (int cn = 0; cn < row.getLastCellNum(); cn++) {
        Cell cell = row.getCell(cn, MissingCellPolicy.RETURN_NULL_AND_BLANK);
        data
            .get(i)
            .add(readCell(cell));
      }
      i++;
    }
    return data;
  }

  private String readCell(Cell cell) {
    if (cell == null) {
      return "";
    }
    switch (cell.getCellTypeEnum()) {
      case STRING:
        return valueOf(cell.getStringCellValue());
      case NUMERIC:
        return valueOf((int) cell.getNumericCellValue());
      case BOOLEAN:
        return valueOf(cell.getBooleanCellValue());
      case FORMULA:
        return valueOf(cell.getCellFormula());
      default:
        return "";
    }
  }

}
