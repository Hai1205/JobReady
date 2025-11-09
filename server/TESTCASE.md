# Test Cases Documentation

This document lists all unit test cases for the microservices in the JobReady project.

## User Service

### Controller Tests (UserControllerTest - 7 tests)

| STT  | Testcase                  | description                   | Expect result            | result |
| ---- | ------------------------- | ----------------------------- | ------------------------ | ------ |
| TC01 | testCreateUser_Success    | POST /users success           | 200 OK with user data    | Pass   |
| TC02 | testGetAllUsers_Success   | GET /users success            | 200 OK with user list    | Pass   |
| TC03 | testGetUserById_Success   | GET /users/{id} success       | 200 OK with user data    | Pass   |
| TC04 | testUpdateUser_Success    | PATCH /users/{id} success     | 200 OK with updated user | Pass   |
| TC05 | testUpdateUser_WithAvatar | PATCH /users/{id} with avatar | 200 OK with updated user | Pass   |
| TC06 | testDeleteUser_Success    | DELETE /users/{id} success    | 200 OK with success msg  | Pass   |
| TC07 | testHealth_Success        | GET /users/health             | 200 OK with health msg   | Pass   |

### Repository Tests (UserRepositoryTest - 8 tests)

| STT  | Testcase             | description               | Expect result           | result |
| ---- | -------------------- | ------------------------- | ----------------------- | ------ |
| TC08 | testSaveUser         | Save new user to database | User saved successfully | Pass   |
| TC09 | testFindByEmail      | Find user by email        | Returns user Optional   | Pass   |
| TC10 | testFindByUsername   | Find user by username     | Returns user Optional   | Pass   |
| TC11 | testExistsByEmail    | Check if email exists     | Returns boolean         | Pass   |
| TC12 | testExistsByUsername | Check if username exists  | Returns boolean         | Pass   |
| TC13 | testFindAll          | Find all users            | Returns user list       | Pass   |
| TC14 | testDeleteUser       | Delete user by ID         | User deleted            | Pass   |
| TC15 | testUpdateUser       | Update existing user      | User updated            | Pass   |

### Service Tests (UserServiceTest - 42 tests)

