package org.imec.ivlab.validator.validators;

import static org.hamcrest.MatcherAssert.assertThat;

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
     */

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateXsdValidKmehr() {
        assertThat(validateTemplate("03_valid.xml"), ValidationMatchers.passes());
    }


    @Test
    public void validateTwoFolders() {
        assertThat(validateTemplate("05_two-folders.xml"), ValidationMatchers.failsForRule(R1100_OneFolder.class));
    }

    @Test
    public void validateFrequency5DaysAndDaynumber6() {
        assertThat(validateTemplate("06_frequency-5-days-and-daynumber-6.xml"), ValidationMatchers.failsForRule(R1001b_MultipleOfDaysDayNumberValue.class));
    }

    @Test
    public void validateFrequency5DaysAndDaynumber5() {
        assertThat(validateTemplate("07_frequency-5-days-and-daynumber-5.xml"), ValidationMatchers.passes());
    }

    @Test
    public void validateFrequency10DaysAndWeekday() {
        assertThat(validateTemplate("08_frequency-10-days-and-weekday.xml"), ValidationMatchers.failsForRule(R1001a_MultipleOfDaysAllowedFields.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateFrequency10DaysAndDate() {
        assertThat(validateTemplate("09_frequency-10-days-and-date.xml"), ValidationMatchers.failsForRule(R1001a_MultipleOfDaysAllowedFields.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateFrequency3weeksAndDate() {
        assertThat(validateTemplate("10_frequency-3-weeks-and-date.xml"), ValidationMatchers.failsForRule(R1001c_MultipleOfWeeksAllowedFields.class));
    }

    @Test
    public void validateFrequency6Weeks3RegimenAndOnly2Weekdays() {
        assertThat(validateTemplate("12_frequency-6-weeks-3-regimen-and-only-2-weekdays.xml"), ValidationMatchers.failsForRule(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }

    @Test
    public void validateFrequency6Weeks3RegimenAnd3Weekdays() {
        assertThat(validateTemplate("13_frequency-6-weeks-3-regimen-and-3-weekdays.xml"), ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }

    @Test
    public void validateFrequency6Weeks1RegimenAndWithoutWeekdays() {
        assertThat(validateTemplate("14_frequency-6-weeks-1-regimen-and-without-weekdays.xml"), ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }

    @Test
    public void validateFrequency6Weeks3RegimenAndWithoutWeekdays() {
        assertThat(validateTemplate("14b_frequency-6-weeks-3-regimen-and-without-weekdays.xml"), ValidationMatchers.passes(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }
    @Test
    public void validateFrequency6Weeks3RegimenDaynumberAndWeekdaysMixed() {
        assertThat(validateTemplate("14c_frequency-6-weeks-3-regimen-with-daynumbers-and-weekdays-mixed.xml"), ValidationMatchers.failsForRule(R1001e_DaynumberOrDayOrWeekdayCount.class));
    }

    @Test
    public void validateNoFrequency1RegimenWithDaynumbers() {
        assertThat(validateTemplate("15_frequency-no-frequency-1-regimen-with-daynumbers.xml"), ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }

    @Test
    public void validateNoFrequency1RegimenWithWeekdays() {
        assertThat(validateTemplate("15b_frequency-no-frequency-1-regimen-with-weekdays.xml"), ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }

    @Test
    public void validateNoFrequency1RegimenWithoutDaynumbersDatesOrWeekdays() {
        assertThat(validateTemplate("16_frequency-no-frequency-1-regimen-without-daynumbers-dates-or-weekdays.xml"), ValidationMatchers.failsForRule(R1001f_DayIfNoFrequency.class));
    }

    @Test
    public void validateFrequency9MonthsAndDayNumber() {
        assertThat(validateTemplate("17_frequency-9-months-and-daynumber.xml"), ValidationMatchers.failsForRule(R1001g_MultipleOfMonthsAllowedFields.class));
    }

    @Test
    public void validateFrequency1YearAndWeekday() {
        assertThat(validateTemplate("18_frequency-1-year-and-weekday.xml"), ValidationMatchers.failsForRule(R1001i_MultipleOfYearsAllowedFields.class));
    }

    @Test
    public void validateFrequency9MonthsAndDate() {
        assertThat(validateTemplate("19_frequency-9-months-and-date.xml"), ValidationMatchers.passes());
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateFrequency9MonthsAndDateDayvalue29() {
        assertThat(validateTemplate("20_frequency-9-months-and-date-dayvalue-29.xml"), ValidationMatchers.failsForRule(R1001h_MultipleOfMonthsDayLimitInDate.class));
    }

    @Test
    public void validateFrequency2YearsAndDateDayValue28February() {
        assertThat(validateTemplate("21_frequency-2-years-and-date-dayvalue-february-28.xml"), ValidationMatchers.passes(R1001j_MultipleOfYearsDayLimitInDate.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateFrequency2YearsAndDateDayValue29February() {
        assertThat(validateTemplate("22_frequency-2-years-and-date-dayvalue-february-29.xml"), ValidationMatchers.failsForRule(R1001j_MultipleOfYearsDayLimitInDate.class));
    }

    @Test
    public void validateTakingTimesUnique() {
        assertThat(validateTemplate("23_taking-times-different.xml"), ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }

    @Test
    public void validateTakingTimesIdenticalDayperiodDifferentDaynumbers() {
        assertThat(validateTemplate("24_taking-times-identical-dayperiod-different-daynumber.xml"), ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }

    @Test
    public void validateTakingTimesIdenticalDayperiodIdenticalDaynumbers() {
        assertThat(validateTemplate("24b_taking-times-identical-dayperiod-identical-daynumber.xml"), ValidationMatchers.failsForRule(R1002_UniqueTakingTimes.class));
    }

    @Test
    public void validateTakingTimesIdenticalTimeDifferentWeekdays() {
        assertThat(validateTemplate("25_taking-times-identical-time-different-weekdays.xml"), ValidationMatchers.passes(R1002_UniqueTakingTimes.class));
    }


    @Test
    public void validateTakingTimesIdenticalTimeIdenticalWeekdays() {
        assertThat(validateTemplate("25b_taking-times-identical-time-identical-weekdays.xml"), ValidationMatchers.failsForRule(R1002_UniqueTakingTimes.class));
    }


    @Test
    public void validateInvalidXmlStructure() {
        assertThat(validateTemplate("26_not-respecting-xsd-mandatory-field-missing.xml"), ValidationMatchers.failsWithMessage("cvc-complex-type.2.4.a:"));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateTemporalityValid() {
        assertThat(validateTemplate("29_temporality-valid.xml"), ValidationMatchers.passes(R1004_TemporalityPresent.class));
    }

    @Test
    public void validateTemporalityMissing() {
        assertThat(validateTemplate("30_temporality-missing.xml"), ValidationMatchers.failsForRule(R1004_TemporalityPresent.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateAdministrationunitIdentical() {
        assertThat(validateTemplate("32_quantity-unit-identical.xml"), ValidationMatchers.passes(R1005_PresentAndIdenticalAdministrationUnits.class));
    }

    @Test
    public void validateAdministrationunitMissing() {
        assertThat(validateTemplate("33_quantity-unit-missing.xml"), ValidationMatchers.failsForRule(R1005_PresentAndIdenticalAdministrationUnits.class));
    }

    @Test
    public void validateAdministrationunitDifferent() {
        assertThat(validateTemplate("34_quantity-unit-different.xml"), ValidationMatchers.failsForRule(R1005_PresentAndIdenticalAdministrationUnits.class));
    }
    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateAdministrationunitNoRegimen() {
        assertThat(validateTemplate("34b_quantity-unit-no-regimen.xml"), ValidationMatchers.passes(R1005_PresentAndIdenticalAdministrationUnits.class));
    }

    @Test
    public void validateFreeTextTooLong() {
        assertThat(validateTemplate("35_instruction-for-patient-too-long.xml"), ValidationMatchers.failsForRule(R1006_FreeTextMaxLength.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateFreeTextMaxLength() {
        assertThat(validateTemplate("36_instruction-for-patient-max-length.xml"), ValidationMatchers.passes(R1006_FreeTextMaxLength.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateSuspensionMedicationSameAsInMedicationTransaction() {
        assertThat(validateTemplate("37_suspension-medicaton-equal-to-medication-in-medication-transaction.xml"), ValidationMatchers.passes(R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction.class));
    }

    @Test
    public void validateSuspensionMedicationDifferentFromMedicationTransaction() {
        assertThat(validateTemplate("38_suspension-medicaton-different-from-medication-in-medication-transaction.xml"), ValidationMatchers.failsForRule(R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction.class));
    }

    @Test
    public void validateBeginmomentMedicationTypeYear() {
        assertThat(validateTemplate("39_beginmoment-medication-type-year.xml"), ValidationMatchers.failsForRule(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }

    @Test
    public void validateEndmomentSuspensionTypeYearMonth() {
        assertThat(validateTemplate("40_endmoment-suspension-type-yearmonth.xml"), ValidationMatchers.failsForRule(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateBeginAndEndmomentsTypesDate() {
        assertThat(validateTemplate("41_beginmoments-and-endmoments-type-date.xml"), ValidationMatchers.passes(R1008_DateTypeForBeginmomentAndEndmoment.class));
    }

    @Test
    public void validateFrequency1HourAndRegimen() {
        assertThat(validateTemplate("42_frequency-1-hour-and-regimen.xml"), ValidationMatchers.failsForRule(R1009_HourlyFrequencyInCombinationWithRegiment.class));
    }

    @Test
    public void validateDateTypeOfTypeYear() {
        assertThat(validateTemplate("43_birthdate-of-type-year.xml"), ValidationMatchers.failsForRule(R1014_NoPartialDates.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateDateTypeOfTypeDate() {
        assertThat(validateTemplate("44_birthdate-of-type-date.xml"), ValidationMatchers.passes(R1014_NoPartialDates.class));
    }

    @Test
    public void validateDateTypeOfTypeYearmonth() {
        assertThat(validateTemplate("45_birthdate-of-type-yearmonth.xml"), ValidationMatchers.failsForRule(R1014_NoPartialDates.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateNoDeliveredCnkOrName() {
        assertThat(validateTemplate("46_no-delivered-cnk-or-name.xml"), ValidationMatchers.passes(R1010_NoDeliveredname.class));
    }

    @Test
    public void validateDeliveredCnkDifferent() {
        assertThat(validateTemplate("47_delivered-cnk-different.xml"), ValidationMatchers.failsForRule(R1010_NoDeliveredname.class));
    }

    @Test
    public void validateDeliveredNameDifferent() {
        assertThat(validateTemplate("48_delivered-name-equal-no-cd.xml"), ValidationMatchers.failsForRule(R1010_NoDeliveredname.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateDeliveredCnkAndNameEqual() {
        assertThat(validateTemplate("49_delivered-cnkd-and-name-equal.xml"), ValidationMatchers.passes(R1010_NoDeliveredname.class));
    }

    @Test
    public void validateSuspensionStoppedAndEndDate() {
        assertThat(validateTemplate("50_suspension-stopped-and-enddate.xml"), ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateSuspensionStoppedAndNoEndDate() {
        assertThat(validateTemplate("51_suspension-stopped-and-no-enddate.xml"), ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateSuspensionSuspendedAndEndDate() {
        assertThat(validateTemplate("52_suspension-suspended-and-enddate.xml"), ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test
    public void validateSuspensionSuspendedAndNoEndDate() {
        assertThat(validateTemplate("53_suspension-suspended-and-no-enddate.xml"), ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test
    public void validateSuspensionCountHigherThanMaximum() {
        assertThat(validateTemplate("54_suspension-count-higher-than-maximum.xml"), ValidationMatchers.failsForRule(R1013_MaximumNumberOfTreatmentSuspensions.class));
    }

    @Test
    public void validateDayperiodInvalid() {
        assertThat(validateTemplate("55_dayperiod-unsupported-value.xml"), ValidationMatchers.failsForRule(R1015_AllowedValuesCdDayperiod.class));
    }

    @Test
    public void validateFrequencyInvalid() {
        assertThat(validateTemplate("56_frequency-unsupported-value.xml"), ValidationMatchers.failsForRule(R1016_AllowedValuesCdPeriodicity.class));
    }

    @Test
    public void validateAdministrationUnitNonVitalink() {
        assertThat(validateTemplate("57_administrationunit-non-vitalink.xml"), ValidationMatchers.failsForRule(R1024_VitalinkSupportedAdministrationUnits.class));
    }

    @Test
    public void validateTransactionIdNotUnique() {
        assertThat(validateTemplate("59_transactionID-not-unique.xml"), ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }

    @Test
    public void validateTransactionIdBlank() {
        assertThat(validateTemplate("60_transactionID-blank.xml"), ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }

    @Test
    public void validateTransactionIdTwoEntries() {
        assertThat(validateTemplate("61_transactionID-two-entries.xml"), ValidationMatchers.failsForRule(R1019_UniqueTransactionIDs.class));
    }

    @Test
    public void validateSuspensionStoppedAndDuration() {
        assertThat(validateTemplate("62_suspension-stopped-and-duration.xml"), ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateSuspensionStoppedAndNoDuration() {
        assertThat(validateTemplate("63_suspension-stopped-and-no-duration.xml"), ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateSuspensionSuspendedAndDuration() {
        assertThat(validateTemplate("64_suspension-suspended-and-duration.xml"), ValidationMatchers.passes(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test
    public void validateSuspensionSuspendedAndNoDuration() {
        assertThat(validateTemplate("65_suspension-suspended-and-no-duration.xml"), ValidationMatchers.failsForRule(R1011_TreatmentSuspensionEnddateVersusType.class));
    }

    @Test
    public void validateEndmomentAndDurationForMedicationTransaction() {
        assertThat(validateTemplate("66_medicationtransaction-duration-and-endmoment.xml"), ValidationMatchers.failsForRule(R1020_EnddateCombinedWithDuration.class));
    }

    @Test
    public void validateEndmomentAndDurationForSuspensionTransaction() {
        assertThat(validateTemplate("67_suspensiontransaction-duration-and-endmoment.xml"), ValidationMatchers.failsForRule(R1020_EnddateCombinedWithDuration.class));
    }

    @Test
    public void validateDurationValueZero() {
        assertThat(validateTemplate("68_durationvalue-zero.xml"), ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }

    @Test
    public void validateDurationValueTrailingZeroes() {
        assertThat(validateTemplate("69_durationvalue-fractionaldigits.xml"), ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }

    @Test
    public void validateDurationTimeUnitInvalidKmehrCode() {
        assertThat(validateTemplate("70_durationtimeunit-unexisting-kmehrcode.xml"), ValidationMatchers.failsForRule(R1021_DurationTimeUnitAllowedCodes.class));
    }

    @Test
    public void validateDurationTimeUnitUnsupportedCode() {
        assertThat(validateTemplate("71_durationtimeunit-notallowed-code.xml"), ValidationMatchers.failsForRule(R1021_DurationTimeUnitAllowedCodes.class));
    }

    @Test
    public void validateTemporalityInvalid() {
        assertThat(validateTemplate("72_temporality-unsupported-value.xml"), ValidationMatchers.failsForRule(R1017_AllowedValuesCdTemporality.class));
    }

    @Test
    public void validateRouteInvalid() {
        assertThat(validateTemplate("73_route-unsupported-value.xml"), ValidationMatchers.failsForRule(R1018_AllowedValuesCdDrugroute.class));
    }

    @Test
    public void validateNaturalNumber5IntegerDigits() {
        assertThat(validateTemplate("74_naturalnumber-5-integerdigits.xml"), ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }

    @Test
    public void validateRationalNumber2IntegerDigits() {
        assertThat(validateTemplate("75_rationalnumber-2-integerdigits.xml"), ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }

    @Test
    public void validateRationalNumber3FractionalDigits() {
        assertThat(validateTemplate("76_rationalnumber-3-fractionaldigits.xml"), ValidationMatchers.failsForRule(R1012_QuantityValueLimited.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateMixOfAllowedNumbers() {
        assertThat(validateTemplate("77_mix-of-allowed-numbers.xml"), ValidationMatchers.passes(R1012_QuantityValueLimited.class));
    }

    @Test
    public void validateTrailingZeroes() {
        assertThat(validateTemplate("78_trailing-zeroes.xml"), ValidationMatchers.failsForRule(R1023_QuantityTrailingZeroes.class));
    }

    @Test
    public void validateMissingPosologyAndRegimen() {
        assertThat(validateTemplate("79_missing-posology-and-regimen.xml"), ValidationMatchers.failsForRule(R1027_PosologyOrRegimen.class));
    }

    @Test
    public void validateQuantityNegativeNumber() {
        assertThat(validateTemplate("80_quantity-negative-number.xml"), ValidationMatchers.failsForRule(R1028_QuantityNonZeroPositiveNumber.class));
    }

    @Test
    public void validateQuantityZero() {
        assertThat(validateTemplate("81_quantity-zero.xml"), ValidationMatchers.failsForRule(R1028_QuantityNonZeroPositiveNumber.class));
    }

    @Test
    public void validateDurationValueNegativeDigits() {
        assertThat(validateTemplate("82_durationvalue-negativedigits.xml"), ValidationMatchers.failsForRule(R1022_DurationValueNonZeroNaturalNumber.class));
    }

    @Test
    public void validateUnexpectedTable() {
        assertThat(validateTemplate("83_unexpected-table.xml"), ValidationMatchers.failsForRule(R1025_OnlyExpectedKmehrTables.class));
    }

    @Test
    public void validateUnexpectedTableVersion() {
        assertThat(validateTemplate("84_unexpected-table-version.xml"), ValidationMatchers.failsForRule(R1026_OnlyExpectedKmehrTableVersions.class));
    }

    @Test
    public void validateUnexpectedCodeForTableVersion() {
        assertThat(validateTemplate("85_unexpected-code-for-table-version.xml"), ValidationMatchers.failsForRule(R1029_OnlyExpectedCodeForKmehrTableVersions.class));
    }

    @Test
    public void validateFrequency3WeeksAndDaynumber21() {
        assertThat(validateTemplate("86_frequency-3-weeks-and-daynumber-21.xml"), ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency3WeeksAndDaynumber22() {
        assertThat(validateTemplate("87_frequency-3-weeks-and-daynumber-22.xml"), ValidationMatchers.failsForRule(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber7() {
        assertThat(validateTemplate("88_frequency-1-week-and-daynumber-7.xml"), ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber1() {
        assertThat(validateTemplate("89_frequency-1-week-and-daynumber-1.xml"), ValidationMatchers.passes(R1001k_MultipleOfWeeksDayNumberValue.class));
    }

    @Test
    public void validateFrequency1WeekAndDaynumber0() {
        assertThat(validateTemplate("90_frequency-1-week-and-daynumber-0.xml"), ValidationMatchers.failsForRule(R1001l_DaynumberValue.class));
    }

    @Test
    public void validateIsCompleteFalse() {
        assertThat(validateTemplate("91_iscomplete-false.xml"), ValidationMatchers.failsForRule(R1032_IscompleteIsTrue.class));
    }

    @Test
    public void validateCnkIsInLocalRange() {
        assertThat(validateTemplate("92_cnk-reserved-for-local-usage.xml"), ValidationMatchers.failsForRule(R1031_CnkNotInLocalRange.class));
    }

    @Test
    public void validateCnkWrongControlNumber() {
        assertThat(validateTemplate("93_cnk-wrong-control-number.xml"), ValidationMatchers.failsForRule(R1030_CnkValidControlNumber.class));
    }

    @Test
    public void validateInnclusterWrongControlNumber() {
        assertThat(validateTemplate("94_inncluster-wrong-control-number.xml"), ValidationMatchers.failsForRule(R1030_CnkValidControlNumber.class));
    }

    @Test
    public void validateEndmomentBeforeBeginmoment() {
        assertThat(validateTemplate("95_endmoment-before-beginmoment.xml"), ValidationMatchers.failsForRule(R1033_BeginmomentVsEndmoment.class));
    }

    @Test(enabled = false, groups = {"brokenValidators"})
    public void validateEndmomentEqualsBeginmoment() {
        assertThat(validateTemplate("96_endmoment-equals-beginmoment.xml"), ValidationMatchers.passes(R1033_BeginmomentVsEndmoment.class));
    }

    @Test
    public void validateIsValidatedFalse() {
        assertThat(validateTemplate("97_isvalidated-false.xml"), ValidationMatchers.failsForRule(R1034_IsvalidatedIsTrue.class));
    }
}