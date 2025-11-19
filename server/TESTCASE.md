# Test Cases Documentation

This document lists all unit test cases for the microservices in the JobReady project.

## User Service

### Controller Tests (UserControllerTest - 7 tests)

| STT  | Testcase                  | description                                       | Expect result                              | result |
| ---- | ------------------------- | ------------------------------------------------- | ------------------------------------------ | ------ |
| TC01 | testCreateUser_Success    | POST /api/v1/users with valid data and admin role | 200 OK with user data created successfully | Pass   |
| TC02 | testGetAllUsers_Success   | GET /api/v1/users with admin role                 | 200 OK with list of all users              | Pass   |
| TC03 | testGetUserById_Success   | GET /api/v1/users/{id} with user role             | 200 OK with user data for the specified ID | Pass   |
| TC04 | testUpdateUser_Success    | PATCH /api/v1/users/{id} with valid data          | 200 OK with updated user data              | Pass   |
| TC05 | testUpdateUser_WithAvatar | PATCH /api/v1/users/{id} with avatar file         | 200 OK with user data including avatar URL | Pass   |
| TC06 | testDeleteUser_Success    | DELETE /api/v1/users/{id} with admin role         | 200 OK with success message                | Pass   |
| TC07 | testHealth_Success        | GET /api/v1/users/health                          | 200 OK with health status message          | Pass   |

### Repository Tests (UserRepositoryTest - 8 tests)

| STT  | Testcase             | description                          | Expect result                                    | result |
| ---- | -------------------- | ------------------------------------ | ------------------------------------------------ | ------ |
| TC08 | testSaveUser         | Save a new user entity to database   | User saved successfully with generated ID        | Pass   |
| TC09 | testFindByEmail      | Find user by email address           | Returns Optional<User> with matching user        | Pass   |
| TC10 | testFindByUsername   | Find user by username                | Returns Optional<User> with matching user        | Pass   |
| TC11 | testExistsByEmail    | Check if email exists in database    | Returns true if email exists, false otherwise    | Pass   |
| TC12 | testExistsByUsername | Check if username exists in database | Returns true if username exists, false otherwise | Pass   |
| TC13 | testFindAll          | Retrieve all users from database     | Returns List<User> with all users                | Pass   |
| TC14 | testDeleteUser       | Delete user by ID                    | User removed from database                       | Pass   |
| TC15 | testUpdateUser       | Update existing user data            | User data updated in database                    | Pass   |

### Service Tests (UserServiceTest - 42 tests)