| STT  | Testcase                                                            | description                          | Expect result                 | result |
| ---- | ------------------------------------------------------------------- | ------------------------------------ | ----------------------------- | ------ |
| TC16 | testHandleCreateUser_Success                                        | Create user with valid data          | User created successfully     | Pass   |
| TC17 | testHandleCreateUser_WithNullEmail_ThrowsException                  | Create user with null email          | Throws exception              | Pass   |
| TC18 | testHandleCreateUser_WithEmptyEmail_ThrowsException                 | Create user with empty email         | Throws exception              | Pass   |
| TC19 | testHandleCreateUser_EmailAlreadyExists_ThrowsException             | Create user with existing email      | Throws exception              | Pass   |
| TC20 | testHandleCreateUser_WithNullUsername_GeneratesFromEmail            | Create user with null username       | Username generated from email | Pass   |
| TC21 | testHandleCreateUser_WithEmptyUsername_GeneratesFromEmail           | Create user with empty username      | Username generated from email | Pass   |
| TC22 | testHandleCreateUser_WithNullPassword_GeneratesRandomPassword       | Create user with null password       | Random password generated     | Pass   |
| TC23 | testHandleCreateUser_WithEmptyPassword_GeneratesRandomPassword      | Create user with empty password      | Random password generated     | Pass   |
| TC24 | testHandleCreateUser_WithAvatar_UploadsSuccessfully                 | Create user with avatar              | Avatar uploaded successfully  | Pass   |
| TC25 | testHandleCreateUser_AvatarUploadFails_ThrowsException              | Create user avatar upload fails      | Throws exception              | Pass   |
| TC26 | testHandleAuthenticateUser_WithEmail_Success                        | Authenticate with email              | User authenticated            | Pass   |
| TC27 | testHandleAuthenticateUser_WithUsername_Success                     | Authenticate with username           | User authenticated            | Pass   |
| TC28 | testHandleAuthenticateUser_UserNotFound_ThrowsException             | Authenticate non-existent user       | Throws exception              | Pass   |
| TC29 | testHandleAuthenticateUser_InvalidPassword_ThrowsException          | Authenticate with wrong password     | Throws exception              | Pass   |
| TC30 | testHandleActivateUser_Success                                      | Activate user account                | User activated                | Pass   |
| TC31 | testHandleActivateUser_UserNotFound_ThrowsException                 | Activate non-existent user           | Throws exception              | Pass   |
| TC32 | testHandleGenerateRandomPassword_GeneratesValidPassword             | Generate random password             | Valid password generated      | Pass   |
| TC33 | testHandleResetPasswordUser_Success                                 | Reset user password                  | Password reset successfully   | Pass   |
| TC34 | testHandleResetPasswordUser_UserNotFound_ThrowsException            | Reset password for non-existent user | Throws exception              | Pass   |
| TC35 | testHandleForgotPasswordUser_Success                                | Forgot password process              | Password reset initiated      | Pass   |
| TC36 | testHandleChangePasswordUser_Success                                | Change user password                 | Password changed successfully | Pass   |
| TC37 | testHandleChangePasswordUser_InvalidCurrentPassword_ThrowsException | Change with wrong current password   | Throws exception              | Pass   |
| TC38 | testHandleGetAllUsers_Success                                       | Get all users                        | Returns user list             | Pass   |
| TC39 | testHandleGetUserById_Success                                       | Get user by ID                       | Returns user data             | Pass   |
| TC40 | testHandleGetUserById_NotFound_ThrowsException                      | Get non-existent user by ID          | Throws exception              | Pass   |
| TC41 | testHandleFindByEmail_Success                                       | Find user by email                   | Returns user data             | Pass   |
| TC42 | testHandleFindByEmail_NotFound_ReturnsNull                          | Find non-existent user by email      | Returns null                  | Pass   |
| TC43 | testHandleFindByUsername_Success                                    | Find user by username                | Returns user data             | Pass   |
| TC44 | testHandleFindById_Success                                          | Find user by ID                      | Returns user data             | Pass   |
| TC45 | testHandleFindByIdentifier_WithEmail_Success                        | Find user by email identifier        | Returns user data             | Pass   |
| TC46 | testHandleFindByIdentifier_WithUsername_Success                     | Find user by username identifier     | Returns user data             | Pass   |
| TC47 | testHandleUpdateUser_UpdateFullname_Success                         | Update user fullname                 | User updated successfully     | Pass   |
| TC48 | testHandleUpdateUser_UpdateAvatar_Success                           | Update user avatar                   | User updated successfully     | Pass   |
| TC49 | testHandleUpdateUser_UserNotFound_ThrowsException                   | Update non-existent user             | Throws exception              | Pass   |
| TC50 | testHandleDeleteUser_Success                                        | Delete user successfully             | User deleted successfully     | Pass   |
| TC51 | testHandleDeleteUser_WithoutAvatar_Success                          | Delete user without avatar           | User deleted successfully     | Pass   |
| TC52 | testHandleCreateOAuth2User_NewUser_Success                          | Create OAuth2 user (new)             | OAuth2 user created           | Pass   |
| TC53 | testHandleCreateOAuth2User_ExistingUser_LinksProvider               | Create OAuth2 user (existing)        | Provider linked to user       | Pass   |
| TC54 | testHandleFindOAuth2User_ByEmailAndProvider_Success                 | Find OAuth2 user by email+provider   | Returns OAuth2 user           | Pass   |
| TC55 | testHandleFindOAuth2User_ByEmailOnly_Success                        | Find OAuth2 user by email only       | Returns OAuth2 user           | Pass   |
| TC56 | testHandleFindOAuth2User_NotFound_ReturnsNull                       | Find non-existent OAuth2 user        | Returns null                  | Pass   |
| TC57 | testHandleUpdateOAuth2User_Success                                  | Update OAuth2 user                   | OAuth2 user updated           | Pass   |

