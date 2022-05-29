## 1단계 - 지하철역 인수 테스트 작성

### 기능 요구 사항

- [x] 지하철 역 목록 조회 인수 테스트 작성
  * Given : 지하철 역들이 등록 되어 있다.
  * When : 사용자는 지하철 역의 목록 조회를 요청한다.
  * Then : 사용자는 등록되어 있는 지하철 역 전체 정보를 응답 받는다.

- [x] 지하철역 삭제 인수 테스트 작성
  * Given : 지하철 역들이 등록되어 있다.
  * When : 사용자는 등록된 지하철 역 중 하나의 삭제를 지하철역 ID 로 요청한다
  * Then : 삭제 되었다는 응답을 받는다 (204)
  * Tenn : 목록을 조회에서 지하철역이 삭제된 것을 확인한다.