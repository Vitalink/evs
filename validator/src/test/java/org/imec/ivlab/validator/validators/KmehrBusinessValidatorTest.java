package org.imec.ivlab.validator.validators;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import matcher.ValidationMatchers;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.util.TemplateReader;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001a_MultipleOfDaysAllowedFields;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001b_MultipleOfDaysDayNumberValue;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001c_MultipleOfWeeksAllowedFields;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001e_DaynumberOrDayOrWeekdayCount;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001f_DayIfNoFrequency;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001g_MultipleOfMonthsAllowedFields;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001h_MultipleOfMonthsDayLimitInDate;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001i_MultipleOfYearsAllowedFields;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001j_MultipleOfYearsDayLimitInDate;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001k_MultipleOfWeeksDayNumberValue;
import org.imec.ivlab.validator.validators.business.rules.impl.R1001l_DaynumberValue;
import org.imec.ivlab.validator.validators.business.rules.impl.R1002_UniqueTakingTimes;
import org.imec.ivlab.validator.validators.business.rules.impl.R1004_TemporalityPresent;
import org.imec.ivlab.validator.validators.business.rules.impl.R1005_PresentAndIdenticalAdministrationUnits;
import org.imec.ivlab.validator.validators.business.rules.impl.R1006_FreeTextMaxLength;
import org.imec.ivlab.validator.validators.business.rules.impl.R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction;
import org.imec.ivlab.validator.validators.business.rules.impl.R1008_DateTypeForBeginmomentAndEndmoment;
import org.imec.ivlab.validator.validators.business.rules.impl.R1009_HourlyFrequencyInCombinationWithRegiment;
import org.imec.ivlab.validator.validators.business.rules.impl.R1010_NoDeliveredname;
import org.imec.ivlab.validator.validators.business.rules.impl.R1011_TreatmentSuspensionEnddateVersusType;
import org.imec.ivlab.validator.validators.business.rules.impl.R1012_QuantityValueLimited;
import org.imec.ivlab.validator.validators.business.rules.impl.R1013_MaximumNumberOfTreatmentSuspensions;
import org.imec.ivlab.validator.validators.business.rules.impl.R1014_NoPartialDates;
import org.imec.ivlab.validator.validators.business.rules.impl.R1015_AllowedValuesCdDayperiod;
import org.imec.ivlab.validator.validators.business.rules.impl.R1016_AllowedValuesCdPeriodicity;
import org.imec.ivlab.validator.validators.business.rules.impl.R1017_AllowedValuesCdTemporality;
import org.imec.ivlab.validator.validators.business.rules.impl.R1018_AllowedValuesCdDrugroute;
import org.imec.ivlab.validator.validators.business.rules.impl.R1019_UniqueTransactionIDs;
import org.imec.ivlab.validator.validators.business.rules.impl.R1020_EnddateCombinedWithDuration;
import org.imec.ivlab.validator.validators.business.rules.impl.R1021_DurationTimeUnitAllowedCodes;
import org.imec.ivlab.validator.validators.business.rules.impl.R1022_DurationValueNonZeroNaturalNumber;
import org.imec.ivlab.validator.validators.business.rules.impl.R1023_QuantityTrailingZeroes;
import org.imec.ivlab.validator.validators.business.rules.impl.R1024_VitalinkSupportedAdministrationUnits;
import org.imec.ivlab.validator.validators.business.rules.impl.R1025_OnlyExpectedKmehrTables;
import org.imec.ivlab.validator.validators.business.rules.impl.R1026_OnlyExpectedKmehrTableVersions;
import org.imec.ivlab.validator.validators.business.rules.impl.R1027_PosologyOrRegimen;
import org.imec.ivlab.validator.validators.business.rules.impl.R1028_QuantityNonZeroPositiveNumber;
import org.imec.ivlab.validator.validators.business.rules.impl.R1029_OnlyExpectedCodeForKmehrTableVersions;
import org.imec.ivlab.validator.validators.business.rules.impl.R1030_CnkValidControlNumber;
import org.imec.ivlab.validator.validators.business.rules.impl.R1031_CnkNotInLocalRange;
import org.imec.ivlab.validator.validators.business.rules.impl.R1032_IscompleteIsTrue;
import org.imec.ivlab.validator.validators.business.rules.impl.R1033_BeginmomentVsEndmoment;
import org.imec.ivlab.validator.validators.business.rules.impl.R1034_IsvalidatedIsTrue;
import org.imec.ivlab.validator.validators.business.rules.impl.R1100_OneFolder;
import org.imec.ivlab.validator.validators.model.ValidationResult;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@org.testng.annotations.Test
public class KmehrBusinessValidatorTest {

