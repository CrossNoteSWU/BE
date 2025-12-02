
“당신이 경험해 보지 못한 세계는 어떤 곳일까요?”
# 비전공자를 위한 융합 지식 큐레이션 플랫폼, CrossNote

**2025 SWU 멋쟁이사자처럼 13기 - 슈멋사 프로젝트 1팀 백엔드 레포지토리**

### Background
> 현대 사회는 한 분야의 전문성을 넘어, 다양한 분야를 넘나드는 융합적 사고와 폭넓은 이해를 요구하고 있습니다. 정보의 양은 폭발적으로 증가했지만, 오히려 개인은 소셜 미디어와 추천 알고리즘의 '필터 버블'에 갇혀 편향된 지식만을 소비하기 쉬운 환경에 놓여있습니다.
> 이로 인해 자신이 경험해 보지 못한 미지의 세계에 대한 호기심을 잃고, 생각의 폭을 넓힐 기회를 놓치게 됩니다.

### Service Overview
> '크로스노트'는 이러한 시대적 흐름에 맞춰 지식의 경계를 허물고 새로운 관점을 제공하고자 합니다. 단순히 특정 분야의 지식을 전달하는 것을 넘어, 서로 다른 지식들을 자유롭게 연결하고 넘나들며 사용자만의 독창적인 지식 체계(雜學多識)를 쌓도록 돕습니다. 
> 이를 통해 사용자가 존재조차 인식하지 못했던 새로운 세계를 우연히 발견하고, 지적 탐험의 과정을 통해 사고의 깊이를 더하고 자신의 세계를 확장해 나가는 데 이 프로젝트의 의의가 있습니다.

### Tech Stacks
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=coffeescript&logoColor=white">  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> 


### Team Member
  <table border="" cellspacing="0" cellpadding="0" width="100%">
  <tr width="100%">
  <td align="center">김하나</a></td>
  <td align="center">양보윤</a></td>
  <td align="center">임제영</a></td>
  </tr>
  <tr width="100%">
  <td  align="center"><img src="https://github.com/user-attachments/assets/95711361-1b07-47ee-b902-20f12113b057" alt="befbedf87e51f5b02aac8b882ada60fd-sticker"  border="0" width="90px"/></a></td>
  <td  align="center"><img src="https://github.com/user-attachments/assets/fa9a7e35-834a-4f9e-af59-a5762dbe0a4d" border="0" width="90px"/></a></td>
  <td  align="center"><a href="https://imgbb.com/"><img src="https://github.com/user-attachments/assets/a4c534bc-2c06-4909-8106-9e5d4b4d6067" alt="befbedf87e51f5b02aac8b882ada60fd-sticker"  border="0" width="90px"/></a></td>
  </tr>
      <tr width="100%">
        <td  align="center"><p>회원가입/로그인</p><p>온보딩</p><p>큐레이션</p></td>
        <td  align="center"><p>홈</p><p>밸런스게임</p><p>마이페이지</p></td>
        <td  align="center"><p>칼럼</p>큐앤에이<p></p><p>알림</p></td>
     </tr>
  </table>

### Deployment
**1. ec2 환경 구성**
>  Install dependencies
> ```
> sudo apt update
> sudo apt install -y git openjdk-17-jdk
> ```

> project clone & build
> ```
> git clone https://github.com/CrossNoteSWU/BE.git
> ./gradlew clean build -x test
> ```
**2. Docker & Redis**
> Install Docker 
> ```
> curl -fsSL https://get.docker.com -o get-docker.sh
> sudo sh get-docker.sh
> ```

> Run Redis container
> ```
> docker pull redis:latest
> docker run -d \
>   --name redis-crossnote \
>   -p 6379:6379 \
>   redis
> ```

**3. Spring Boot 실행 유지**
> ```
> sudo systemctl restart crossnote
> ```

**4. RDS(MySQL) 연결 확인**
> Connection check
> ```
> telnet <RDS-ENDPOINT> 3306
> mysql -h <RDS-ENDPOINT> -u <USER> -p
> ```

**5. Nginx + HTTPS**
> Configure Nginx site
> ```
> sudo nano /etc/nginx/site-available/https://cross-note.com
> ```
> Reload nginx
> ```
> sudo nginx -t
> sudo systemctl reload nginx
> ```

