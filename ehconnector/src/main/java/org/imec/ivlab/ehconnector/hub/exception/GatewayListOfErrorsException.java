package org.imec.ivlab.ehconnector.hub.exception;

import be.fgov.ehealth.standards.kmehr.schema.v1.ErrorType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GatewayListOfErrorsException extends Exception {

    private static final long serialVersionUID = 7276058746256410826L;

	private List<Error> errors = new ArrayList<>();

	public GatewayListOfErrorsException(List<ErrorType> errorTypes) {
		super("A gateway exception occurred");
		process(errorTypes);
	}

	private void process(List<ErrorType> errorTypes) {

		if (errorTypes == null) {
			return;
		}

		for (ErrorType errorType : errorTypes) {

			Error error = new Error();

			if (errorType.getCds() != null) {
				error.setStatusCodes(errorType.getCds().get(0).getValue());
			}

			if (errorType.getDescription() != null) {
				error.setMessage(errorType.getDescription().getValue());
			}

			errors.add(error);

		}

	}

	@Override
	public String toString() {

		StringBuilder sb =  new StringBuilder("GatewayException{");

		for (Error error : errors) {
			sb.append("statusCode=" + error.getStatusCode() + ", message='" + error.getMessage() + "', ");
		}

		String message = sb.toString();

		return StringUtils.removeEnd(message, ", ") + "}";

	}

	public List<Error> getErrors() {
		return errors;
	}

	public class Error {

		private String statusCode;
		private String message;

		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCodes(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}



}