    private KmehrValidator validator = new KmehrValidator();

    private ValidationResult validateTemplate(String templateFileName) {

        String kmehrmessageString = TemplateReader.read(templateFileName);
        Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehrmessageString);

        validator.skipDisabledRules(true);
        ValidationResult validationResult = validator.validate(kmehrmessage, kmehrmessageString);
        return  validationResult;
    }

    @BeforeTest
    public void init() {
    }

    /*
     * Some tests are quite flaky, they will be skipped for now
     * Since we don't need validation to 
     * 
     * If we ever want to debug them, they are in the group "brokenValidators"
     * @Test(enabled = false, groups = {"brokenValidators"})
     */

    @Test
    public void validateXsdValidKmehr() {
        ValidationResult validationResult = validateTemplate("03_valid.xml");
        assertThat(validationResult).is(ValidationMatchers.passes());
    }


    @Test
    public void validateTwoFolders() {
        ValidationResult validationResult = validateTemplate("05_two-folders.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1100_OneFolder.class));
    }

    @Test
    public void validateFrequency5DaysAndDaynumber6() {
        ValidationResult validationResult = validateTemplate("06_frequency-5-days-and-daynumber-6.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001b_MultipleOfDaysDayNumberValue.class));
    }
    
    @Test
    public void validateFrequency5DaysAndDaynumber5() {
        ValidationResult validationResult = validateTemplate("07_frequency-5-days-and-daynumber-5.xml");
        assertThat(validationResult).is(ValidationMatchers.passes());
    }
    
    @Test
    public void validateFrequency10DaysAndWeekday() {
        ValidationResult validationResult = validateTemplate("08_frequency-10-days-and-weekday.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001a_MultipleOfDaysAllowedFields.class));
    }
    
    @Test
    public void validateFrequency10DaysAndDate() {
        ValidationResult validationResult = validateTemplate("09_frequency-10-days-and-date.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001a_MultipleOfDaysAllowedFields.class));
    }
    
    @Test
    public void validateFrequency3weeksAndDate() {
        ValidationResult validationResult = validateTemplate("10_frequency-3-weeks-and-date.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001c_MultipleOfWeeksAllowedFields.class));
    }
    
    @Test
    public void validateFrequency6Weeks3RegimenAndOnly2Weekdays() {
        ValidationResult validationResult = validateTemplate("12_frequency-6-weeks-3-regimen-and-only-2-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    
    @Test
    public void validateFrequency6Weeks3RegimenAnd3Weekdays() {
        ValidationResult validationResult = validateTemplate("13_frequency-6-weeks-3-regimen-and-3-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    
    @Test
    public void validateFrequency6Weeks1RegimenAndWithoutWeekdays() {
        ValidationResult validationResult = validateTemplate("14_frequency-6-weeks-1-regimen-and-without-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    
    @Test
    public void validateFrequency6Weeks3RegimenAndWithoutWeekdays() {
        ValidationResult validationResult = validateTemplate("14b_frequency-6-weeks-3-regimen-and-without-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    
    @Test
    public void validateFrequency6Weeks3RegimenDaynumberAndWeekdaysMixed() {
        ValidationResult validationResult = validateTemplate("14c_frequency-6-weeks-3-regimen-with-daynumbers-and-weekdays-mixed.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    
    @Test
    public void validateNoFrequency1RegimenWithDaynumbers() {
        ValidationResult validationResult = validateTemplate("15_frequency-no-frequency-1-regimen-with-daynumbers.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }
    
    @Test
    public void validateNoFrequency1RegimenWithWeekdays() {
        ValidationResult validationResult = validateTemplate("15b_frequency-no-frequency-1-regimen-with-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }
    
    @Test
    public void validateNoFrequency1RegimenWithoutDaynumbersDatesOrWeekdays() {
        ValidationResult validationResult = validateTemplate("16_frequency-no-frequency-1-regimen-without-daynumbers-dates-or-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }
    
    @Test
    public void validateFrequency9MonthsAndDayNumber() {
        ValidationResult validationResult = validateTemplate("17_frequency-9-months-and-daynumber.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001g_MultipleOfMonthsAllowedFields.class));
    }
    
    @Test
    public void validateFrequency1YearAndWeekday() {
        ValidationResult validationResult = validateTemplate("18_frequency-1-year-and-weekday.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001i_MultipleOfYearsAllowedFields.class));
    }
    
    @Test
    public void validateFrequency9MonthsAndDate() {
        ValidationResult validationResult = validateTemplate("19_frequency-9-months-and-date.xml");
        assertThat(validationResult).is(ValidationMatchers.passes());
    }
    
    @Test
    public void validateFrequency9MonthsAndDateDayvalue29() {
        ValidationResult validationResult = validateTemplate("20_frequency-9-months-and-date-dayvalue-29.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001h_MultipleOfMonthsDayLimitInDate.class));
    }
    
    @Test
    public void validateFrequency2YearsAndDateDayValue28February() {
        ValidationResult validationResult = validateTemplate("21_frequency-2-years-and-date-dayvalue-february-28.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001j_MultipleOfYearsDayLimitInDate.class));
    }
    
    @Test
    public void validateFrequency2YearsAndDateDayValue29February() {
        ValidationResult validationResult = validateTemplate("22_frequency-2-years-and-date-dayvalue-february-29.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001j_MultipleOfYearsDayLimitInDate.class));
    }
    
    @Test
    public void validateTakingTimesUnique() {
        ValidationResult validationResult = validateTemplate("23_taking-times-different.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }
    
    @Test
    public void validateTakingTimesIdenticalDayperiodDifferentDaynumbers() {
        ValidationResult validationResult = validateTemplate("24_taking-times-identical-dayperiod-different-daynumber.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }
    
    @Test
    public void validateTakingTimesIdenticalDayperiodIdenticalDaynumbers() {
        ValidationResult validationResult = validateTemplate("24b_taking-times-identical-dayperiod-identical-daynumber.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1002_UniqueTakingTimes.class));
    }
    
    @Test
    public void validateTakingTimesIdenticalTimeDifferentWeekdays() {
        ValidationResult validationResult = validateTemplate("25_taking-times-identical-time-different-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }
    
    @Test
    public void validateTakingTimesIdenticalTimeIdenticalWeekdays() {
        ValidationResult validationResult = validateTemplate("25b_taking-times-identical-time-identical-weekdays.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1002_UniqueTakingTimes.class));
    }
    
    @Test
    public void validateInvalidXmlStructure() {
        ValidationResult validationResult = validateTemplate("26_not-respecting-xsd-mandatory-field-missing.xml");
        assertThat(validationResult).is(ValidationMatchers.failsWithMessage("cvc-complex-type.2.4.a:"));
    }
    
    @Test
    public void validateTemporalityValid() {
        ValidationResult validationResult = validateTemplate("29_temporality-valid.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1004_TemporalityPresent.class));
    }
    
    @Test
    public void validateTemporalityMissing() {
        ValidationResult validationResult = validateTemplate("30_temporality-missing.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1004_TemporalityPresent.class));
    }
    
    @Test
    public void validateAdministrationunitIdentical() {
        ValidationResult validationResult = validateTemplate("32_quantity-unit-identical.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1005_PresentAndIdenticalAdministrationUnits.class));
    }
    
    @Test
    public void validateAdministrationunitMissing() {
        ValidationResult validationResult = validateTemplate("33_quantity-unit-missing.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1005_PresentAndIdenticalAdministrationUnits.class));
    }
    
    @Test
    public void validateAdministrationunitDifferent() {
        ValidationResult validationResult = validateTemplate("34_quantity-unit-different.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1005_PresentAndIdenticalAdministrationUnits.class));
    }
    
    @Test
    public void validateAdministrationunitNoRegimen() {
        ValidationResult validationResult = validateTemplate("34b_quantity-unit-no-regimen.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1005_PresentAndIdenticalAdministrationUnits.class));
    }
    
    @Test
    public void validateFreeTextTooLong() {
        ValidationResult validationResult = validateTemplate("35_instruction-for-patient-too-long.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1006_FreeTextMaxLength.class));
    }
    
    @Test
    public void validateFreeTextMaxLength() {
        ValidationResult validationResult = validateTemplate("36_instruction-for-patient-max-length.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1006_FreeTextMaxLength.class));
    }
    
    @Test
    public void validateSuspensionMedicationSameAsInMedicationTransaction() {
        ValidationResult validationResult = validateTemplate("37_suspension-medicaton-equal-to-medication-in-medication-transaction.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction.class));
    }
    
    @Test
    public void validateSuspensionMedicationDifferentFromMedicationTransaction() {
        ValidationResult validationResult = validateTemplate("38_suspension-medicaton-different-from-medication-in-medication-transaction.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction.class));
    }
    
    @Test
    public void validateBeginmomentMedicationTypeYear() {
        ValidationResult validationResult = validateTemplate("39_beginmoment-medication-type-year.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }
    
    @Test
    public void validateEndmomentSuspensionTypeYearMonth() {
        ValidationResult validationResult = validateTemplate("40_endmoment-suspension-type-yearmonth.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }
    
    @Test
    public void validateBeginAndEndmomentsTypesDate() {
        ValidationResult validationResult = validateTemplate("41_beginmoments-and-endmoments-type-date.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }
    
    @Test
    public void validateFrequency1HourAndRegimen() {
        ValidationResult validationResult = validateTemplate("42_frequency-1-hour-and-regimen.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1009_HourlyFrequencyInCombinationWithRegiment.class));
    }
    
    @Test
    public void validateDateTypeOfTypeYear() {
        ValidationResult validationResult = validateTemplate("43_birthdate-of-type-year.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1014_NoPartialDates.class));
    }
    
    @Test
    public void validateDateTypeOfTypeDate() {
        ValidationResult validationResult = validateTemplate("44_birthdate-of-type-date.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1014_NoPartialDates.class));
    }
    
    @Test
    public void validateDateTypeOfTypeYearmonth() {
        ValidationResult validationResult = validateTemplate("45_birthdate-of-type-yearmonth.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1014_NoPartialDates.class));
    }
    
    @Test
    public void validateNoDeliveredCnkOrName() {
        ValidationResult validationResult = validateTemplate("46_no-delivered-cnk-or-name.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1010_NoDeliveredname.class));
    }
    
    @Test
    public void validateDeliveredCnkDifferent() {
        ValidationResult validationResult = validateTemplate("47_delivered-cnk-different.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1010_NoDeliveredname.class));
    }
    
    @Test
    public void validateDeliveredNameDifferent() {
        ValidationResult validationResult = validateTemplate("48_delivered-name-equal-no-cd.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1010_NoDeliveredname.class));
    }
    
    @Test
    public void validateDeliveredCnkAndNameEqual() {
        ValidationResult validationResult = validateTemplate("49_delivered-cnkd-and-name-equal.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1010_NoDeliveredname.class));
    }
    
    @Test
    public void validateSuspensionStoppedAndEndDate() {
        ValidationResult validationResult = validateTemplate("50_suspension-stopped-and-enddate.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionStoppedAndNoEndDate() {
        ValidationResult validationResult = validateTemplate("51_suspension-stopped-and-no-enddate.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionSuspendedAndEndDate() {
        ValidationResult validationResult = validateTemplate("52_suspension-suspended-and-enddate.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionSuspendedAndNoEndDate() {
        ValidationResult validationResult = validateTemplate("53_suspension-suspended-and-no-enddate.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionCountHigherThanMaximum() {
        ValidationResult validationResult = validateTemplate("54_suspension-count-higher-than-maximum.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1013_MaximumNumberOfTreatmentSuspensions.class));
    }
    
    @Test
    public void validateDayperiodInvalid() {
        ValidationResult validationResult = validateTemplate("55_dayperiod-unsupported-value.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1015_AllowedValuesCdDayperiod.class));
    }
    
    @Test
    public void validateFrequencyInvalid() {
        ValidationResult validationResult = validateTemplate("56_frequency-unsupported-value.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1016_AllowedValuesCdPeriodicity.class));
    }
    
    @Test
    public void validateAdministrationUnitNonVitalink() {
        ValidationResult validationResult = validateTemplate("57_administrationunit-non-vitalink.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1024_VitalinkSupportedAdministrationUnits.class));
    }
    
    @Test
    public void validateTransactionIdNotUnique() {
        ValidationResult validationResult = validateTemplate("59_transactionID-not-unique.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }
    
    @Test
    public void validateTransactionIdBlank() {
        ValidationResult validationResult = validateTemplate("60_transactionID-blank.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }
    
    @Test
    public void validateTransactionIdTwoEntries() {
        ValidationResult validationResult = validateTemplate("61_transactionID-two-entries.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }
    
    @Test
    public void validateSuspensionStoppedAndDuration() {
        ValidationResult validationResult = validateTemplate("62_suspension-stopped-and-duration.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionStoppedAndNoDuration() {
        ValidationResult validationResult = validateTemplate("63_suspension-stopped-and-no-duration.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionSuspendedAndDuration() {
        ValidationResult validationResult = validateTemplate("64_suspension-suspended-and-duration.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateSuspensionSuspendedAndNoDuration() {
        ValidationResult validationResult = validateTemplate("65_suspension-suspended-and-no-duration.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }
    
    @Test
    public void validateEndmomentAndDurationForMedicationTransaction() {
        ValidationResult validationResult = validateTemplate("66_medicationtransaction-duration-and-endmoment.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1020_EnddateCombinedWithDuration.class));
    }
    
    @Test
    public void validateEndmomentAndDurationForSuspensionTransaction() {
        ValidationResult validationResult = validateTemplate("67_suspensiontransaction-duration-and-endmoment.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1020_EnddateCombinedWithDuration.class));
    }
    
    @Test
    public void validateDurationValueZero() {
        ValidationResult validationResult = validateTemplate("68_durationvalue-zero.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }
    
    @Test
    public void validateDurationValueTrailingZeroes() {
        ValidationResult validationResult = validateTemplate("69_durationvalue-fractionaldigits.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }
    
    @Test
    public void validateDurationTimeUnitInvalidKmehrCode() {
        ValidationResult validationResult = validateTemplate("70_durationtimeunit-unexisting-kmehrcode.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1021_DurationTimeUnitAllowedCodes.class));
    }
    
    @Test
    public void validateDurationTimeUnitUnsupportedCode() {
        ValidationResult validationResult = validateTemplate("71_durationtimeunit-notallowed-code.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1021_DurationTimeUnitAllowedCodes.class));
    }
    
    @Test
    public void validateTemporalityInvalid() {
        ValidationResult validationResult = validateTemplate("72_temporality-unsupported-value.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1017_AllowedValuesCdTemporality.class));
    }
    
    @Test
    public void validateRouteInvalid() {
        ValidationResult validationResult = validateTemplate("73_route-unsupported-value.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1018_AllowedValuesCdDrugroute.class));
    }
    
    @Test
    public void validateNaturalNumber5IntegerDigits() {
        ValidationResult validationResult = validateTemplate("74_naturalnumber-5-integerdigits.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }
    
    @Test
    public void validateRationalNumber2IntegerDigits() {
        ValidationResult validationResult = validateTemplate("75_rationalnumber-2-integerdigits.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }
    
    @Test
    public void validateRationalNumber3FractionalDigits() {
        ValidationResult validationResult = validateTemplate("76_rationalnumber-3-fractionaldigits.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }
    
    @Test
    public void validateMixOfAllowedNumbers() {
        ValidationResult validationResult = validateTemplate("77_mix-of-allowed-numbers.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1012_QuantityValueLimited.class));
    }
    
    @Test
    public void validateTrailingZeroes() {
        ValidationResult validationResult = validateTemplate("78_trailing-zeroes.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1023_QuantityTrailingZeroes.class));
    }
    
    @Test
    public void validateMissingPosologyAndRegimen() {
        ValidationResult validationResult = validateTemplate("79_missing-posology-and-regimen.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1027_PosologyOrRegimen.class));
    }
    
    @Test
    public void validateQuantityNegativeNumber() {
        ValidationResult validationResult = validateTemplate("80_quantity-negative-number.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1028_QuantityNonZeroPositiveNumber.class));
    }
    
    @Test
    public void validateQuantityZero() {
        ValidationResult validationResult = validateTemplate("81_quantity-zero.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1028_QuantityNonZeroPositiveNumber.class));
    }
    
    @Test
    public void validateDurationValueNegativeDigits() {
        ValidationResult validationResult = validateTemplate("82_durationvalue-negativedigits.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }
    
    @Test
    public void validateUnexpectedTable() {
        ValidationResult validationResult = validateTemplate("83_unexpected-table.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1025_OnlyExpectedKmehrTables.class));
    }
    
    @Test
    public void validateUnexpectedTableVersion() {
        ValidationResult validationResult = validateTemplate("84_unexpected-table-version.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1026_OnlyExpectedKmehrTableVersions.class));
    }
    
    @Test
    public void validateUnexpectedCodeForTableVersion() {
        ValidationResult validationResult = validateTemplate("85_unexpected-code-for-table-version.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1029_OnlyExpectedCodeForKmehrTableVersions.class));
    }
    
    @Test
    public void validateFrequency3WeeksAndDaynumber21() {
        ValidationResult validationResult = validateTemplate("86_frequency-3-weeks-and-daynumber-21.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency3WeeksAndDaynumber22() {
        ValidationResult validationResult = validateTemplate("87_frequency-3-weeks-and-daynumber-22.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber7() {
        ValidationResult validationResult = validateTemplate("88_frequency-1-week-and-daynumber-7.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber1() {
        ValidationResult validationResult = validateTemplate("89_frequency-1-week-and-daynumber-1.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber0() {
        ValidationResult validationResult = validateTemplate("90_frequency-1-week-and-daynumber-0.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1001l_DaynumberValue.class));
    }

    @Test
    public void validateIsCompleteFalse() {
        ValidationResult validationResult = validateTemplate("91_iscomplete-false.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1032_IscompleteIsTrue.class));
    }

    @Test
    public void validateCnkIsInLocalRange() {
        ValidationResult validationResult = validateTemplate("92_cnk-reserved-for-local-usage.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1031_CnkNotInLocalRange.class));
    }

    @Test
    public void validateCnkWrongControlNumber() {
        ValidationResult validationResult = validateTemplate("93_cnk-wrong-control-number.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1030_CnkValidControlNumber.class));
    }

    @Test
    public void validateInnclusterWrongControlNumber() {
        ValidationResult validationResult = validateTemplate("94_inncluster-wrong-control-number.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1030_CnkValidControlNumber.class));
    }

    @Test
    public void validateEndmomentBeforeBeginmoment() {
        ValidationResult validationResult = validateTemplate("95_endmoment-before-beginmoment.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1033_BeginmomentVsEndmoment.class));
    }

    @Test
    public void validateEndmomentEqualsBeginmoment() {
        ValidationResult validationResult = validateTemplate("96_endmoment-equals-beginmoment.xml");
        assertThat(validationResult).is(ValidationMatchers.passes(R1033_BeginmomentVsEndmoment.class));
    }

    @Test
    public void validateIsValidatedFalse() {
        ValidationResult validationResult = validateTemplate("97_isvalidated-false.xml");
        assertThat(validationResult).is(ValidationMatchers.failsForRule(R1034_IsvalidatedIsTrue.class));
    }
}