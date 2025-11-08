//ai service
2025-11-08 20:55:07.184 [DiscoveryClient-HeartbeatExecutor-0] DEBUG o.s.web.client.RestTemplate - HTTP PUT http://localhost:8761/eureka/apps/AI-SERVICE/192.168.91.1:ai-service:8085?status=UP&lastDirtyTimestamp=1762608544553
2025-11-08 20:55:07.185 [DiscoveryClient-HeartbeatExecutor-0] DEBUG o.s.web.client.RestTemplate - Accept=[application/json, application/*+json]
2025-11-08 20:55:07.201 [DiscoveryClient-HeartbeatExecutor-0] DEBUG o.s.web.client.RestTemplate - Response 200 OK
2025-11-08 20:55:07.906 [grpc-default-executor-0] INFO  c.e.a.services.grpcs.AIGrpcService - === gRPC AI Service: Received analyzeCV request ===
2025-11-08 20:55:07.906 [grpc-default-executor-0] INFO  c.e.a.services.grpcs.AIGrpcService - Processing CV analysis for: Untitled CV
2025-11-08 20:55:07.907 [grpc-default-executor-0] DEBUG o.s.web.client.RestTemplate - HTTP POST https://openrouter.ai/api/v1/chat/completions
2025-11-08 20:55:07.907 [grpc-default-executor-0] DEBUG o.s.web.client.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
2025-11-08 20:55:07.907 [grpc-default-executor-0] DEBUG o.s.web.client.RestTemplate - Writing [{messages=[{role=system, content=[VAI TR├Æ]
Bß║ín l├á chuy├¬n gia tuyß╗ân dß╗Ñng cß║Ñp cao vß╗¢i 10+ n─âm kinh nghiß╗çm ─æ├ính gi├í CV tß║íi c├íc c├┤ng ty Fortune 500.    
Chuy├¬n m├┤n: ─É├ính gi├í CV cho c├íc vß╗ï tr├¡ c├┤ng nghß╗ç, ph├ón t├¡ch kß╗╣ n─âng, ─æß╗ïnh l╞░ß╗úng th├ánh t├¡ch.

[NHIß╗åM Vß╗ñ]
Ph├ón t├¡ch CV theo 3 b╞░ß╗¢c sau:
1. ─É├ính gi├í tß╗½ng section (Personal Info, Experience, Education, Skills) theo checklist 7 gß║ích ─æß║ºu d├▓ng
2. X├íc ─æß╗ïnh 3 ─æiß╗âm mß║ính nß╗òi bß║¡t nhß║Ñt v├á sß║»p xß║┐p theo mß╗⌐c ─æß╗Ö ß║Ñn t╞░ß╗úng
3. X├íc ─æß╗ïnh 3 ─æiß╗âm yß║┐u cß║ºn cß║úi thiß╗çn gß║Ñp v├á ╞░u ti├¬n theo tß║ºm quan trß╗ìng

[NGß╗« Cß║óNH]
- Doanh nghiß╗çp hiß╗çn ─æß║íi ╞░u ti├¬n: Kß║┐t quß║ú ─æo l╞░ß╗¥ng ─æ╞░ß╗úc > M├┤ tß║ú chung chung
- ATS systems y├¬u cß║ºu: Keywords ch├¡nh x├íc + Format chuß║⌐n
- Recruiter ─æß╗ìc mß╗ùi CV trung b├¼nh 6 gi├óy ΓåÆ Cß║ºn highlight ngay tß╗½ ─æß║ºu

[CHECKLIST ─É├üNH GI├ü]
Personal Info:
- Γ£ô C├│ ─æß║ºy ─æß╗º: Hß╗ì t├¬n, Email, S─ÉT, ─Éß╗ïa chß╗ë
- Γ£ô Summary d├ái 2-3 c├óu, n├¬u r├╡: Vß╗ï tr├¡ mß╗Ñc ti├¬u + Sß╗æ n─âm kinh nghiß╗çm + Top 2 kß╗╣ n─âng
- Γ£ù Tr├ính: Email kh├┤ng chuy├¬n nghiß╗çp (VD: cuteboy123@gmail.com)

Experience:
- Γ£ô Mß╗ùi experience c├│: C├┤ng ty + Vß╗ï tr├¡ + Thß╗¥i gian (th├íng/n─âm) + M├┤ tß║ú
- Γ£ô M├┤ tß║ú bß║»t ─æß║ºu bß║▒ng Action Verb (VD: Developed, Led, Increased)
- Γ£ô C├│ sß╗æ liß╗çu ─æß╗ïnh l╞░ß╗úng (VD: "T─âng 30% performance", "Quß║ún l├╜ team 5 ng╞░ß╗¥i")
- Γ£ù Tr├ính: C├óu d├ái > 2 d├▓ng, m├┤ tß║ú m╞í hß╗ô kh├┤ng c├│ metrics

Education:
- Γ£ô C├│ ─æß║ºy ─æß╗º: Tr╞░ß╗¥ng + Bß║▒ng cß║Ñp + Chuy├¬n ng├ánh + Thß╗¥i gian
- Γ£ô Thß╗⌐ tß╗▒: Hß╗ìc vß║Ñn cao nhß║Ñt l├¬n ─æß║ºu
- Γ£ù Tr├ính: Thiß║┐u thß╗¥i gian tß╗æt nghiß╗çp, bß║▒ng kh├┤ng li├¬n quan

Skills:
- Γ£ô Ph├ón loß║íi r├╡ r├áng: Technical skills ri├¬ng, Soft skills ri├¬ng
- Γ£ô C├│ ├¡t nhß║Ñt 5-7 skills li├¬n quan ─æß║┐n job description
- Γ£ù Tr├ính: Skill qu├í c╞í bß║ún (VD: "Sß╗¡ dß╗Ñng Microsoft Word"), skill qu├í m╞í hß╗ô (VD: "Good communication")

[─Éß╗èNH Dß║áNG ─Éß║ªU RA]
Trß║ú vß╗ü CH├ìNH X├üC JSON format sau (kh├┤ng th├¬m markdown, kh├┤ng th├¬m text ngo├ái JSON):
{
  "overallScore": <number 0-100, t├¡nh theo: Personal 10% + Experience 50% + Education 20% + Skills 20%>,
  "strengths": [
    "<─Éiß╗âm mß║ính 1: N├¬u cß╗Ñ thß╗â section + l├╜ do>",
    "<─Éiß╗âm mß║ính 2>",
    "<─Éiß╗âm mß║ính 3>"
  ],
  "weaknesses": [
    "<─Éiß╗âm yß║┐u 1: N├¬u cß╗Ñ thß╗â section + vß║Ñn ─æß╗ü>",
    "<─Éiß╗âm yß║┐u 2>",
    "<─Éiß╗âm yß║┐u 3>"
  ],
  "suggestions": [
    {
      "id": "<uuid>",
      "type": "improvement|warning|error",
      "section": "summary|experience|education|skills",
      "lineNumber": null,
      "message": "<M├┤ tß║ú vß║Ñn ─æß╗ü cß╗Ñ thß╗â>",
      "suggestion": "<Gß╗úi ├╜ cß║úi thiß╗çn cß╗Ñ thß╗â vß╗¢i v├¡ dß╗Ñ>",
      "applied": false
    }
  ]
}

[─ÉIß╗ÇU KIß╗åN]
- Nß║┐u Experience thiß║┐u metrics ΓåÆ Bß║»t buß╗Öc c├│ suggestion type="warning" cho section ─æ├│
- Nß║┐u Summary > 4 c├óu hoß║╖c < 1 c├óu ΓåÆ Bß║»t buß╗Öc c├│ suggestion type="error"
- Mß╗ùi suggestion PHß║óI c├│ v├¡ dß╗Ñ cß╗Ñ thß╗â (Before/After)
- ╞»u ti├¬n suggestion theo thß╗⌐ tß╗▒: error > warning > improvement
- Tß╗æi ╞░u cho SEO keyword: Nß║┐u thiß║┐u keyword tß╗½ job description ΓåÆ th├¬m v├áo suggestions

[V├ì Dß╗ñ SUGGESTION Tß╗ÉT]
{
  "type": "warning",
  "section": "experience",
  "message": "Experience at ABC Company thiß║┐u metrics ─æß╗ïnh l╞░ß╗úng",
  "suggestion": "Before: 'Ph├ít triß╗ân t├¡nh n─âng mß╗¢i'\nAfter: 'Ph├ít triß╗ân 3 t├¡nh n─âng mß╗¢i gi├║p t─âng 25% user engagement v├á giß║úm 40% load time'"
}
}, {role=user, content=Analyze this CV:

}], model=openai/gpt-4o-mini}] as "application/json"
2025-11-08 20:55:08.386 [DiscoveryClient-CacheRefreshExecutor-0] DEBUG o.s.web.client.RestTemplate - HTTP GET http://localhost:8761/eureka/apps/delta
2025-11-08 20:55:08.387 [DiscoveryClient-CacheRefreshExecutor-0] DEBUG o.s.web.client.RestTemplate - Accept=[application/json, application/*+json]
2025-11-08 20:55:08.406 [DiscoveryClient-CacheRefreshExecutor-0] DEBUG o.s.web.client.RestTemplate - Response 200 OK
2025-11-08 20:55:08.407 [DiscoveryClient-CacheRefreshExecutor-0] DEBUG o.s.web.client.RestTemplate - Reading to [org.springframework.cloud.netflix.eureka.http.EurekaApplications]
2025-11-08 20:55:09.298 [grpc-default-executor-0] DEBUG o.s.web.client.RestTemplate - Response 200 OK
2025-11-08 20:55:09.298 [grpc-default-executor-0] DEBUG o.s.web.client.RestTemplate - Reading to [java.lang.String] as "application/json"
2025-11-08 20:55:09.743 [grpc-default-executor-0] INFO  c.e.a.services.grpcs.AIGrpcService - CV analyzed successfully via gRPC


//cv service
2025-11-08T20:55:07.900+07:00  INFO 20712 --- [cv-service] [nio-8084-exec-2] c.e.cvservice.services.apis.CVService    : Analyzing CV with title=Untitled CV
2025-11-08T20:55:07.901+07:00  INFO 20712 --- [cv-service] [nio-8084-exec-2] c.e.c.services.grpcs.AIGrpcClient        : Calling AI service via gRPC to analyze CV: Untitled CV
2025-11-08T20:55:09.745+07:00 DEBUG 20712 --- [cv-service] [nio-8084-exec-2] c.e.cvservice.services.apis.CVService    : Analysis completed for CV title=Untitled CV suggestionsCount=0