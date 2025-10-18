package com.example.cvservice.services.apis;

import com.example.cvservice.dtos.responses.Response;

public class BaseService {
    protected Response buildErrorResponse(int status, String message) {
        Response response = new Response();
        response.setStatusCode(status);
        response.setMessage(message);
        return response;
    }
}