| STT  | Testcase                                                            | description                                                                     | Expect result                                         | result |
| ---- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------- | ------ |
| TC16 | testHandleCreateUser_Success                                        | Create user with valid data (username, email, password, fullname, role, status) | UserDto returned with created user data               | Pass   |
| TC17 | testHandleCreateUser_WithNullEmail_ThrowsException                  | Create user with null email                                                     | OurException thrown with appropriate message          | Pass   |
| TC18 | testHandleCreateUser_WithEmptyEmail_ThrowsException                 | Create user with empty email string                                             | OurException thrown with appropriate message          | Pass   |
| TC19 | testHandleCreateUser_EmailAlreadyExists_ThrowsException             | Create user with email that already exists                                      | OurException thrown indicating duplicate email        | Pass   |
| TC20 | testHandleCreateUser_WithNullUsername_GeneratesFromEmail            | Create user with null username                                                  | Username auto-generated from email prefix             | Pass   |
| TC21 | testHandleCreateUser_WithEmptyUsername_GeneratesFromEmail           | Create user with empty username                                                 | Username auto-generated from email prefix             | Pass   |
| TC22 | testHandleCreateUser_WithNullPassword_GeneratesRandomPassword       | Create user with null password                                                  | Random password generated and encoded                 | Pass   |
| TC23 | testHandleCreateUser_WithEmptyPassword_GeneratesRandomPassword      | Create user with empty password                                                 | Random password generated and encoded                 | Pass   |
| TC24 | testHandleCreateUser_WithAvatar_UploadsSuccessfully                 | Create user with avatar file                                                    | Avatar uploaded to Cloudinary, URL stored             | Pass   |
| TC25 | testHandleCreateUser_AvatarUploadFails_ThrowsException              | Create user with avatar upload failure                                          | OurException thrown for upload failure                | Pass   |
| TC26 | testHandleAuthenticateUser_WithEmail_Success                        | Authenticate user using email and password                                      | UserDto returned for valid credentials                | Pass   |
| TC27 | testHandleAuthenticateUser_WithUsername_Success                     | Authenticate user using username and password                                   | UserDto returned for valid credentials                | Pass   |
| TC28 | testHandleAuthenticateUser_UserNotFound_ThrowsException             | Authenticate with non-existent user                                             | OurException thrown for user not found                | Pass   |
| TC29 | testHandleAuthenticateUser_InvalidPassword_ThrowsException          | Authenticate with wrong password                                                | OurException thrown for invalid credentials           | Pass   |
| TC30 | testHandleActivateUser_Success                                      | Activate user account by ID                                                     | User status changed to active                         | Pass   |
| TC31 | testHandleActivateUser_UserNotFound_ThrowsException                 | Activate non-existent user                                                      | OurException thrown for user not found                | Pass   |
| TC32 | testHandleGenerateRandomPassword_GeneratesValidPassword             | Generate random password with specified length                                  | Valid password string returned                        | Pass   |
| TC33 | testHandleResetPasswordUser_Success                                 | Reset user password to new random password                                      | Password reset successfully, email sent               | Pass   |
| TC34 | testHandleResetPasswordUser_UserNotFound_ThrowsException            | Reset password for non-existent user                                            | OurException thrown for user not found                | Pass   |
| TC35 | testHandleForgotPasswordUser_Success                                | Initiate forgot password process                                                | OTP sent to user email                                | Pass   |
| TC36 | testHandleChangePasswordUser_Success                                | Change user password with valid current password                                | Password changed successfully                         | Pass   |
| TC37 | testHandleChangePasswordUser_InvalidCurrentPassword_ThrowsException | Change password with wrong current password                                     | OurException thrown for invalid current password      | Pass   |
| TC38 | testHandleGetAllUsers_Success                                       | Retrieve all users from database                                                | List<UserDto> returned with all users                 | Pass   |
| TC39 | testHandleGetUserById_Success                                       | Get user by ID                                                                  | UserDto returned for existing user                    | Pass   |
| TC40 | testHandleGetUserById_NotFound_ThrowsException                      | Get user by non-existent ID                                                     | OurException thrown with 404 status                   | Pass   |
| TC41 | testHandleFindByEmail_Success                                       | Find user by email                                                              | UserDto returned for existing email                   | Pass   |
| TC42 | testHandleFindByEmail_NotFound_ReturnsNull                          | Find user by non-existent email                                                 | null returned                                         | Pass   |
| TC43 | testHandleFindByUsername_Success                                    | Find user by username                                                           | UserDto returned for existing username                | Pass   |
| TC44 | testHandleFindById_Success                                          | Find user by ID (service method)                                                | UserDto returned for existing user                    | Pass   |
| TC45 | testHandleFindByIdentifier_WithEmail_Success                        | Find user by identifier (email format)                                          | UserDto returned, email search used                   | Pass   |
| TC46 | testHandleFindByIdentifier_WithUsername_Success                     | Find user by identifier (username format)                                       | UserDto returned, username search used                | Pass   |
| TC47 | testHandleUpdateUser_UpdateFullname_Success                         | Update user fullname                                                            | UserDto returned with updated fullname                | Pass   |
| TC48 | testHandleUpdateUser_UpdateAvatar_Success                           | Update user avatar                                                              | Avatar uploaded, old avatar deleted, UserDto returned | Pass   |
| TC49 | testHandleUpdateUser_UserNotFound_ThrowsException                   | Update non-existent user                                                        | RuntimeException thrown                               | Pass   |
| TC50 | testHandleDeleteUser_Success                                        | Delete user by ID                                                               | true returned, avatar deleted if exists               | Pass   |
| TC51 | testHandleDeleteUser_WithoutAvatar_Success                          | Delete user without avatar                                                      | true returned, no avatar deletion attempted           | Pass   |
| TC52 | testHandleCreateOAuth2User_NewUser_Success                          | Create new OAuth2 user                                                          | UserDto returned with OAuth2 provider data            | Pass   |
| TC53 | testHandleCreateOAuth2User_ExistingUser_LinksProvider               | Link OAuth2 provider to existing user                                           | UserDto returned with provider linked                 | Pass   |
| TC54 | testHandleFindOAuth2User_ByEmailAndProvider_Success                 | Find OAuth2 user by email and provider                                          | UserDto returned for matching OAuth2 user             | Pass   |
| TC55 | testHandleFindOAuth2User_ByEmailOnly_Success                        | Find OAuth2 user by email only                                                  | UserDto returned for existing user                    | Pass   |
| TC56 | testHandleFindOAuth2User_NotFound_ReturnsNull                       | Find non-existent OAuth2 user                                                   | null returned                                         | Pass   |
| TC57 | testHandleUpdateOAuth2User_Success                                  | Update OAuth2 user data                                                         | UserDto returned with updated OAuth2 data             | Pass   |

