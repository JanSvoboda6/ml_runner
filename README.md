[![Gradle tests](https://github.com/JanSvoboda6/ml_runner/actions/workflows/gradle.yml/badge.svg)](https://github.com/JanSvoboda6/ml_runner/actions/workflows/gradle.yml)
[![React tests](https://github.com/JanSvoboda6/ml_runner/actions/workflows/react.yml/badge.svg)](https://github.com/JanSvoboda6/ml_runner/actions/workflows/react.yml)
[![Publish images to Docker Hub](https://github.com/JanSvoboda6/ml_runner/actions/workflows/docker.yml/badge.svg)](https://github.com/JanSvoboda6/ml_runner/actions/workflows/docker.yml)

**This is a repository of Machine Learning Runner application.**

**Demos and a production configuration (in Czech) can be found [here](https://drive.google.com/drive/folders/17BWR5fbwnzHdIt4Ofe438MTBGxZYUjsl).**

Development configuration
===
Prerequisite: Installed [Docker](https://docs.docker.com/get-docker/)
1.  Open terminal
2. `git clone https://github.com/JanSvoboda6/ml_runner.git`
4. `cd ml_runner/react/ml_runner`
5. `npm install`
6. `npm start`
7.  Open another terminal window
8. `cd ml_runner/java/web`
9. `./gradlew run` <br />

Notes: </br>
Web application is running on port `3000`. </br>
API is running on port `8088`. </br>
Database could be accessed via browser on `localhost:8080/console/`.</br>
The in-memory database is used, all data is deleted after application is stopped. <br />
Before restart please remove all the containers that have been created in the previous run.

There is a possibility to run a whole application inside containers. </br>
It could be used in the future if more devs are working on the code.
