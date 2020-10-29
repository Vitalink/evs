package org.imec.ivlab.validator;

import org.imec.ivlab.validator.exceptions.ValidatorException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BatchKmehrValidatorFromIde {


    public static void main(String[] args) throws ValidatorException {


        BatchKmehrValidator validator = new BatchKmehrValidator();


        BatchKmehrValidatorArguments arguments = new BatchKmehrValidatorArguments();
//        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/tests/SKT_2017_06/voorbereiding"));
//        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/reports/RKT_2017_04/Kluis Exports"));
//        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/tests/temp/Thomas/"));

//        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/kluis_exports/72071135503/to-validate"));
//        arguments.setReportDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/kluis_exports/72071135503/to-validate"));

//        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/kluis_exports/73100906111/to-validate"));
//        arguments.setReportDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/kluis_exports/73100906111/to-validate"));
//
        arguments.setScanRootDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/test_specs/EVS_scenarios"));
        arguments.setReportDir(new File("/Users/machtst/Dropbox (imec Ghent)/iMinds Health - Team/Health Validation Lab/Lab Operations/Testbatterij Vitalink/test_specs/EVS_scenarios/000_validatie/validatieresultaten/"));

        List<String> xsdErrorsToIgnore = Arrays.asList(new String[]{"The content of element 'Signature' is not complete"});
        arguments.setXsdErrorMessagesToIgnore(xsdErrorsToIgnore);

//        List<String> ruleIdsToIgnore = Arrays.asList(new String[]{"1031"});
//        arguments.setRuleIdsToIgnore(ruleIdsToIgnore);

        File report = validator.validate(arguments);


    }

}
