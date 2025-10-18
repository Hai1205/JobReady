package com.example.userservice.services.apis;

import com.example.userservice.dtos.response.Response;

public class BaseService {
    protected Response buildErrorResponse(int status, String message) {
        Response response = new Response();
        response.setStatusCode(status);
        response.setMessage(message);
        return response;
    }
}
