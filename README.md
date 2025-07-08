<img width="905" alt="image" src="https://github.com/user-attachments/assets/97162224-c49a-46f7-bcaf-9c1f12fc7d02" />

개발 사양 <br>
mac silicon <br>
Tool : [Docker](https://www.docker.com/), [InteliJ](https://www.jetbrains.com/idea/) <br>

사용 LLM model : [ollama llama3.1:8b ](https://ollama.com/)

설치 방법
1. docker에 올라마 설치 명령어 <br>
docker pull ollama/ollama<br>
docker run -d --name ollama-container -p 11434:11434 ollama/ollama <br>
--- arm 으로 실행하기 --- <br>
docker run -d --name ollama-container -p 11434:11434 --platform linux/amd64 ollama/ollama <br>

> docker ps -a <br> docker ps 로 실행중인거 확인 <br>
> 작동여부 curl 명령어로 확인 <br> curl http://localhost:11434 <br><br>
> 모델 불러오기<br>
> curl -X POST http://localhost:11434/api/pull -d '{"name": "llama3"}' <- llama3로 하면 llama3:latest 로 해석되어 최신버전인 llama3.1:8b 불러옴<br><br>
> 터미널에서 간단한 대화 방법 <br>
> curl -X POST http://localhost:11434/api/generate -d '{
  "model": "llama3.1:latest",
  "prompt": "오늘 날씨 어때?",
  "stream": false
}'
}'
