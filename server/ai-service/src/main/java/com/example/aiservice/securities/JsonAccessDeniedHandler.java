// package com.example.aiservice.securities;

// import java.io.IOException;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import org.springframework.http.MediaType;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.web.access.AccessDeniedHandler;
// import org.springframework.stereotype.Component;

// @Component
// public class JsonAccessDeniedHandler implements AccessDeniedHandler {

//     @Override
//     public void handle(HttpServletRequest request, HttpServletResponse response,
//             AccessDeniedException accessDeniedException)
//             throws IOException {
//         response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//         response.getWriter().write("{\"status\":403,\"message\":\"Forbidden\"}");
//     }
// }