## Auth Service

### Controller Tests (AuthControllerTest - 11 tests)

| STT  | Testcase                   | description                        | Expect result           | result |
| ---- | -------------------------- | ---------------------------------- | ----------------------- | ------ |
| TC58 | testLogin_Success          | POST /auth/login success           | 200 OK with token       | Pass   |
| TC59 | testRegister_Success       | POST /auth/register success        | 200 OK with user data   | Pass   |
| TC60 | testSendOTP_Success        | POST /auth/send-otp success        | 200 OK with success msg | Pass   |
| TC61 | testVerifyOTP_Success      | POST /auth/verify-otp success      | 200 OK with success msg | Pass   |
| TC62 | testChangePassword_Success | POST /auth/change-password success | 200 OK with success msg | Pass   |
| TC63 | testResetPassword_Success  | POST /auth/reset-password success  | 200 OK with success msg | Pass   |
| TC64 | testForgotPassword_Success | POST /auth/forgot-password success | 200 OK with success msg | Pass   |
| TC65 | testRefreshToken_Success   | POST /auth/refresh-token success   | 200 OK with new token   | Pass   |
| TC66 | testLogout_Success         | POST /auth/logout success          | 200 OK with success msg | Pass   |
| TC67 | testControllerBeanExists   | Check controller bean exists       | Bean exists             | Pass   |
| TC68 | testHealth_Success         | GET /auth/health                   | 200 OK with health msg  | Pass   |

### Service Tests (AuthServiceTest - 35 tests)

| STT   | Testcase                            | description                           | Expect result                 | result |
| ----- | ----------------------------------- | ------------------------------------- | ----------------------------- | ------ |
| TC69  | testLogin_Success                   | Login with valid credentials          | Returns JWT token             | Pass   |
| TC70  | testLogin_InvalidCredentials        | Login with invalid credentials        | Throws exception              | Pass   |
| TC71  | testLogin_PendingAccount            | Login with pending account            | Throws exception              | Pass   |
| TC72  | testLogin_BannedAccount             | Login with banned account             | Throws exception              | Pass   |
| TC73  | testRegister_Success                | Register new user successfully        | User registered successfully  | Pass   |
| TC74  | testValidateToken_Valid             | Validate valid JWT token              | Token is valid                | Pass   |
| TC75  | testValidateToken_Invalid           | Validate invalid JWT token            | Token is invalid              | Pass   |
| TC76  | testSendOTP_Success                 | Send OTP successfully                 | OTP sent successfully         | Pass   |
| TC77  | testSendOTP_UserNotFound            | Send OTP to non-existent user         | Throws exception              | Pass   |
| TC78  | testVerifyOTP_Success               | Verify OTP successfully               | OTP verified successfully     | Pass   |
| TC79  | testVerifyOTP_InvalidOTP            | Verify with invalid OTP               | Throws exception              | Pass   |
| TC80  | testChangePassword_Success          | Change password successfully          | Password changed successfully | Pass   |
| TC81  | testChangePassword_PasswordMismatch | Change with password mismatch         | Throws exception              | Pass   |
| TC82  | testResetPassword_Success           | Reset password successfully           | Password reset successfully   | Pass   |
| TC83  | testRefreshToken_Success            | Refresh token successfully            | New token generated           | Pass   |
| TC84  | testRefreshToken_InvalidToken       | Refresh with invalid token            | Throws exception              | Pass   |
| TC85  | testLogout_Success                  | Logout successfully                   | User logged out               | Pass   |
| TC86  | testForgotPassword_Success          | Forgot password process success       | Password reset initiated      | Pass   |
| TC87  | testForgotPassword_UserNotFound     | Forgot password for non-existent user | Throws exception              | Pass   |
| TC88  | testForgotPassword_PasswordMismatch | Forgot password with mismatch         | Throws exception              | Pass   |
| TC89  | testForgotPassword_NullFields       | Forgot password with null fields      | Throws exception              | Pass   |
| TC90  | testRegister_DuplicateEmail         | Register with duplicate email         | Throws exception              | Pass   |
| TC91  | testRegister_EmptyFields            | Register with empty fields            | Throws exception              | Pass   |
| TC92  | testRegister_InvalidJson            | Register with invalid JSON            | Throws exception              | Pass   |
| TC93  | testLogin_NullIdentifier            | Login with null identifier            | Throws exception              | Pass   |
| TC94  | testLogin_InvalidJson               | Login with invalid JSON               | Throws exception              | Pass   |
| TC95  | testVerifyOTP_NullIdentifier        | Verify OTP with null identifier       | Throws exception              | Pass   |
| TC96  | testVerifyOTP_UserNotFound          | Verify OTP for non-existent user      | Throws exception              | Pass   |
| TC97  | testVerifyOTP_InvalidJson           | Verify OTP with invalid JSON          | Throws exception              | Pass   |
| TC98  | testChangePassword_UserNotFound     | Change password for non-existent user | Throws exception              | Pass   |
| TC99  | testChangePassword_NullFields       | Change password with null fields      | Throws exception              | Pass   |
| TC100 | testResetPassword_UserNotFound      | Reset password for non-existent user  | Throws exception              | Pass   |
| TC101 | testResetPassword_NullEmail         | Reset password with null email        | Throws exception              | Pass   |
| TC102 | testRefreshToken_NoToken            | Refresh token with no token           | Throws exception              | Pass   |
| TC103 | testRefreshToken_UserNotFound       | Refresh token for non-existent user   | Throws exception              | Pass   |

