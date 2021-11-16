# EC2 크롤러 실행



1. crawler 폴더로 이동

   `/crawler`

2. 가상 환경으로 진입

   `source crawler/bin/activate`

3. 패키지 설치

   `bash install.sh`

   > pip install --upgrade --no-deps --force-reinstall -r requirements.txt
   >
   > 위 명령을 실행하는 스크립트

4. 크롤러 실행

   `nohup python3 price.py &`

   > `ps -ef | grep price.py` 로 확인 가능 
