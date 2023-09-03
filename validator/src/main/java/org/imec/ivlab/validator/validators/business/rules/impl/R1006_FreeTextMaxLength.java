package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.CompoundPrescriptionUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1006_FreeTextMaxLength extends BaseMSEntryRule {

    private static final int MAX_LENGTH_FREE_TEXT = 340;

    @Override
    public String getMessage() {
        return "Maximum length of free text fields is " + MAX_LENGTH_FREE_TEXT + " characters";
    }

    @Override
    public String getRuleId() {
        return "1006";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            List<TextType> textTypes = TransactionUtil.getText(transaction);

            if (!isLengthOk(textTypes)) {
                return failRule(msEntry.getMseTransaction());
            }

            for (ItemType itemType : TransactionUtil.getItems(transaction)) {
                if (!(isLengthOk(itemType.getTexts()) && isLengthOk(itemType.getInstructionforoverdosing()) && isLengthOk(itemType.getInstructionforpatient()) && isLengthOk(itemType.getInstructionforreimbursement()))) {
                    return failRule(msEntry.getMseTransaction());
                }

                if (!CollectionsUtil.notEmptyOrNull(itemType.getContents())) {
                    for (ContentType contentType : itemType.getContents()) {
                        if (!isLengthOk(contentType.getTexts())) {
                            return failRule(msEntry.getMseTransaction());
                        }

                        CompoundPrescriptionUtil compoundPrescriptionUtil = new CompoundPrescriptionUtil();
                        if (contentType.getCompoundprescription() != null) {
                            if (!isLengthOkStrings(compoundPrescriptionUtil.getCompoundText(contentType.getCompoundprescription()))) {
                                return failRule(msEntry.getMseTransaction());
                            }
                            if (!isLengthOk(compoundPrescriptionUtil.getMagistralText(contentType.getCompoundprescription()))) {
                                return failRule(msEntry.getMseTransaction());
                            }
                        }
                        if (!isLengthOk(contentType.getEcg())) {
                            return failRule(msEntry.getMseTransaction());
                        }
                        if (!isLengthOk(contentType.getBacteriology())) {
                            return failRule(msEntry.getMseTransaction());
                        }
                        if (contentType.getLocation() != null && !isLengthOk(contentType.getLocation().getText())) {
                            return failRule(msEntry.getMseTransaction());
                        }
                        if (contentType.getMedication() != null) {
                            if (!isLengthOk(contentType.getMedication().getInstructionforreimbursement())) {
                                return failRule(msEntry.getMseTransaction());
                            }
                            if (!isLengthOk(contentType.getMedication().getInn())) {
                                return failRule(msEntry.getMseTransaction());
                            }
                            if (!isLengthOk(contentType.getMedication().getInstructionforoverdosing())) {
                                return failRule(msEntry.getMseTransaction());
                            }
                            if (!isLengthOk(contentType.getMedication().getInstructionforpatient())) {
                                return failRule(msEntry.getMseTransaction());
                            }
                            if (!isLengthOk(contentType.getMedication().getMagistral())) {
                                return failRule(msEntry.getMseTransaction());
                            }
                        }
                    }

                }

                if (itemType.getPosology() != null) {
                    if (!isLengthOk(itemType.getPosology().getText())) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }

                if (itemType.getBeginmoment() != null) {
                    if (!isLengthOk(itemType.getBeginmoment().getText())) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }

                if (itemType.getEndmoment() != null) {
                    if (!isLengthOk(itemType.getEndmoment().getText())) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }

                if (itemType.getFrequency() != null) {
                    if (!isLengthOk(itemType.getFrequency().getText())) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }
                if (itemType.getRenewal() != null) {
                    if (!isLengthOk(itemType.getRenewal().getText())) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }

            }


        }

        return passRule();
    }

    private boolean isLengthOk(List<TextType> textTypes) {

        if (textTypes == null) {
            return true;
        }

        for (TextType textType : textTypes) {
            if (!isLengthOk(textType)) {
                return false;
            }
        }

        return true;

    }

    private boolean isLengthOkStrings(List<String> strings) {

        if (strings == null) {
            return true;
        }

        for (String string : strings) {
            if (!isLengthOk(string)) {
                return false;
            }
        }

        return true;

    }

    private boolean isLengthOk(TextType textType) {

        return textType == null || isLengthOk(textType.getValue());

    }

    private boolean isLengthOk(String string) {

        return StringUtils.length(string) <= MAX_LENGTH_FREE_TEXT;

    }

}