### Service Tests (OtpServiceTest - 16 tests)

| STT   | Testcase                       | description                       | Expect result             | result |
| ----- | ------------------------------ | --------------------------------- | ------------------------- | ------ |
| TC104 | testGenerateOtp_Success        | Generate OTP successfully         | OTP generated and stored  | Pass   |
| TC105 | testValidateOtp_Valid          | Validate correct OTP              | Returns true              | Pass   |
| TC106 | testValidateOtp_Invalid        | Validate incorrect OTP            | Returns false             | Pass   |
| TC107 | testValidateOtp_Expired        | Validate expired OTP              | Returns false             | Pass   |
| TC108 | testOtpExists_Exists           | Check if OTP exists for email     | Returns true              | Pass   |
| TC109 | testOtpExists_NotExists        | Check if OTP doesn't exist        | Returns false             | Pass   |
| TC110 | testOtpExists_Exception        | Check exists with Redis exception | Throws RuntimeException   | Pass   |
| TC111 | testDeleteOtp_Success          | Delete OTP successfully           | OTP deleted               | Pass   |
| TC112 | testDeleteOtp_Exception        | Delete OTP with Redis exception   | Throws RuntimeException   | Pass   |
| TC113 | testGenerateOtp_NullEmail      | Generate OTP with null email      | Calls sendMail with null  | Pass   |
| TC114 | testGenerateOtp_EmptyEmail     | Generate OTP with empty email     | Calls sendMail with empty | Pass   |
| TC115 | testValidateOtp_NullEmail      | Validate with null email          | Returns false             | Pass   |
| TC116 | testValidateOtp_NullOtp        | Validate with null OTP            | Returns false             | Pass   |
| TC117 | testValidateOtp_EmptyEmail     | Validate with empty email         | Returns false             | Pass   |
| TC118 | testValidateOtp_EmptyOtp       | Validate with empty OTP           | Returns false             | Pass   |
| TC119 | testGenerateOtp_SaveOtpFails   | Generate OTP when save fails      | Throws RuntimeException   | Pass   |
| TC120 | testValidateOtp_RedisException | Validate OTP with Redis exception | Throws RuntimeException   | Pass   |

