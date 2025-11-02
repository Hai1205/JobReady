package com.example.authservice.services.apis;

import com.example.authservice.dtos.responses.Response;

public class BaseService {
    protected Response buildErrorResponse(int status, String message) {
        Response response = new Response();
        response.setStatusCode(status);
        response.setMessage(message);
        return response;
    }
}