**6. S3 설정**
> IAM 권한: `s3:PutObject` 필요 <br>
> Test upload
> ```
> aws s3 ls
> ```

**7. CI/CD**
> GitHub Action
> ```
> chmod +x deploy.yml
> ```


### Directory Structure

```
📂 crossnote
└─ 📦 src
   ├─ 📂 main
   │  ├─ 📂 generated
   │  │  └─ 📂 com
   │  │     └─ 📂 swulion
   │  │        └─ 📂 crossnote
   │  │           └─ 📂 entity
   │  │              ├─ 📂 balance
   │  │              │  ├─ 📜 QBalanceOption.java
   │  │              │  ├─ 📜 QBalanceQuiz.java
   │  │              │  └─ 📜 QUserBalanceChoice.java
   │  │              ├─ 📂 Column
   │  │              │  ├─ 📜 QColumnCategory.java
   │  │              │  ├─ 📜 QColumnComment.java
   │  │              │  └─ 📜 QColumnEntity.java
   │  │              ├─ 📂 Curation
   │  │              │  ├─ 📜 QCuration.java
   │  │              │  ├─ 📜 QLike.java
   │  │              │  └─ 📜 QScrap.java
   │  │              ├─ 📂 QA
   │  │              │  ├─ 📜 QAnswer.java
   │  │              │  ├─ 📜 QQuestion.java
   │  │              │  └─ 📜 QQuestionCategory.java
   │  │              ├─ 📜 QBaseTimeEntity.java
   │  │              ├─ 📜 QCategory.java
   │  │              ├─ 📜 QFollow.java
   │  │              ├─ 📜 QNotification.java
   │  │              ├─ 📜 QUser.java
   │  │              ├─ 📜 QUserCategoryPreference.java
   │  │              └─ 📜 QUserCategoryPreferenceId.java
   │  ├─ 📂 java
   │  │  └─ 📂 com
   │  │     └─ 📂 swulion
   │  │        └─ 📂 crossnote
   │  │           ├─ 📂 client
   │  │           │  ├─ 📜 CurationSourceClient.java
   │  │           │  ├─ 📜 DbpiaClient.java
   │  │           │  ├─ 📜 KciClient.java
   │  │           │  ├─ 📜 NaverNewsClient.java
   │  │           │  ├─ 📜 NlBookClient.java
   │  │           │  └─ 📜 YoutubeClient.java
   │  │           ├─ 📂 config
   │  │           │  ├─ 📜 ApiKeys.java
   │  │           │  ├─ 📜 CorsConfig.java
   │  │           │  ├─ 📜 GenerationConfig.java
   │  │           │  ├─ 📜 QueryDslConfig.java
   │  │           │  ├─ 📜 RedisConfig.java
   │  │           │  ├─ 📜 RestTemplateConfig.java
   │  │           │  └─ 📜 SecurityConfig.java
   │  │           ├─ 📂 controller
   │  │           │  ├─ 📜 AnswerController.java
   │  │           │  ├─ 📜 AuthController.java
   │  │           │  ├─ 📜 BalanceGameController.java
   │  │           │  ├─ 📜 ColumnCommentController.java
   │  │           │  ├─ 📜 ColumnController.java
   │  │           │  ├─ 📜 CurationController.java
   │  │           │  ├─ 📜 FollowController.java
   │  │           │  ├─ 📜 HomeController.java
   │  │           │  ├─ 📜 MyPageController.java
   │  │           │  ├─ 📜 NotificationController.java
   │  │           │  ├─ 📜 OnboardingController.java
   │  │           │  ├─ 📜 ProfileController.java
   │  │           │  ├─ 📜 QuestionController.java
   │  │           │  └─ 📜 UserController.java
   │  │           ├─ 📂 dto
   │  │           │  ├─ 📂 balance
   │  │           │  │  ├─ 📜 AnswerResultDto.java
   │  │           │  │  ├─ 📜 BalanceHomeDto.java
   │  │           │  │  ├─ 📜 BalanceQuizDto.java
   │  │           │  │  ├─ 📜 CurationLinkDto.java
   │  │           │  │  ├─ 📜 OptionDto.java
   │  │           │  │  └─ 📜 SubmitAnswerRequest.java
   │  │           │  ├─ 📂 Column
   │  │           │  │  ├─ 📜 ColumnCommentCreateDto.java
   │  │           │  │  ├─ 📜 ColumnCommentGetDto.java
   │  │           │  │  ├─ 📜 ColumnCommentRequestDto.java
   │  │           │  │  ├─ 📜 ColumnCommentResponseDto.java
   │  │           │  │  ├─ 📜 ColumnDetailGetDto.java
   │  │           │  │  ├─ 📜 ColumnDetailResponseDto.java
   │  │           │  │  ├─ 📜 ColumnReadResponseDto.java
   │  │           │  │  ├─ 📜 ColumnRequestDto.java
   │  │           │  │  └─ 📜 ColumnSearchDto.java
   │  │           │  ├─ 📂 Curation
   │  │           │  │  ├─ 📜 AiGeneratedContentDto.java
   │  │           │  │  ├─ 📜 AiJsonResponseDto.java
   │  │           │  │  ├─ 📜 CurationDetailDto.java
   │  │           │  │  ├─ 📜 CurationFeedDto.java
   │  │           │  │  ├─ 📜 CurationSourceDto.java
   │  │           │  │  ├─ 📜 CurationToggleResponseDto.java
   │  │           │  │  ├─ 📜 DbpiaResponseDto.java
   │  │           │  │  ├─ 📜 GeminiRequestDto.java
   │  │           │  │  ├─ 📜 GeminiResponseDto.java
   │  │           │  │  ├─ 📜 KciResponseDto.java
   │  │           │  │  ├─ 📜 NaverNewsResponseDto.java
   │  │           │  │  ├─ 📜 NlBookResponseDto.java
   │  │           │  │  └─ 📜 YoutubeResponseDto.java
   │  │           │  ├─ 📂 Follow
   │  │           │  │  ├─ 📜 FollowListResponseDto.java
   │  │           │  │  ├─ 📜 FollowStatusResponseDto.java
   │  │           │  │  └─ 📜 FollowUserSummaryDto.java
   │  │           │  ├─ 📂 Login
   │  │           │  │  ├─ 📜 LocalLoginRequestDto.java
   │  │           │  │  ├─ 📜 LocalSignUpRequestDto.java
   │  │           │  │  └─ 📜 LoginResponseDto.java
   │  │           │  ├─ 📂 MyPage
   │  │           │  │  ├─ 📜 KnowledgeReportResponseDto.java
   │  │           │  │  ├─ 📜 MyProfileResponseDto.java
   │  │           │  │  ├─ 📜 MyQnAResponseDto.java
   │  │           │  │  ├─ 📜 ScrappedCurationDto.java
   │  │           │  │  ├─ 📜 UpdateCurationLevelRequestDto.java
   │  │           │  │  ├─ 📜 UpdateExpertiseRequestDto.java
   │  │           │  │  ├─ 📜 UpdateInterestsRequestDto.java
   │  │           │  │  ├─ 📜 UpdateProfileRequestDto.java
   │  │           │  │  └─ 📜 UserPreferencesResponseDto.java
   │  │           │  ├─ 📂 Onboarding
   │  │           │  │  ├─ 📜 OnboardingCurationRequestDto.java
   │  │           │  │  ├─ 📜 OnboardingDetailResponseDto.java
   │  │           │  │  ├─ 📜 OnboardingExpertiseRequestDto.java
   │  │           │  │  ├─ 📜 OnboardingGoogleRequestDto.java
   │  │           │  │  ├─ 📜 OnboardingInterestRequestDto.java
   │  │           │  │  └─ 📜 OnboardingResponseDto.java
   │  │           │  ├─ 📂 Question
   │  │           │  │  ├─ 📜 AnswerCreateDto.java
   │  │           │  │  ├─ 📜 AnswerResponseDto.java
   │  │           │  │  ├─ 📜 AnswerUpdateDto.java
   │  │           │  │  ├─ 📜 QuestionDetailGetDto.java
   │  │           │  │  ├─ 📜 QuestionRequestDto.java
   │  │           │  │  ├─ 📜 QuestionResponseDto.java
   │  │           │  │  ├─ 📜 QuestionSearchDto.java
   │  │           │  │  └─ 📜 QuestionUpdateDto.java
   │  │           │  ├─ 📜 NotificationGetDto.java
   │  │           │  ├─ 📜 RefreshTokenRequestDto.java
   │  │           │  └─ 📜 UserProfileFullResponseDto.java
   │  │           ├─ 📂 entity
   │  │           │  ├─ 📂 balance
   │  │           │  │  ├─ 📜 BalanceOption.java
   │  │           │  │  ├─ 📜 BalanceQuiz.java
   │  │           │  │  ├─ 📜 QuizType.java
   │  │           │  │  └─ 📜 UserBalanceChoice.java
   │  │           │  ├─ 📂 Column
   │  │           │  │  ├─ 📜 ColumnCategory.java
   │  │           │  │  ├─ 📜 ColumnComment.java
   │  │           │  │  └─ 📜 ColumnEntity.java
   │  │           │  ├─ 📂 Curation
   │  │           │  │  ├─ 📜 Curation.java
   │  │           │  │  ├─ 📜 CurationLevel.java
   │  │           │  │  ├─ 📜 CurationType.java
   │  │           │  │  ├─ 📜 Like.java
   │  │           │  │  ├─ 📜 Scrap.java
   │  │           │  │  └─ 📜 ScrapTargetType.java
   │  │           │  ├─ 📂 QA
   │  │           │  │  ├─ 📜 Answer.java
   │  │           │  │  ├─ 📜 Question.java
   │  │           │  │  └─ 📜 QuestionCategory.java
   │  │           │  ├─ 📜 BaseTimeEntity.java
   │  │           │  ├─ 📜 Category.java
   │  │           │  ├─ 📜 Follow.java
   │  │           │  ├─ 📜 Gender.java
   │  │           │  ├─ 📜 LoginType.java
   │  │           │  ├─ 📜 Notification.java
   │  │           │  ├─ 📜 NotificationType.java
   │  │           │  ├─ 📜 PreferenceType.java
   │  │           │  ├─ 📜 User.java
   │  │           │  ├─ 📜 UserCategoryPreference.java
   │  │           │  └─ 📜 UserCategoryPreferenceId.java
   │  │           ├─ 📂 exception
   │  │           │  ├─ 📜 GlobalExceptionHandler.java
   │  │           │  └─ 📜 SseExceptionHandler.java
   │  │           ├─ 📂 jwt
   │  │           │  ├─ 📜 JwtAuthenticationFilter.java
   │  │           │  ├─ 📜 JwtBlacklistService.java
   │  │           │  └─ 📜 JwtTokenProvider.java
   │  │           ├─ 📂 oauth
   │  │           │  ├─ 📜 CustomOAuth2User.java
   │  │           │  ├─ 📜 OAuth2AuthenticationSuccessHandler.java
   │  │           │  ├─ 📜 OAuth2LoginSuccessHandler.java
   │  │           │  └─ 📜 OAuthAttributes.java
   │  │           ├─ 📂 repository
   │  │           │  ├─ 📂 balance
   │  │           │  │  └─ 📜 UserBalanceChoiceRepository.java
   │  │           │  ├─ 📂 Column
   │  │           │  │  ├─ 📜 ColumnCategoryRepository.java
   │  │           │  │  ├─ 📜 ColumnCommentRepository.java
   │  │           │  │  ├─ 📜 ColumnRepository.java
   │  │           │  │  └─ 📜 ColumnRepositoryImpl.java
   │  │           │  ├─ 📂 Curation
   │  │           │  │  ├─ 📜 CurationRepository.java
   │  │           │  │  ├─ 📜 CurationRepositoryCustom.java
   │  │           │  │  ├─ 📜 CurationRepositoryImpl.java
   │  │           │  │  ├─ 📜 LikeRepository.java
   │  │           │  │  └─ 📜 ScrapRepository.java
   │  │           │  ├─ 📂 QnA
   │  │           │  │  ├─ 📜 AnswerRepository.java
   │  │           │  │  ├─ 📜 QuestionCategoryRepository.java
   │  │           │  │  ├─ 📜 QuestionRepository.java
   │  │           │  │  └─ 📜 QuestionRepositoryImpl.java
   │  │           │  ├─ 📜 BalanceOptionRepository.java
   │  │           │  ├─ 📜 BalanceQuizRepository.java
   │  │           │  ├─ 📜 CategoryRepository.java
   │  │           │  ├─ 📜 FollowRepository.java
   │  │           │  ├─ 📜 NotificationRepository.java
   │  │           │  ├─ 📜 SseEmitterRepository.java
   │  │           │  ├─ 📜 UserCategoryPreferenceRepository.java
   │  │           │  └─ 📜 UserRepository.java
   │  │           ├─ 📂 service
   │  │           │  ├─ 📜 AnswerService.java
   │  │           │  ├─ 📜 BalanceGameService.java
   │  │           │  ├─ 📜 BestColumnCurationService.java
   │  │           │  ├─ 📜 ColumnCommentService.java
   │  │           │  ├─ 📜 ColumnService.java
   │  │           │  ├─ 📜 CurationSelectorService.java
   │  │           │  ├─ 📜 CurationService.java
   │  │           │  ├─ 📜 CustomUserDetails.java
   │  │           │  ├─ 📜 CustomUserDetailsService.java
   │  │           │  ├─ 📜 FollowService.java
   │  │           │  ├─ 📜 GeminiService.java
   │  │           │  ├─ 📜 KnowledgeReportService.java
   │  │           │  ├─ 📜 MyColumnService.java
   │  │           │  ├─ 📜 MyPageService.java
   │  │           │  ├─ 📜 MyQnAService.java
   │  │           │  ├─ 📜 NotificationService.java
   │  │           │  ├─ 📜 OnboardingService.java
   │  │           │  ├─ 📜 ProfileService.java
   │  │           │  ├─ 📜 QuestionService.java
   │  │           │  ├─ 📜 ScrappedCurationService.java
   │  │           │  ├─ 📜 TerminologyService.java
   │  │           │  └─ 📜 UserService.java
   │  │           └─ 📜 CrossnoteApplication.java
   │  └─ 📂 resources
   │     ├─ 📂 terminology
   │     │  ├─ 📜 10_language.txt
   │     │  ├─ 📜 11_phychology.txt
   │     │  ├─ 📜 12_math.txt
   │     │  ├─ 📜 13_physics.txt
   │     │  ├─ 📜 14_chemistry.txt
   │     │  ├─ 📜 15_biology.txt
   │     │  ├─ 📜 16_medical.txt
   │     │  ├─ 📜 17_it.txt
   │     │  ├─ 📜 18_ai.txt
   │     │  ├─ 📜 19_electronics.txt
   │     │  ├─ 📜 20_mechanical.txt
   │     │  ├─ 📜 21_ie.txt
   │     │  ├─ 📜 22_economics.txt
   │     │  ├─ 📜 23_business.txt
   │     │  ├─ 📜 24_marketing.txt
   │     │  ├─ 📜 25_art.txt
   │     │  ├─ 📜 26_music.txt
   │     │  ├─ 📜 27_literature.txt
   │     │  ├─ 📜 28_uiux.txt
   │     │  ├─ 📜 29_architecture.txt
   │     │  ├─ 📜 30_film.txt
   │     │  ├─ 📜 31_health.txt
   │     │  ├─ 📜 32_sports.txt
   │     │  ├─ 📜 33_travel.txt
   │     │  ├─ 📜 34_lifestyle.txt
   │     │  ├─ 📜 35_environment.txt
   │     │  ├─ 📜 7_philosophy.txt
   │     │  ├─ 📜 8_history.txt
   │     │  └─ 📜 9_sociology.txt
   │     ├─ 📜 .env
   │     ├─ 📜 application.properties
   │     └─ 📜 data.sql
   └─ 📂 test
      └─ 📂 java
         └─ 📂 com
            └─ 📂 swulion
               └─ 📂 crossnote
                  ├─ 📂 service
                  │  ├─ 📜 FollowServiceTest.java
                  │  ├─ 📜 OnboardingServiceTest.java
                  │  └─ 📜 TerminologyServiceTest.java
                  └─ 📜 CrossnoteApplicationTests.java
