package com.jonathand.issuecrush.response;

public class ResponseMissingParameterException extends RuntimeException {

    public ResponseMissingParameterException(String parameterName, String parameterType) {
        super("Parameter " + parameterName + " is missing (" + parameterType + ")");
    }

}