## Mail Service

### Service Tests (MailServiceTest - 12 tests)

| STT   | Testcase                                   | description                              | Expect result                      | result |
| ----- | ------------------------------------------ | ---------------------------------------- | ---------------------------------- | ------ |
| TC121 | testSendMailActivation_Success             | Send activation email successfully       | Calls sendMail with correct params | Pass   |
| TC122 | testSendMailResetPassword_Success          | Send reset password email successfully   | Calls sendMail with correct params | Pass   |
| TC123 | testSendMailActivation_ThrowsException     | Send activation email with exception     | Throws RuntimeException            | Pass   |
| TC124 | testSendMailResetPassword_ThrowsException  | Send reset password email with exception | Throws RuntimeException            | Pass   |
| TC125 | testSendMailActivation_NullEmail           | Send activation with null email          | Calls sendMail with null email     | Pass   |
| TC126 | testSendMailActivation_EmptyEmail          | Send activation with empty email         | Calls sendMail with empty email    | Pass   |
| TC127 | testSendMailActivation_NullOtp             | Send activation with null OTP            | Calls sendMail with null OTP       | Pass   |
| TC128 | testSendMailActivation_EmptyOtp            | Send activation with empty OTP           | Calls sendMail with empty OTP      | Pass   |
| TC129 | testSendMailResetPassword_NullEmail        | Send reset with null email               | Calls sendMail with null email     | Pass   |
| TC130 | testSendMailResetPassword_EmptyEmail       | Send reset with empty email              | Calls sendMail with empty email    | Pass   |
| TC131 | testSendMailResetPassword_NullNewPassword  | Send reset with null password            | Calls sendMail with null password  | Pass   |
| TC132 | testSendMailResetPassword_EmptyNewPassword | Send reset with empty password           | Calls sendMail with empty password | Pass   |

## AI Service

### Service Tests (AIServiceTest - 20 tests)

| STT   | Testcase                                                       | description                             | Expect result                         | result |
| ----- | -------------------------------------------------------------- | --------------------------------------- | ------------------------------------- | ------ |
| TC133 | testAnalyzeCV_Success                                          | Analyze CV with valid data              | Returns analysis result               | Pass   |
| TC134 | testAnalyzeCV_ThrowsOurException                               | Analyze CV with service error           | Throws OurException                   | Pass   |
| TC135 | testImproveCV_Success                                          | Improve CV section successfully         | Returns improved content              | Pass   |
| TC136 | testImproveCV_ThrowsException                                  | Improve CV with network error           | Throws OurException                   | Pass   |
| TC137 | testAnalyzeCVWithJobDescription_Success                        | Analyze CV with job description         | Returns analysis result               | Pass   |
| TC138 | testAnalyzeCVWithJobDescription_DefaultLanguage                | Analyze CV with default language        | Returns analysis result               | Pass   |
| TC139 | testAnalyzeCVWithJobDescription_ParsesMarkdownResponse         | Analyze CV with markdown response       | Returns parsed result                 | Pass   |
| TC140 | testAnalyzeCVWithJobDescription_ThrowsException                | Analyze CV with JD API error            | Throws OurException                   | Pass   |
| TC141 | testAnalyzeCV_WithEmptySuggestions                             | Analyze CV with empty suggestions       | Returns result with empty suggestions | Pass   |
| TC142 | testAnalyzeCV_WithNoPersonalInfo                               | Analyze CV without personal info        | Returns analysis result               | Pass   |
| TC143 | testAnalyzeCVWithJobDescription_WithParsedJobDescription       | Analyze CV with parsed JD               | Returns analysis result               | Pass   |
| TC144 | testAnalyzeCV_WithMalformedJSON                                | Analyze CV with malformed JSON          | Throws OurException                   | Pass   |
| TC145 | testImproveCV_WithEmptyContent                                 | Improve CV with empty content           | Returns result with empty content     | Pass   |
| TC146 | testAnalyzeCVWithJobDescription_WithMatchScoreAlternativeField | Analyze CV with alternative match score | Returns analysis result               | Pass   |
| TC147 | testAnalyzeCV_NullCV                                           | Analyze CV with null CV                 | Throws NullPointerException           | Pass   |
| TC148 | testImproveCV_NullSection                                      | Improve CV with null section            | Returns result with null section      | Pass   |
| TC149 | testImproveCV_NullContent                                      | Improve CV with null content            | Returns result with null content      | Pass   |
| TC150 | testAnalyzeCVWithJobDescription_NullCV                         | Analyze CV with JD null CV              | Throws NullPointerException           | Pass   |
| TC151 | testAnalyzeCVWithJobDescription_NullJdText                     | Analyze CV with JD null text            | Throws NullPointerException           | Pass   |
| TC152 | testAnalyzeCVWithJobDescription_EmptyJdText                    | Analyze CV with JD empty text           | Throws OurException                   | Pass   |

