// package com.example.authservice.dto.messages;

// import java.io.Serializable;

// public class AuthenticationMessage implements Serializable {
//     private String email;
//     private String password;
//     private String correlationId;

//     public AuthenticationMessage() {
//     }

//     public AuthenticationMessage(String correlationId, String email, String password) {
//         this.correlationId = correlationId;
//         this.email = email;
//         this.password = password;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }

//     public String getCorrelationId() {
//         return correlationId;
//     }

//     public void setCorrelationId(String correlationId) {
//         this.correlationId = correlationId;
//     }

//     @Override
//     public String toString() {
//         return "AuthenticationRequest{" +
//                 "email='" + email + '\'' +
//                 ", correlationId='" + correlationId + '\'' +
//                 '}';
//     }
// }