## Auth Service

### Controller Tests (AuthControllerTest - 11 tests)

| STT  | Testcase                   | description                                                          | Expect result                            | result |
| ---- | -------------------------- | -------------------------------------------------------------------- | ---------------------------------------- | ------ |
| TC58 | testLogin_Success          | POST /api/v1/auth/login with valid credentials                       | 200 OK with JWT token and user data      | Pass   |
| TC59 | testRegister_Success       | POST /api/v1/auth/register with valid user data                      | 200 OK with user registration success    | Pass   |
| TC60 | testSendOTP_Success        | POST /api/v1/auth/send-otp/{email}                                   | 200 OK with OTP sent message             | Pass   |
| TC61 | testVerifyOTP_Success      | POST /api/v1/auth/verify-otp/{email} with valid OTP                  | 200 OK with OTP verification success     | Pass   |
| TC62 | testChangePassword_Success | PATCH /api/v1/auth/change-password/{identifier} with valid passwords | 200 OK with password change success      | Pass   |
| TC63 | testResetPassword_Success  | PATCH /api/v1/auth/reset-password with valid data                    | 200 OK with password reset success       | Pass   |
| TC64 | testForgotPassword_Success | PATCH /api/v1/auth/forgot-password with valid email                  | 200 OK with forgot password initiated    | Pass   |
| TC65 | testRefreshToken_Success   | POST /api/v1/auth/refresh-token with valid refresh token             | 200 OK with new JWT token                | Pass   |
| TC66 | testLogout_Success         | POST /api/v1/auth/logout                                             | 200 OK with logout success message       | Pass   |
| TC67 | testControllerBeanExists   | Verify AuthController bean is properly configured                    | Controller bean exists and is accessible | Pass   |
| TC68 | testHealth_Success         | GET /api/v1/auth/health                                              | 200 OK with health status message        | Pass   |

### Service Tests (AuthServiceTest - 35 tests)

