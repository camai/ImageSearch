# GitHub Issue List (작업 목록)

본 파일은 프로젝트 수행을 위한 이슈 트래킹 리스트입니다.
GitHub CLI가 설치되어 있다면 아래 스크립트를 복사하여 터미널에 붙여넣으면 저장소에 일괄로 Issue가 생성됩니다.

```bash
# [1단계: 기반 작업 및 프로젝트 세팅]
gh issue create --title "[Setting] 프로젝트 초기 세팅 및 의존성 구성" --body "1. Min SDK 29, Target/Compile 34 설정<br>2. Hilt, Coroutine, Room, Retrofit, Paging3, Compose 의존성 추가<br>3. ktlint 및 buildSrc (또는 Version Catalog) 구성" --label "setting"
gh issue create --title "[Core] Clean Architecture 패키지/모듈 구조 수립" --body "1. Data, Domain, Presentation 레이어 분리<br>2. Base 클래스 (BaseViewModel, Resource/Result Wrapper) 생성" --label "architecture"

# [2단계: 네트워크 및 로컬 데이터 레이어]
gh issue create --title "[Data] Naver Image Search API 연동 (Retrofit)" --body "1. DTO 및 Network Service 인터페이스 작성<br>2. OkHttp Interceptor로 Client ID/Secret 자동 주입<br>3. Exception/Error Handling 로직 추가" --label "data"
gh issue create --title "[Data/Domain] 북마크 저장을 위한 Room Database 설계" --body "1. ImageEntity, BookmarkDao 작성<br>2. LocalDataSource 및 Repository에 반영" --label "data"

# [3단계: 메인 및 북마크 탭 구현]
gh issue create --title "[UI] 메인 화면 레이아웃 및 하단 네비게이션(BottomNav) 구현" --body "1. TopAppBar (타이틀, 검색 아이콘) 구성<br>2. Main Tab, Bookmark Tab 프래그먼트/컴포저블 네비게이션 연결" --label "ui"
gh issue create --title "[Feature] 메인 탭 Paging3 무한 스크롤 및 리스트 구현" --body "1. Naver PagingSource 작성<br>2. 50개 단위 페이징 로드 및 화면 바인딩<br>3. Pull-to-Refresh (당겨서 새로고침) 적용<br>4. Image Cell (썸네일, Title) UI 작성" --label "feature","main"
gh issue create --title "[Feature] 북마크 기능 토글 및 북마크 탭 리스트 노출" --body "1. 롱클릭 혹은 아이콘 터치로 북마크 추가/제거<br>2. Room 연동을 통한 북마크 탭 리스트 실시간 반영<br>3. 북마크 다중/단일 삭제 기능 추가" --label "feature","bookmark"

# [4단계: 검색 및 뷰어 화면 구현]
gh issue create --title "[Feature] 로컬 검색 화면 및 Debounce(1초) 로직 구현" --body "1. 검색 TopBar 텍스트 입력 UI<br>2. StateFlow debounce(1000L) 적용<br>3. 기 로드된 PagingData의 Snapshot 기준 Title 필터링 구현<br>4. 빈 화면(Empty) 및 에러 상태 레이아웃" --label "feature","search"
gh issue create --title "[Feature] 세로 스크롤 방식의 콘텐츠 뷰어 화면 구현" --body "1. 선택된 이미지를 index 0으로 위치<br>2. 나머지 30개의 랜덤 이미지를 하단에 렌더링 (총 31장)<br>3. 핀치 줌(Pinch Zoom) 및 팬(Pan/Drag) 제스처 기능 추가" --label "feature","viewer"

# [5단계: 최적화 및 안정화]
gh issue create --title "[Optimize] 메모리/OOM 최적화 및 오프라인 페이징 (선택)" --body "1. Coil/Glide 이미지 캐싱 최적화 (Downsampling)<br>2. 태블릿/가로모드 대응 및 상태 유실 방지<br>3. Paging3 RemoteMediator 연동 검토" --label "optimize"
gh issue create --title "[Test & Doc] 단위 테스트 작성 및 README 문서화" --body "1. 비즈니스 로직(UseCase, ViewModel) JUnit 테스트<br>2. 설계 사상, 트러블슈팅, 라이브러리 사용처 README.md 정리" --label "documentation"
```
