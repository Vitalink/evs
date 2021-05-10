package org.imec.ivlab.validator;

import org.imec.ivlab.validator.exceptions.ValidatorException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BatchKmehrValidatorFromIde {


    public static void main(String[] args) throws ValidatorException {


        BatchKmehrValidator validator = new BatchKmehrValidator();


        BatchKmehrValidatorArguments arguments = new BatchKmehrValidatorArguments();

        arguments.setScanRootDir(new File(""));
        arguments.setReportDir(new File(""));

        List<String> xsdErrorsToIgnore = Arrays.asList(new String[]{"The content of element 'Signature' is not complete"});
        arguments.setXsdErrorMessagesToIgnore(xsdErrorsToIgnore);


        File report = validator.validate(arguments);


    }

}