| STT   | Testcase                            | description                                          | Expect result                                             | result |
| ----- | ----------------------------------- | ---------------------------------------------------- | --------------------------------------------------------- | ------ |
| TC69  | testLogin_Success                   | Login with valid email/username and password         | JWT token generated and returned                          | Pass   |
| TC70  | testLogin_InvalidCredentials        | Login with invalid credentials                       | OurException thrown with invalid credentials message      | Pass   |
| TC71  | testLogin_PendingAccount            | Login with account in pending status                 | OurException thrown for pending account                   | Pass   |
| TC72  | testLogin_BannedAccount             | Login with banned account                            | OurException thrown for banned account                    | Pass   |
| TC73  | testRegister_Success                | Register new user with valid data                    | User created successfully with activation email sent      | Pass   |
| TC74  | testValidateToken_Valid             | Validate valid JWT token                             | Token validation succeeds                                 | Pass   |
| TC75  | testValidateToken_Invalid           | Validate invalid JWT token                           | Token validation fails                                    | Pass   |
| TC76  | testSendOTP_Success                 | Send OTP to valid email                              | OTP generated, stored in Redis, email sent                | Pass   |
| TC77  | testSendOTP_UserNotFound            | Send OTP to non-existent user                        | OurException thrown for user not found                    | Pass   |
| TC78  | testVerifyOTP_Success               | Verify correct OTP for email                         | OTP verification succeeds, OTP deleted from Redis         | Pass   |
| TC79  | testVerifyOTP_InvalidOTP            | Verify incorrect OTP                                 | OurException thrown for invalid OTP                       | Pass   |
| TC80  | testChangePassword_Success          | Change password with valid current and new passwords | Password changed successfully, encoded and saved          | Pass   |
| TC81  | testChangePassword_PasswordMismatch | Change password with mismatched confirm password     | OurException thrown for password mismatch                 | Pass   |
| TC82  | testResetPassword_Success           | Reset password for valid user                        | New random password generated, encoded, saved, email sent | Pass   |
| TC83  | testRefreshToken_Success            | Refresh JWT token with valid refresh token           | New JWT token pair generated                              | Pass   |
| TC84  | testRefreshToken_InvalidToken       | Refresh token with invalid refresh token             | OurException thrown for invalid token                     | Pass   |
| TC85  | testLogout_Success                  | Logout user                                          | Refresh token invalidated in Redis                        | Pass   |
| TC86  | testForgotPassword_Success          | Initiate forgot password process                     | OTP sent to user email for password reset                 | Pass   |
| TC87  | testForgotPassword_UserNotFound     | Forgot password for non-existent user                | OurException thrown for user not found                    | Pass   |
| TC88  | testForgotPassword_PasswordMismatch | Forgot password with password mismatch               | OurException thrown for password mismatch                 | Pass   |
| TC89  | testForgotPassword_NullFields       | Forgot password with null required fields            | OurException thrown for null fields                       | Pass   |
| TC90  | testRegister_DuplicateEmail         | Register user with email that already exists         | OurException thrown for duplicate email                   | Pass   |
| TC91  | testRegister_EmptyFields            | Register user with empty required fields             | OurException thrown for empty fields                      | Pass   |
| TC92  | testRegister_InvalidJson            | Register user with invalid JSON format               | OurException thrown for invalid JSON                      | Pass   |
| TC93  | testLogin_NullIdentifier            | Login with null identifier                           | OurException thrown for null identifier                   | Pass   |
| TC94  | testLogin_InvalidJson               | Login with invalid JSON format                       | OurException thrown for invalid JSON                      | Pass   |
| TC95  | testVerifyOTP_NullIdentifier        | Verify OTP with null identifier                      | OurException thrown for null identifier                   | Pass   |
| TC96  | testVerifyOTP_UserNotFound          | Verify OTP for non-existent user                     | OurException thrown for user not found                    | Pass   |
| TC97  | testVerifyOTP_InvalidJson           | Verify OTP with invalid JSON format                  | OurException thrown for invalid JSON                      | Pass   |
| TC98  | testChangePassword_UserNotFound     | Change password for non-existent user                | OurException thrown for user not found                    | Pass   |
| TC99  | testChangePassword_NullFields       | Change password with null required fields            | OurException thrown for null fields                       | Pass   |
| TC100 | testResetPassword_UserNotFound      | Reset password for non-existent user                 | OurException thrown for user not found                    | Pass   |
| TC101 | testResetPassword_NullEmail         | Reset password with null email                       | OurException thrown for null email                        | Pass   |
| TC102 | testRefreshToken_NoToken            | Refresh token without providing token                | OurException thrown for missing token                     | Pass   |
| TC103 | testRefreshToken_UserNotFound       | Refresh token for non-existent user                  | OurException thrown for user not found                    | Pass   |

### Service Tests (OtpServiceTest - 16 tests)

