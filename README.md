# CopyClient

스마트폰 -> PC 인증번호 전송용 클라이언트

이 앱은 서버 프로그램과 함께 사용해야 합니다.  
서버 프로그램 다운로드 : https://github.com/Hot6ix/CopyServer

- 주의 사항
  - 연결 전 Server가 실행되어 있어야 합니다.
  - 공공에서 제공하는 네트워크 접속 후 사용하는 것을 권장하지 않습니다.
  - 추가 설정 시 외부에서도 접속은 가능하지만 인증번호는 노출 시 위험한 정보이므로 같은 네트워크 안에서만 사용하는 것을 권장합니다.
  - 개발자는 위 주의 사항을 숙지했음에도 외부 접속을 통한 서버 연결 후 발생하는 피해는 책임지지 않습니다.

- 사용 방법
  - 앱 실행 후 IP를 Server IP로 설정해주세요.
  - Server Port가 다를 시 Port를 변경해주세요.
  - Server에서 비밀번호 설정 시 비밀번호를 입력해주세요.
  - 필터는 간단하게 구현되어 있습니다. 더 정확한 필터링을 원하시면 변경해주세요.
  - 연결하기를 눌러 서버와 연결해주세요.
  
- Server의 IP를 확인하는 방법
  - 인증번호 전송용 서버 프로그램 실행 후 시스템 트레이에서 오른쪽 클릭 시 내부 IP를 확인할 수 있습니다.