## CV Service

### Controller Tests (CVControllerTest - 21 tests)

| STT   | Testcase                                       | description                        | Expect result               | result |
| ----- | ---------------------------------------------- | ---------------------------------- | --------------------------- | ------ |
| TC153 | testCreateCV_Success                           | POST /cvs/users/{userId} success   | 200 OK with CV data         | Pass   |
| TC154 | testCreateCV_Unauthorized                      | POST /cvs/users/{userId} no auth   | 401 Unauthorized            | Pass   |
| TC155 | testGetAllCVs_Success                          | GET /cvs success                   | 200 OK with CV list         | Pass   |
| TC156 | testGetCVById_Success                          | GET /cvs/{cvId} success            | 200 OK with CV data         | Pass   |
| TC157 | testGetCVById_Unauthorized                     | GET /cvs/{cvId} no auth            | 401 Unauthorized            | Pass   |
| TC158 | testAnalyzeCV_Success                          | POST /cvs/analyze success          | 200 OK with analysis        | Pass   |
| TC159 | testAnalyzeCV_Unauthorized                     | POST /cvs/analyze no auth          | 401 Unauthorized            | Pass   |
| TC160 | testImproveCV_Success                          | POST /cvs/improve success          | 200 OK with improvement     | Pass   |
| TC161 | testAnalyzeCVWithJobDescription_Success        | POST /cvs/analyze-with-jd success  | 200 OK with match score     | Pass   |
| TC162 | testGetUserCVs_Success                         | GET /cvs/users/{userId} success    | 200 OK with user CVs        | Pass   |
| TC163 | testGetUserCVs_Unauthorized                    | GET /cvs/users/{userId} no auth    | 401 Unauthorized            | Pass   |
| TC164 | testGetCVByTitle_Success                       | GET /cvs/title/{title} success     | 200 OK with CV data         | Pass   |
| TC165 | testUpdateCV_Success                           | PATCH /cvs/{cvId} success          | 200 OK with updated CV      | Pass   |
| TC166 | testUpdateCV_Unauthorized                      | PATCH /cvs/{cvId} no auth          | 401 Unauthorized            | Pass   |
| TC167 | testDeleteCV_Success                           | DELETE /cvs/{cvId} success         | 200 OK with success message | Pass   |
| TC168 | testDuplicateCV_Success                        | POST /cvs/{cvId}/duplicate success | 200 OK with duplicated CV   | Pass   |
| TC169 | testHealth_Success                             | GET /cvs/health                    | 200 OK with health message  | Pass   |
| TC170 | testInvalidEndpoint                            | GET /cvs/invalid                   | 404 Not Found               | Pass   |
| TC171 | testCreateCV_WithUserRole_Success              | POST with user role                | 200 OK                      | Pass   |
| TC172 | testCreateCV_WithAdminRole_Success             | POST with admin role               | 200 OK                      | Pass   |
| TC173 | testCreateCV_WithInsufficientRole_Unauthorized | POST with insufficient role        | 403 Forbidden               | Pass   |