| STT   | Testcase                       | description                              | Expect result                                              | result |
| ----- | ------------------------------ | ---------------------------------------- | ---------------------------------------------------------- | ------ |
| TC104 | testGenerateOtp_Success        | Generate OTP for valid email             | OTP generated, stored in Redis with expiration, email sent | Pass   |
| TC105 | testValidateOtp_Valid          | Validate correct OTP for email           | Returns true, OTP deleted from Redis                       | Pass   |
| TC106 | testValidateOtp_Invalid        | Validate incorrect OTP                   | Returns false                                              | Pass   |
| TC107 | testValidateOtp_Expired        | Validate OTP after expiration            | Returns false                                              | Pass   |
| TC108 | testOtpExists_Exists           | Check if OTP exists for email            | Returns true                                               | Pass   |
| TC109 | testOtpExists_NotExists        | Check if OTP doesn't exist for email     | Returns false                                              | Pass   |
| TC110 | testOtpExists_Exception        | Check OTP existence with Redis exception | RuntimeException thrown                                    | Pass   |
| TC111 | testDeleteOtp_Success          | Delete OTP for email                     | OTP deleted from Redis                                     | Pass   |
| TC112 | testDeleteOtp_Exception        | Delete OTP with Redis exception          | RuntimeException thrown                                    | Pass   |
| TC113 | testGenerateOtp_NullEmail      | Generate OTP with null email             | Email sent with null email parameter                       | Pass   |
| TC114 | testGenerateOtp_EmptyEmail     | Generate OTP with empty email            | Email sent with empty email parameter                      | Pass   |
| TC115 | testValidateOtp_NullEmail      | Validate OTP with null email             | Returns false                                              | Pass   |
| TC116 | testValidateOtp_NullOtp        | Validate OTP with null OTP value         | Returns false                                              | Pass   |
| TC117 | testValidateOtp_EmptyEmail     | Validate OTP with empty email            | Returns false                                              | Pass   |
| TC118 | testValidateOtp_EmptyOtp       | Validate OTP with empty OTP value        | Returns false                                              | Pass   |
| TC119 | testGenerateOtp_SaveOtpFails   | Generate OTP when Redis save fails       | RuntimeException thrown                                    | Pass   |
| TC120 | testValidateOtp_RedisException | Validate OTP with Redis exception        | RuntimeException thrown                                    | Pass   |

### Integration Tests (AuthServiceIntegrationTest - 3 tests)

| STT   | Testcase                    | description                                       | Expect result                            | result |
| ----- | --------------------------- | ------------------------------------------------- | ---------------------------------------- | ------ |
| TC121 | testHealthEndpoint          | GET /api/v1/auth/health endpoint integration test | 200 OK with health status message        | Pass   |
| TC122 | testLoginEndpoint_Structure | POST /api/v1/auth/login endpoint structure test   | 200 OK response structure validated      | Pass   |
| TC123 | testContextLoads            | Spring application context loads successfully     | Application context loads without errors | Pass   |

### Service Tests (MailServiceTest - 12 tests)

| STT   | Testcase                                   | description                                       | Expect result                            | result |
| ----- | ------------------------------------------ | ------------------------------------------------- | ---------------------------------------- | ------ |
| TC124 | testSendMailActivation_Success             | Send account activation email                     | Email sent with activation OTP           | Pass   |
| TC125 | testSendMailResetPassword_Success          | Send password reset email                         | Email sent with reset password link      | Pass   |
| TC126 | testSendMailActivation_ThrowsException     | Send activation email with service exception      | RuntimeException thrown                  | Pass   |
| TC127 | testSendMailResetPassword_ThrowsException  | Send reset password email with service exception  | RuntimeException thrown                  | Pass   |
| TC128 | testSendMailActivation_NullEmail           | Send activation email with null email             | Email sent with null email parameter     | Pass   |
| TC129 | testSendMailActivation_EmptyEmail          | Send activation email with empty email            | Email sent with empty email parameter    | Pass   |
| TC130 | testSendMailActivation_NullOtp             | Send activation email with null OTP               | Email sent with null OTP parameter       | Pass   |
| TC131 | testSendMailActivation_EmptyOtp            | Send activation email with empty OTP              | Email sent with empty OTP parameter      | Pass   |
| TC132 | testSendMailResetPassword_NullEmail        | Send reset password email with null email         | Email sent with null email parameter     | Pass   |
| TC133 | testSendMailResetPassword_EmptyEmail       | Send reset password email with empty email        | Email sent with empty email parameter    | Pass   |
| TC134 | testSendMailResetPassword_NullNewPassword  | Send reset password email with null new password  | Email sent with null password parameter  | Pass   |
| TC135 | testSendMailResetPassword_EmptyNewPassword | Send reset password email with empty new password | Email sent with empty password parameter | Pass   |

