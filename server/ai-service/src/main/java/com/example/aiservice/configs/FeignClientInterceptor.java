// package com.example.aiservice.configs;

// import feign.RequestInterceptor;
// import feign.RequestTemplate;
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.stereotype.Component;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// @Component
// public class FeignClientInterceptor implements RequestInterceptor {

//     @Override
//     public void apply(RequestTemplate template) {
//         // Lấy token từ header của request hiện tại
//         ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
//                 .getRequestAttributes();
//         if (requestAttributes != null) {
//             HttpServletRequest request = requestAttributes.getRequest();
//             String authorizationHeader = request.getHeader("Authorization");
//             if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//                 template.header("Authorization", authorizationHeader);
//             }
//         }
//     }
// }