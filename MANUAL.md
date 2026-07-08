# 기본 설정
본 프로젝트는 **Competitive Companion**을 사용해 테스트 케이스를 가지고 옵니다.
따라서 **Chrome, Edge** 확장 프로그램에서 설치를 요구합니다.

<img src="./manual img/1.png">

이후 스프링 컨트롤러와 통신 포트를 같게 설정해야 하기에 설정에서 포트 번호를 원하는 것으로 설정합니다.
<img src="./manual img/2.png">

여기서 설정한 포트 번호를 **src/main/resources/application.yml**를 만든 다음 port: 10042로 설정해줍니다.
<img src="./manual img/3.png">

---
# 실행 및 빌드
프로젝트가 스프링 환경에서 작동하기 때문에 pom.xml을 Maven에 추가해야 합니다.
<img src="./manual img/4.png">

Application에 있는 main을 실행합니다.
<img src="./manual img/5.png">

크롤링을 하기 전에 파싱 형식을 atCoder로 설정해줍니다.
<img src="./manual img/7.png">

이후에 AtCoder 사이트에서 확장을 실행합니다.
<img src="./manual img/6.png">

최종 결과 zip 파일이 만들어지며 AtCoder에서 던져주는 테스트 케이스들과 문제에 대한 정보 및 html이 저장됩니다.