## AI Service

### Service Tests (AIServiceTest - 20 tests)

| STT   | Testcase                                                       | description                                         | Expect result                                             | result |
| ----- | -------------------------------------------------------------- | --------------------------------------------------- | --------------------------------------------------------- | ------ |
| TC136 | testAnalyzeCV_Success                                          | Analyze CV with valid data and OpenRouter API       | Analysis result returned with suggestions and score       | Pass   |
| TC137 | testAnalyzeCV_ThrowsOurException                               | Analyze CV when OpenRouter API fails                | OurException thrown with API error message                | Pass   |
| TC138 | testImproveCV_Success                                          | Improve specific CV section using AI                | Improved content returned for the section                 | Pass   |
| TC139 | testImproveCV_ThrowsException                                  | Improve CV section when API fails                   | OurException thrown for network/API error                 | Pass   |
| TC140 | testAnalyzeCVWithJobDescription_Success                        | Analyze CV matching with job description            | Analysis result with match score and recommendations      | Pass   |
| TC141 | testAnalyzeCVWithJobDescription_DefaultLanguage                | Analyze CV with JD using default language           | Analysis result returned with default language processing | Pass   |
| TC142 | testAnalyzeCVWithJobDescription_ParsesMarkdownResponse         | Analyze CV with JD and parse markdown response      | Parsed result with structured suggestions                 | Pass   |
| TC143 | testAnalyzeCVWithJobDescription_ThrowsException                | Analyze CV with JD when API fails                   | OurException thrown for API failure                       | Pass   |
| TC144 | testAnalyzeCV_WithEmptySuggestions                             | Analyze CV that returns empty suggestions           | Result with empty suggestions array returned              | Pass   |
| TC145 | testAnalyzeCV_WithNoPersonalInfo                               | Analyze CV without personal information section     | Analysis result returned without personal info processing | Pass   |
| TC146 | testAnalyzeCVWithJobDescription_WithParsedJobDescription       | Analyze CV with pre-parsed job description          | Analysis result using parsed JD data                      | Pass   |
| TC147 | testAnalyzeCV_WithMalformedJSON                                | Analyze CV with malformed JSON response from API    | OurException thrown for JSON parsing error                | Pass   |
| TC148 | testImproveCV_WithEmptyContent                                 | Improve CV section with empty content               | Result with empty improved content returned               | Pass   |
| TC149 | testAnalyzeCVWithJobDescription_WithMatchScoreAlternativeField | Analyze CV with alternative match score field       | Analysis result with alternative score field processed    | Pass   |
| TC150 | testAnalyzeCV_NullCV                                           | Analyze CV with null CV object                      | NullPointerException thrown                               | Pass   |
| TC151 | testImproveCV_NullSection                                      | Improve CV with null section parameter              | Result with null section returned                         | Pass   |
| TC152 | testImproveCV_NullContent                                      | Improve CV with null content parameter              | Result with null content returned                         | Pass   |
| TC153 | testAnalyzeCVWithJobDescription_NullCV                         | Analyze CV with JD using null CV object             | NullPointerException thrown                               | Pass   |
| TC154 | testAnalyzeCVWithJobDescription_NullJdText                     | Analyze CV with JD using null job description text  | NullPointerException thrown                               | Pass   |
| TC155 | testAnalyzeCVWithJobDescription_EmptyJdText                    | Analyze CV with JD using empty job description text | OurException thrown for empty JD text                     | Pass   |

## CV Service

