timestamps {
    node("dockerhost") {
        stage('Prepare') {
            git (url: 'https://gitlab.lmru.adeo.com/lego-front/auto-tests.git',
                credentialsId: 'jenkins-gitlab',
                branch: "debug"
            )
		}
        stage('Test') {
			sh("docker pull ksolkin/img-oracle-jdk8-maven-with-sh")
            docker.image('ksolkin/img-oracle-jdk8-maven-with-sh:latest')//.inside("-v android-gradle-cache:/root/.gradle -v android-maven-cache:/root/.m2")
			{
                sh "mvn -version"
				sh "mvn clean test"
            }
        }
    }
}