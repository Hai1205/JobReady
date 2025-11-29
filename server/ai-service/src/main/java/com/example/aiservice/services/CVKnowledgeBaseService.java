package com.example.aiservice.services;

import com.example.aiservice.entities.CVTemplateEntity;
import com.example.aiservice.repositories.CVTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVKnowledgeBaseService {

    private final CVTemplateRepository templateRepository;
    private final EnhancedEmbeddingService embeddingService;
    
    private boolean initialized = false;

    /**
     * Initialize knowledge base khi application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeKnowledgeBase() {
        if (initialized) {
            log.info("Knowledge base already initialized, skipping...");
            return;
        }

        log.info("=== Starting knowledge base initialization ===");
        
        try {
            // 1. Check if templates exist
            long templateCount = templateRepository.count();
            
            if (templateCount == 0) {
                log.info("No templates found, creating default templates...");
                createDefaultTemplates();
            }
            
            // 2. Load high-quality templates
            List<CVTemplateEntity> templates = templateRepository
                .findByRatingGreaterThanEqualAndIsActiveTrue(4);
            
            log.info("Loaded {} high-quality templates from database", templates.size());
            
            // 3. Convert to Documents and ingest
            List<Document> documents = templates.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());
            
            if (!documents.isEmpty()) {
                embeddingService.batchIngestTemplates(documents);
            }
            
            initialized = true;
            log.info("=== Knowledge base initialization completed successfully ===");
            
        } catch (Exception e) {
            log.error("Failed to initialize knowledge base", e);
            throw new RuntimeException("Knowledge base initialization failed", e);
        }
    }

    /**
     * Manually reinitialize
     */
    @Transactional
    public void reinitializeKnowledgeBase() {
        log.info("Manual re-initialization triggered");
        initialized = false;
        initializeKnowledgeBase();
    }

    /**
     * Add new template
     */
    @Transactional
    public CVTemplateEntity addTemplate(CVTemplateEntity template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setIsActive(true);
        
        CVTemplateEntity saved = templateRepository.save(template);
        
        // Ingest if high quality
        if (saved.getRating() >= 4) {
            Document document = convertToDocument(saved);
            embeddingService.ingestTemplate(
                saved.getId(), 
                document.getContent(), 
                document.getMetadata()
            );
            log.info("Template {} added and ingested", saved.getId());
        }
        
        return saved;
    }

    /**
     * Update template
     */
    @Transactional
    public CVTemplateEntity updateTemplate(String id, CVTemplateEntity updatedTemplate) {
        CVTemplateEntity existing = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        
        existing.setContent(updatedTemplate.getContent());
        existing.setCategory(updatedTemplate.getCategory());
        existing.setLevel(updatedTemplate.getLevel());
        existing.setSection(updatedTemplate.getSection());
        existing.setRating(updatedTemplate.getRating());
        existing.setKeywords(updatedTemplate.getKeywords());
        existing.setUpdatedAt(LocalDateTime.now());
        
        CVTemplateEntity saved = templateRepository.save(existing);
        
        // Update in vector store
        if (saved.getRating() >= 4 && saved.getIsActive()) {
            Document document = convertToDocument(saved);
            embeddingService.updateTemplate(
                saved.getId(), 
                document.getContent(), 
                document.getMetadata()
            );
        } else {
            embeddingService.deleteTemplate(saved.getId());
        }
        
        return saved;
    }

    /**
     * Soft delete template
     */
    @Transactional
    public void deleteTemplate(String id) {
        CVTemplateEntity template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        
        template.setIsActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);
        
        embeddingService.deleteTemplate(id);
        log.info("Template {} soft deleted", id);
    }

    /**
     * Convert entity to Document
     */
    private Document convertToDocument(CVTemplateEntity template) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("category", template.getCategory());
        metadata.put("level", template.getLevel());
        metadata.put("section", template.getSection());
        metadata.put("rating", template.getRating());
        metadata.put("keywords", template.getKeywords() != null ? template.getKeywords() : List.of());
        
        return new Document(
            template.getId(),
            template.getContent(),
            metadata
        );
    }

    /**
     * Create default templates
     */
    @Transactional
    protected void createDefaultTemplates() {
        List<CVTemplateEntity> templates = Arrays.asList(
            // SUMMARY - Senior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("senior")
                .section("summary")
                .content("Senior Software Engineer with 8+ years of experience building scalable web applications using Java, Spring Boot, and React. Led cross-functional teams of 5-10 engineers to deliver mission-critical features serving 1M+ users. Specialized in microservices architecture and cloud infrastructure, achieving 99.9% uptime and reducing API response time by 60%.")
                .rating(5)
                .keywords(Arrays.asList("Senior", "8+ years", "Spring Boot", "React", "microservices", "99.9% uptime", "Led teams"))
                .isActive(true)
                .build(),
            
            // SUMMARY - Mid Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("mid")
                .section("summary")
                .content("Full-Stack Developer with 4 years of experience building responsive web applications. Proficient in React, Node.js, PostgreSQL, and AWS. Successfully delivered 15+ production features that improved user engagement by 35%. Strong advocate for clean code, test-driven development, and continuous integration practices.")
                .rating(5)
                .keywords(Arrays.asList("4 years", "Full-Stack", "React", "Node.js", "PostgreSQL", "AWS", "TDD"))
                .isActive(true)
                .build(),
            
            // SUMMARY - Junior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("junior")
                .section("summary")
                .content("Motivated Junior Developer with 1 year of hands-on experience in web development using JavaScript, React, and Node.js. Completed 10+ features including user authentication, REST APIs, and responsive UI components. Quick learner with strong problem-solving skills and passion for writing maintainable code.")
                .rating(5)
                .keywords(Arrays.asList("1 year", "Junior", "JavaScript", "React", "Node.js", "REST APIs"))
                .isActive(true)
                .build(),
            
            // EXPERIENCE - Senior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("senior")
                .section("experience")
                .content("• Led 8-engineer team to rebuild legacy monolith into microservices architecture using Spring Boot and Docker, reducing deployment time from 2 hours to 15 minutes and improving system reliability by 40%\n• Architected and implemented Redis caching strategy and database optimization, improving API response time by 65% and successfully handling 50K concurrent users during peak traffic\n• Mentored 3 junior developers through code reviews, pair programming, and technical workshops, resulting in 40% reduction in production bugs and improved team velocity by 25%")
                .rating(5)
                .keywords(Arrays.asList("Led", "microservices", "Spring Boot", "Docker", "Redis", "65% improvement", "Mentored"))
                .isActive(true)
                .build(),
            
            // EXPERIENCE - Mid Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("mid")
                .section("experience")
                .content("• Developed 20+ RESTful APIs using Node.js and Express, serving 10K daily active users with 99.5% uptime and average response time under 200ms\n• Implemented JWT-based authentication and role-based access control (RBAC) system, enhancing security for 5K+ user accounts and reducing unauthorized access attempts by 80%\n• Optimized database queries and added proper indexes in PostgreSQL, reducing page load time from 3 seconds to 800ms and improving overall application performance by 45%")
                .rating(5)
                .keywords(Arrays.asList("Developed", "RESTful APIs", "Node.js", "10K users", "JWT", "Optimized", "PostgreSQL"))
                .isActive(true)
                .build(),
            
            // SKILLS - Senior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("senior")
                .section("skills")
                .content("Backend: Java, Spring Boot, Spring Security, Hibernate, JPA, Microservices | Frontend: React, TypeScript, Redux, Next.js, Material-UI | Database: PostgreSQL, MongoDB, Redis, Elasticsearch | Cloud & DevOps: AWS (EC2, S3, RDS, Lambda), Docker, Kubernetes, Jenkins, GitHub Actions | Tools: Git, Jira, Confluence, Postman, DataGrip")
                .rating(5)
                .keywords(Arrays.asList("Java", "Spring Boot", "React", "TypeScript", "PostgreSQL", "MongoDB", "AWS", "Docker", "Kubernetes"))
                .isActive(true)
                .build(),
            
            // SKILLS - Mid Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("mid")
                .section("skills")
                .content("Programming: JavaScript, TypeScript, Python, SQL | Frontend: React, HTML5, CSS3, Tailwind CSS, Bootstrap | Backend: Node.js, Express, NestJS, REST APIs | Database: PostgreSQL, MySQL, MongoDB | Tools & Others: Git, Docker, Jest, Postman, VS Code, Agile/Scrum")
                .rating(5)
                .keywords(Arrays.asList("JavaScript", "TypeScript", "React", "Node.js", "PostgreSQL", "MongoDB", "Docker", "Git"))
                .isActive(true)
                .build(),
            
            // EDUCATION - Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("mid")
                .section("education")
                .content("Bachelor of Science in Computer Science | XYZ University | 2016 - 2020 | GPA: 3.8/4.0\nRelevant Coursework: Data Structures & Algorithms, Database Systems, Web Development, Software Engineering, Operating Systems\nAchievements: Dean's List (4 semesters), First Prize in University Hackathon 2019")
                .rating(5)
                .keywords(Arrays.asList("Computer Science", "Bachelor", "GPA 3.8", "Dean's List", "Hackathon"))
                .isActive(true)
                .build(),

            // JOB DESCRIPTION - Senior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("senior")
                .section("job_description")
                .content("Senior Backend Engineer\n\nWe are seeking an experienced Senior Backend Engineer to join our growing engineering team. You will lead the design and development of scalable microservices, mentor junior developers, and drive technical decisions.\n\nRequired Skills:\n- 5+ years of experience in backend development\n- Expert in Java, Spring Boot, microservices architecture\n- Strong experience with PostgreSQL, Redis, Kafka\n- Proven track record of leading technical projects\n- Experience with AWS, Docker, Kubernetes\n\nResponsibilities:\n- Design and implement scalable backend services\n- Lead code reviews and maintain code quality\n- Mentor junior and mid-level engineers\n- Collaborate with product and frontend teams\n- Optimize system performance and reliability")
                .rating(5)
                .keywords(Arrays.asList("Senior", "Backend", "5+ years", "Spring Boot", "microservices", "Lead", "AWS"))
                .isActive(true)
                .build(),
            
            // JOB DESCRIPTION - Mid Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("mid")
                .section("job_description")
                .content("Full-Stack Developer\n\nWe're looking for a talented Full-Stack Developer to help build and maintain our web applications. You'll work with modern technologies and collaborate with a dynamic team.\n\nRequired Skills:\n- 2-4 years of experience in web development\n- Proficient in React, Node.js, TypeScript\n- Experience with PostgreSQL or MongoDB\n- Familiar with RESTful APIs and Git\n- Good understanding of responsive design\n\nResponsibilities:\n- Develop and maintain web applications\n- Write clean, maintainable code\n- Participate in code reviews\n- Collaborate with designers and backend team\n- Debug and fix issues in production")
                .rating(5)
                .keywords(Arrays.asList("Mid", "Full-Stack", "2-4 years", "React", "Node.js", "TypeScript", "APIs"))
                .isActive(true)
                .build(),
            
            // JOB DESCRIPTION - Junior Tech
            CVTemplateEntity.builder()
                .category("tech")
                .level("junior")
                .section("job_description")
                .content("Junior Frontend Developer\n\nJoin our team as a Junior Frontend Developer! This is a great opportunity for recent graduates or developers with 1-2 years of experience to grow their skills.\n\nRequired Skills:\n- 0-2 years of experience in frontend development\n- Knowledge of HTML, CSS, JavaScript\n- Basic understanding of React or Vue.js\n- Familiarity with Git version control\n- Willingness to learn and adapt\n\nResponsibilities:\n- Build responsive user interfaces\n- Implement designs from Figma/Sketch\n- Write clean, well-documented code\n- Learn from senior developers through code reviews\n- Fix bugs and improve existing features")
                .rating(5)
                .keywords(Arrays.asList("Junior", "Frontend", "0-2 years", "JavaScript", "React", "HTML", "CSS"))
                .isActive(true)
                .build()
        );

        templateRepository.saveAll(templates);
        log.info("Created {} default templates", templates.size());
    }

    public List<CVTemplateEntity> getAllTemplates() {
        return templateRepository.findByIsActiveTrue();
    }
}