### Controller Tests (CVControllerTest - 21 tests)

| STT   | Testcase                                       | description                                       | Expect result                        | result |
| ----- | ---------------------------------------------- | ------------------------------------------------- | ------------------------------------ | ------ |
| TC156 | testCreateCV_Success                           | POST /cvs/users/{userId} with valid CV data       | 200 OK with created CV data          | Pass   |
| TC157 | testCreateCV_Unauthorized                      | POST /cvs/users/{userId} without authentication   | 401 Unauthorized                     | Pass   |
| TC158 | testGetAllCVs_Success                          | GET /cvs with admin role                          | 200 OK with list of all CVs          | Pass   |
| TC159 | testGetCVById_Success                          | GET /cvs/{cvId} with valid permissions            | 200 OK with CV data                  | Pass   |
| TC160 | testGetCVById_Unauthorized                     | GET /cvs/{cvId} without permissions               | 401 Unauthorized                     | Pass   |
| TC161 | testAnalyzeCV_Success                          | POST /cvs/analyze with valid CV data              | 200 OK with AI analysis result       | Pass   |
| TC162 | testAnalyzeCV_Unauthorized                     | POST /cvs/analyze without authentication          | 401 Unauthorized                     | Pass   |
| TC163 | testImproveCV_Success                          | POST /cvs/improve with valid CV section           | 200 OK with improved content         | Pass   |
| TC164 | testAnalyzeCVWithJobDescription_Success        | POST /cvs/analyze-with-jd with CV and JD data     | 200 OK with match score and analysis | Pass   |
| TC165 | testGetUserCVs_Success                         | GET /cvs/users/{userId} for own CVs               | 200 OK with user's CV list           | Pass   |
| TC166 | testGetUserCVs_Unauthorized                    | GET /cvs/users/{userId} for other user's CVs      | 401 Unauthorized                     | Pass   |
| TC167 | testGetCVByTitle_Success                       | GET /cvs/title/{title} with valid title           | 200 OK with CV data                  | Pass   |
| TC168 | testUpdateCV_Success                           | PATCH /cvs/{cvId} with valid update data          | 200 OK with updated CV data          | Pass   |
| TC169 | testUpdateCV_Unauthorized                      | PATCH /cvs/{cvId} without permissions             | 401 Unauthorized                     | Pass   |
| TC170 | testDeleteCV_Success                           | DELETE /cvs/{cvId} with valid permissions         | 200 OK with success message          | Pass   |
| TC171 | testDuplicateCV_Success                        | POST /cvs/{cvId}/duplicate with valid permissions | 200 OK with duplicated CV data       | Pass   |
| TC172 | testHealth_Success                             | GET /cvs/health                                   | 200 OK with health status message    | Pass   |
| TC173 | testInvalidEndpoint                            | GET /cvs/invalid endpoint                         | 404 Not Found                        | Pass   |
| TC174 | testCreateCV_WithUserRole_Success              | POST /cvs with user role permissions              | 200 OK                               | Pass   |
| TC175 | testCreateCV_WithAdminRole_Success             | POST /cvs with admin role permissions             | 200 OK                               | Pass   |
| TC176 | testCreateCV_WithInsufficientRole_Unauthorized | POST /cvs with insufficient role permissions      | 403 Forbidden                        | Pass   |

### Repository Tests (CVRepositoryTest - 20 tests)

