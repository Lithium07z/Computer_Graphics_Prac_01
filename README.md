# 컴퓨터 그래픽스 실습 01 — 도형, 변환, 카메라

2023년 1학기 컴퓨터 그래픽스 과제를 정리한 Android 앱입니다. Kotlin과 OpenGL ES 3.0으로 기본 도형을 그리고, 모델 변환·투영·카메라 이동·텍스처 매핑을 단계별로 실습합니다.

## 주요 실습

앱 첫 화면에서 각 예제를 선택할 수 있습니다.

| 메뉴 | 실습 내용 |
| --- | --- |
| Drawing Surface | `GLSurfaceView`와 OpenGL 렌더링 화면 구성 |
| Drawing A Triangle / Draw a Square | 정점 데이터와 셰이더로 삼각형·사각형 그리기 |
| Draw a ColorCube / Draw a HexaPyramid | 색상이 있는 정육면체·육각뿔 렌더링 |
| Transformation | 축별 회전, 확대·축소, X/Y/Z 방향 이동 |
| Draw Objects | 여러 도형 배치와 회전 시작·정지 |
| VIEWING | 원근·직교 투영 전환과 카메라 이동·회전 |
| Image Texturing | 상자·기둥·바닥에 이미지 텍스처 적용 |

## 코드 안내

Kotlin 소스는 [`app/src/main/java/kr/ac/hallym/prac01`](app/src/main/java/kr/ac/hallym/prac01)에 있습니다.

- [`MainActivity.kt`](app/src/main/java/kr/ac/hallym/prac01/MainActivity.kt): 예제 선택과 화면 전환
- [`MainGLRenderer.kt`](app/src/main/java/kr/ac/hallym/prac01/MainGLRenderer.kt): 모드별 렌더링, 모델·뷰·투영 행렬 계산
- `Draw*.kt`, `RotateCube.kt`, `ViewObjects.kt`: 예제 화면과 버튼 입력
- `MyTriangle.kt`, `MySquare.kt`, `MyColorCube.kt`, `MyHexaPyramid.kt`, `MyTex*.kt`: 도형의 정점·셰이더·텍스처 처리
- [`app/src/main/assets`](app/src/main/assets): BMP 텍스처 이미지
- [`app/src/main/res/layout`](app/src/main/res/layout): 메뉴와 조작 화면 레이아웃

셰이더 코드는 각 도형 클래스에 문자열로 들어 있습니다. 도형 하나의 구조를 보려면 `MyTriangle.kt`, 전체 렌더링 흐름을 보려면 `MainGLRenderer.kt`부터 읽으면 됩니다.

## 실행 방법

1. 저장소를 복제하고 Android Studio에서 루트 폴더를 엽니다.
2. Android SDK Platform 33을 설치하고 Gradle JDK를 11로 설정한 뒤 동기화를 진행합니다. JDK 기준은 [AGP 7.4 호환성 안내](https://developer.android.com/build/releases/agp-7-4-0-release-notes)를 참고하세요.
3. OpenGL ES 3.0을 지원하는 Android API 24 이상 기기 또는 에뮬레이터에서 `app`을 실행합니다.
4. 첫 화면의 메뉴를 선택하고 화면에 표시된 버튼으로 도형이나 카메라를 조작합니다.

프로젝트에 기록된 빌드 설정은 다음과 같습니다.

| 항목 | 설정 |
| --- | --- |
| Android Gradle Plugin / Gradle Wrapper | 7.4.2 / 7.5 |
| Kotlin | 1.8.0 |
| compileSdk / targetSdk / minSdk | 33 / 33 / 24 |
| Java 호환성 / Kotlin JVM target | 1.8 / 1.8 |

Android SDK와 Gradle 실행용 JDK를 설정한 환경에서는 루트에서 다음 명령으로 디버그 APK를 빌드할 수 있습니다.

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux에서는 `bash gradlew assembleDebug`를 사용합니다. 결과 경로는 `app/build/outputs/apk/debug/app-debug.apk`입니다.

## 참고

학부 수업 당시의 실습 코드를 보관한 저장소입니다. 포함된 테스트는 기본 템플릿 수준이며, 렌더링 결과를 검증하는 자동 테스트는 없습니다. 최신 Android 개발 환경으로의 이전이나 기기별 동작 검증은 별도로 필요합니다.
