<div align="center">

  <img src="assets/logo.png" alt="logo" width="200" height="auto" />
  <h1>IssueCrush</h1>

  <p>
    The backend for IssueCrush.
  </p>


<!-- Badges -->
<!--
<p>
  <a href="">
    <img src="https://img.shields.io/github/last-commit/Louis3797/awesome-readme-template" alt="last update" />
  </a>
  <a href="https://github.com/Louis3797/awesome-readme-template/stargazers">
    <img src="https://img.shields.io/github/stars/Louis3797/awesome-readme-template" alt="stars" />
  </a>
  <a href="https://github.com/Louis3797/awesome-readme-template/issues/">
    <img src="https://img.shields.io/github/issues/Louis3797/awesome-readme-template" alt="open issues" />
  </a>
  <a href="https://github.com/Louis3797/awesome-readme-template/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/Louis3797/awesome-readme-template.svg" alt="license" />
  </a>
</p> -->

<h4>
    <a href="https://github.com/Louis3797/awesome-readme-template/">View Demo</a>
  <span> · </span>
    <a href="https://github.com/Louis3797/awesome-readme-template">View Frontend (more detailed)</a>
  </h4>
</div>

<br />

<!-- Table of Contents -->
# :notebook_with_decorative_cover: Table of Contents

- [About the Project](#star2-about-the-project)
    * [Tech Stack](#space_invader-tech-stack)
    * [Environment Variables](#key-environment-variables)
- [Getting Started](#toolbox-getting-started)
    * [Run Locally](#running-run-locally)
    * [Deployment](#triangular_flag_on_post-deployment)
- [Roadmap](#compass-roadmap)
- [License](#warning-license)


<!-- About the Project -->
## :star2: About the Project
<div>
<p>Streamline your issue tracking and resolution process with IssueCrush. Collaborate seamlessly, manage tickets effortlessly, and deliver exceptional support.</p>
</div>


<!-- TechStack -->
### :space_invader: Tech Stack

<ul>
  <li><a href="https://spring.io/projects/spring-boot/">Spring Boot</a></li>
  <li><a href="https://www.postgresql.org/">PostgreSQL</a></li>
  <li><a href="https://www.docker.com/">Docker</a></li>
</ul>

<!-- Env Variables -->
### :key: Environment Variables

To run this project, you will need to configure the application.yml files.


<!-- Getting Started -->
## 	:toolbox: Getting Started

<!-- Run Locally -->
### :running: Run Locally

Clone the project

```bash
git clone https://github.com/JonathanD01/issuecrush-backend.git
```

Go to the project directory

```bash
cd my-project
```

Build jar

````bash
./mvn clean install
````

Run jar
```bash
# Run the jar from target folder. Jar name may differ due to version mismatch.
java -jar target/issuecrush-0.0.1-SNAPSHOT.jar 
```

<!-- Deployment -->
### :triangular_flag_on_post: Deployment
To deploy, you will need to configure the docker-compose.yml.

<!-- Build local docker image -->
#### Build Docker Image Locally

````bash
./mvnw clean install -P jib-push-to-local
````

<!-- Build docker image -->
#### Build Docker Image

````bash
./mvnw clean install -P jib-push-to-dockerhub
````

<!-- Roadmap -->
## :compass: Roadmap

* [ ] Upload images
* [ ] Improve tests
* [ ] Notifications
* [ ] Messaging
* [ ] Logs
* [ ] Handle wrong credentials


<!-- License -->
## :warning: License

Distributed under the GNU AGPLv3 License. See LICENSE.md for more information.
