2025-11-16T15:27:16.206+07:00  INFO 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Creating CV for userId=448cfa02-6f0f-4cd8-9fb2-fdb45b626418 title='Senior Software Developer' experiencesCount=1 educationsCount=1
2025-11-16T15:27:16.207+07:00 DEBUG 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Created personal info for CV (email=john.doe@example.com)
2025-11-16T15:27:16.207+07:00 DEBUG 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Created 1 experiences for CV
2025-11-16T15:27:16.208+07:00 DEBUG 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Created 1 educations for CV
2025-11-16T15:27:16.252+07:00  INFO 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Created CV id=78e3dbbe-9909-4a4d-a246-36a09e24e791 for userId=448cfa02-6f0f-4cd8-9fb2-fdb45b626418
2025-11-16T15:27:16.253+07:00 DEBUG 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : SavedCV state - personalInfo: not null, experiences size: 1, educations size: 1, skills size: 7, color: #3498db, template: modern, isVisibility: false
=== CVMapper.toDto START ===
CV ID: 78e3dbbe-9909-4a4d-a246-36a09e24e791
CV Title: Senior Software Developer
CV Color: #3498db
CV Template: modern
CV IsVisibility: false
CV Skills: 7
CV PersonalInfo: NOT NULL
PersonalInfo before mapping: NOT NULL - john.doe@example.com
PersonalInfo after mapping: NOT NULL
Experiences entities: 1
Experiences DTOs: 1
Educations entities: 1
Educations DTOs: 1
Skills from entity: 7 items
=== CVMapper.toDto END ===
DTO Color: #3498db
DTO Template: modern
DTO IsVisibility: false
DTO Skills: 7
DTO PersonalInfo: NOT NULL
2025-11-16T15:27:16.257+07:00 DEBUG 16508 --- [cv-service] [ault-executor-0] c.e.cvservice.services.apis.BaseApi      : Result DTO state - personalInfo: not null, experiences size: 1, educations size: 1, skills size: 7, color: #3498db, template: modern, isVisibility: false
Hibernate: 
    insert
    into
        `personal-infos`
        (
            avatar_public_id, avatar_url, email, fullname, location, phone, summary, id
        )
    values
        (?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: 
    insert
    into
        cvs
        (color, created_at, is_visibility, `personal_info_id`, template, title, updated_at, user_id, id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: 
    insert
    into
        educations
        (degree, end_date, field, school, start_date, id)
    values
        (?, ?, ?, ?, ?, ?)
Hibernate: 
    insert
    into
        experiences
        (company, description, end_date, position, start_date, id)
    values
        (?, ?, ?, ?, ?, ?)
Hibernate: 
    insert
    into
        cvs_educations
        (cv_id, educations_id)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cvs_experiences
        (cv_id, experiences_id)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
Hibernate: 
    insert
    into
        cv_skills
        (cv_id, skills)
    values
        (?, ?)
2025-11-16T15:27:17.339+07:00  INFO 16508 --- [cv-service] [ault-executor-0] c.e.c.s.grpcs.servers.CVGrpcServer       : createCV: userId=448cfa02-6f0f-4cd8-9fb2-fdb45b626418, cvId=78e3dbbe-9909-4a4d-a246-36a09e24e791