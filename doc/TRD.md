# Technical Requirements Document (TRD)
**프로젝트명**: ImageSearch

## 1. 아키텍처 및 핵심 패턴
*   **설계 패턴**: **Clean Architecture** (Presentation - Domain - Data 레이어 분리)
*   **UI 패턴**: **MVI (Model-View-Intent)** 또는 **MVVM** 기반 단방향 데이터 흐름(UDF) 구성
*   **비동기 처리**: `Kotlin Coroutines` + `Flow` (UI State는 `StateFlow`로 노출)
*   **의존성 주입 (DI)**: `Dagger Hilt` (엔터프라이즈 환경 표준)
*   **에러 핸들링**: `Result` 래퍼 클래스 또는 `Sealed Interface`를 사용한 안정적인 예외 공통화 처리

## 2. 모듈 및 패키지 구조 (Package Structure)
단일 모듈 내에서 패키지형 클린 아키텍처로 구성하거나 멀티 모듈로 분리.
*   `data`: Naver API Remote DataSource, Room Local DataSource, Repository Impl
*   `domain`: Entity (ImageItem), Repository Interface, UseCases (비즈니스 로직 캡슐화)
*   `presentation`: UI Components, ViewModel (`StateFlow`로 상태 발행), UI State/Intent

## 3. 기술 스택 및 주요 라이브러리
| 분류 | 기술/라이브러리 | 핵심 용도 |
| :--- | :--- | :--- |
| **Language & SDK** | Kotlin, Min SDK 29, Target 34 | 최신 안드로이드 개발 표준 |
| **Network** | Retrofit2, OkHttp3 (Interceptor) | Naver API 연동 및 Auth Header (Client-Id, Client-Secret) 삽입 |
| **Serialization** | Kotlinx Serialization | JSON 응답 파싱 |
| **List & Pagination** | Jetpack Paging 3 (+ RemoteMediator) | 무한 스크롤(50개씩), DB 캐싱 연동을 통한 오프라인 지원 |
| **Local Proxy/DB** | Room, DataStore (Preferences) | 북마크 저장, Paging 3 캐시키 관리 |
| **Image Loading** | Coil (또는 Glide) | 메모리 및 디스크 캐시, OOM 방지, 비동기 렌더링 |
| **UI Framework** | Jetpack Compose (권장) 또는 XML | Material 3 디자인, 뷰어 핀치 줌 구현 유리 (Modifier.transformable) |

## 4. 도메인별 세부 기술 구현 전략

### 4.1 메인 화면 & Paging 3
*   `Naver Image Search API`는 `display=50`과 `start` 파라미터를 사용해 페이징 처리함
*   `PagingSource` 또는 `RemoteMediator`(오프라인 지원 시)를 구현하여 PagingData Flow 생성
*   Pull-to-Refresh는 Compose의 `PullRefresh` API 역량 활용 (`PagingDataAdapter.refresh()` 호출)

### 4.2 검색 (로컬 필터링) 화면
*   Flow 기반 `debounce(1000L)` 오퍼레이터 적용 후 `filter` 연산
*   `distinctUntilChanged()`를 통해 동일한 검색어의 중복 수행 방지
*   Target 데이터는 메인 탭에 로딩된 현재 상태의 메모리 리스트(`Snapshot`)를 기반으로 필터 수행

### 4.3 뷰어 화면 (고도화 포인트)
*   **구조**: 세로 무한 스크롤 형태 (Compose `LazyColumn` 또는 `RecyclerView`)
*   **데이터 조립**: 
    1.  전달받은 `targetImage` 1개를 리스트 최상단(index=0)에 삽입
    2.  `domain` 계층에서 기존 데이터 풀을 셔플(`shuffled()`)하여 30개를 추출 후 병합
*   **Gesture (줌 & 팬)**: Compose 기준 `Modifier.transformable`, `Modifier.pointerInput`을 결합하거나 외부 Zoomable Image 라이브러리 활용 적용하여 확대/축소/스크롤 충돌 방지

## 5. 성능 및 정책 관리
*   **이미지 최적화**: 디코딩 시 리사이징 전략(Downsampling) 적용, 미노출 썸네일에 대한 메모리 해제 로직 마련.
*   **구성 변경 (Configuration Change)**: 상태 유실 방지를 위해 ViewModel에 캐싱된 StateFlow를 사용하여 화면 회전 시 매끄럽게 복귀.
*   **테스트**: ViewModel 및 UseCase의 주요 로직은 `JUnit5`와 `MockK`를 활용해 단위 테스트 작성(필수 지향점).
