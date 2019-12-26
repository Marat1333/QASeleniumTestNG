pipeline {
    agent {
        docker { image 'ksolkin/img-oracle-jdk8-maven-with-sh' }
    }
    stages {
        stage('Test') {
            steps {
                sh """
                mvn clean test -Dmaven.test.failure.ignore=false
                -DxmlPath=testXML/GlobalWebSuite.xml
                -DmpropsFile=src/main/resources/configurationFiles/${env.CONFIGURATION}_grid.yml
                -DthreadCount=${env.THREAD_COUNT}
                -DmPlan=${env.PLAN}
                -DmRun=${env.RUN}
                -DmSuite=258
                -DmProject=10
                """
            }
        }
    }
}

/*timestamps {
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
}*/