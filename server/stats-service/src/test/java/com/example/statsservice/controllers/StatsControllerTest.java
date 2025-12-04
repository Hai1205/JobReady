package com.example.statsservice.controllers;

import com.example.statsservice.dtos.responses.Response;
import com.example.statsservice.services.apis.StatsApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsApi statsApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "admin")
    void testGetDashboardStats() throws Exception {
        Response response = new Response(200, "Success");
        when(statsApi.getDashboardStats()).thenReturn(response);

        mockMvc.perform(get("/api/v1/stats/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testGetStatsReport() throws Exception {
        Response response = new Response(200, "Report generated");
        when(statsApi.getStatsReport()).thenReturn(response);

        mockMvc.perform(get("/api/v1/stats/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Report generated"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/v1/stats/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Stats Service is running"));
    }
}