### Repository Tests (CVRepositoryTest - 20 tests)

| STT   | Testcase                             | description                   | Expect result          | result |
| ----- | ------------------------------------ | ----------------------------- | ---------------------- | ------ |
| TC174 | testFindById_Success                 | Find CV by existing ID        | Returns CV Optional    | Pass   |
| TC175 | testFindById_NotFound                | Find CV by non-existent ID    | Returns empty Optional | Pass   |
| TC176 | testFindAll                          | Find all CVs                  | Returns CV list        | Pass   |
| TC177 | testSave_NewCV                       | Save new CV                   | Returns saved CV       | Pass   |
| TC178 | testSave_UpdateExistingCV            | Update existing CV            | Returns updated CV     | Pass   |
| TC179 | testFindByTitle_Success              | Find CV by existing title     | Returns CV Optional    | Pass   |
| TC180 | testFindByTitle_NotFound             | Find CV by non-existent title | Returns empty Optional | Pass   |
| TC181 | testFindAllByUserId                  | Find CVs by user ID           | Returns user's CV list | Pass   |
| TC182 | testFindAllByUserId_NoCVs            | Find CVs for user with no CVs | Returns empty list     | Pass   |
| TC183 | testFindByPersonalInfoEmail_Success  | Find CV by email              | Returns CV Optional    | Pass   |
| TC184 | testFindByPersonalInfoEmail_NotFound | Find CV by non-existent email | Returns empty Optional | Pass   |
| TC185 | testExistsByPersonalInfoEmail_True   | Check if email exists (true)  | Returns true           | Pass   |
| TC186 | testExistsByPersonalInfoEmail_False  | Check if email exists (false) | Returns false          | Pass   |
| TC187 | testDeleteById                       | Delete CV by ID               | CV no longer exists    | Pass   |
| TC188 | testCount                            | Count total CVs               | Returns correct count  | Pass   |
| TC189 | testExistsById_True                  | Check if ID exists (true)     | Returns true           | Pass   |
| TC190 | testExistsById_False                 | Check if ID exists (false)    | Returns false          | Pass   |
| TC191 | testFindAll_EmptyDatabase            | Find all in empty database    | Returns empty list     | Pass   |
| TC192 | testSave_WithNullTitle               | Save CV with null title       | Returns saved CV       | Pass   |
| TC193 | testFindByTitle_CaseSensitive        | Find by title case sensitive  | Returns empty Optional | Pass   |

### Service Tests (CVServiceTest - 8 tests)

| STT   | Testcase                     | description                     | Expect result            | result |
| ----- | ---------------------------- | ------------------------------- | ------------------------ | ------ |
| TC194 | testCreateCV_Success         | Create basic CV for user        | Returns success response | Pass   |
| TC195 | testCreateCV_UserNotFound    | Create CV for non-existent user | Returns error response   | Pass   |
| TC196 | testGetCVById_Success        | Get CV by ID via service        | Returns success response | Pass   |
| TC197 | testGetCVById_NotFound       | Get non-existent CV by ID       | Returns error response   | Pass   |
| TC198 | testHandleGetCVById_Success  | Get CV by valid ID              | Returns CV DTO           | Pass   |
| TC199 | testHandleGetCVById_NotFound | Get CV by non-existent ID       | Throws OurException      | Pass   |
| TC200 | testDeleteCV_Success         | Delete CV via service method    | Returns success response | Pass   |
| TC201 | testDeleteCV_NotFound        | Delete non-existent CV          | Returns error response   | Pass   |

## Summary

- **Total Test Cases**: 201
- **All Tests Passing**: Yes
- **Coverage**: Comprehensive testing for all microservices including controller, repository, and service layers</content>
  <parameter name="filePath">c:\Users\ASUS\OneDrive\Desktop\Learn\Backend\Microservice\projects\JobReady\server\TESTCASE.md