| STT   | Testcase                             | description                                 | Expect result                            | result |
| ----- | ------------------------------------ | ------------------------------------------- | ---------------------------------------- | ------ |
| TC177 | testFindById_Success                 | Find CV by existing ID                      | Returns Optional<CV> with CV data        | Pass   |
| TC178 | testFindById_NotFound                | Find CV by non-existent ID                  | Returns empty Optional                   | Pass   |
| TC179 | testFindAll                          | Retrieve all CVs from database              | Returns List<CV> with all CVs            | Pass   |
| TC180 | testSave_NewCV                       | Save new CV entity                          | Returns saved CV with generated ID       | Pass   |
| TC181 | testSave_UpdateExistingCV            | Update existing CV data                     | Returns updated CV                       | Pass   |
| TC182 | testFindByTitle_Success              | Find CV by existing title                   | Returns Optional<CV> with matching CV    | Pass   |
| TC183 | testFindByTitle_NotFound             | Find CV by non-existent title               | Returns empty Optional                   | Pass   |
| TC184 | testFindAllByUserId                  | Find all CVs for specific user              | Returns List<CV> for user                | Pass   |
| TC185 | testFindAllByUserId_NoCVs            | Find CVs for user with no CVs               | Returns empty list                       | Pass   |
| TC186 | testFindByPersonalInfoEmail_Success  | Find CV by personal info email              | Returns Optional<CV> with matching email | Pass   |
| TC187 | testFindByPersonalInfoEmail_NotFound | Find CV by non-existent personal info email | Returns empty Optional                   | Pass   |
| TC188 | testExistsByPersonalInfoEmail_True   | Check if personal info email exists (true)  | Returns true                             | Pass   |
| TC189 | testExistsByPersonalInfoEmail_False  | Check if personal info email exists (false) | Returns false                            | Pass   |
| TC190 | testDeleteById                       | Delete CV by ID                             | CV removed from database                 | Pass   |
| TC191 | testCount                            | Count total number of CVs                   | Returns correct count                    | Pass   |
| TC192 | testExistsById_True                  | Check if CV ID exists (true)                | Returns true                             | Pass   |
| TC193 | testExistsById_False                 | Check if CV ID exists (false)               | Returns false                            | Pass   |
| TC194 | testFindAll_EmptyDatabase            | Find all CVs in empty database              | Returns empty list                       | Pass   |
| TC195 | testSave_WithNullTitle               | Save CV with null title                     | Returns saved CV                         | Pass   |
| TC196 | testFindByTitle_CaseSensitive        | Find CV by title with case sensitivity      | Returns empty Optional (case sensitive)  | Pass   |

### Service Tests (CVServiceTest - 8 tests)

| STT   | Testcase                     | description                          | Expect result                                      | result |
| ----- | ---------------------------- | ------------------------------------ | -------------------------------------------------- | ------ |
| TC197 | testCreateCV_Success         | Create basic CV for user via service | Returns success response with CV data              | Pass   |
| TC198 | testCreateCV_UserNotFound    | Create CV for non-existent user      | Returns error response with user not found message | Pass   |
| TC199 | testGetCVById_Success        | Get CV by ID via service method      | Returns success response with CV data              | Pass   |
| TC200 | testGetCVById_NotFound       | Get non-existent CV by ID            | Returns error response with CV not found message   | Pass   |
| TC201 | testHandleGetCVById_Success  | Handle get CV by valid ID            | Returns CV DTO                                     | Pass   |
| TC202 | testHandleGetCVById_NotFound | Handle get CV by non-existent ID     | Throws OurException with 404 status                | Pass   |
| TC203 | testDeleteCV_Success         | Delete CV via service method         | Returns success response                           | Pass   |
| TC204 | testDeleteCV_NotFound        | Delete non-existent CV               | Returns error response                             | Pass   |

## Summary

- **Total Test Cases**: 204
- **All Tests Passing**: Yes
- **Coverage**: Comprehensive testing for all microservices including controller, repository, service, and integration layers
- **User Service**: 57 tests (7 controller + 8 repository + 42 service)
- **Auth Service**: 77 tests (11 controller + 35 service + 16 OTP service + 3 integration + 12 mail service)
- **AI Service**: 20 tests (20 service tests)
- **CV Service**: 49 tests (21 controller + 20 repository + 8 service)
- **Test Types**: Unit tests, Integration tests, Controller tests, Repository tests, Service tests
- **Testing Framework**: JUnit 5, Mockito, Spring Boot Test
- **Coverage Areas**: Happy path, Error handling, Edge cases, Security, Validation, Database operations, External API integration</content>
  <parameter name="filePath">c:\Users\ASUS\OneDrive\Desktop\Learn\Backend\Microservice\projects\JobReady\server\TESTCASE.md
