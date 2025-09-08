TARGET=target/research-assistant-0.0.1-SNAPSHOT.jar

build:
	mvn clean package -DskipTests

run: build
	java -jar $(TARGET)

request_process:
	curl -X POST http://localhost:8080/api/research/process \
		-H "Content-Type: application/json" \
		-d "{\"operation\": \"summarize\", \"content\": \"Albert Einstein was a theoretical physicist known for the theory of relativity. His work revolutionized physics and changed our understanding of space and time.\"}"

agent:
	mvn exec:java \
		-Dexec.mainClass="com.google.adk.web.AdkWebServer" \
		-Dexec.args="--adk.agents.source-dir=src/main/java" \
		-Dexec.classpathScope="compile"
