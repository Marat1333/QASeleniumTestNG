def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DxmlPath=testXML/GlobalWebSuite.xml -DmpropsFile=src/main/resources/configurationFiles/${env.CONFIGURATION}_grid.yml -DthreadCount=${env.THREAD_COUNT} -DmPlan=${env.PLAN} -DmRun=${env.RUN} -DmSuite=4378 -DmProject=16"

pipeline {
    agent {
        docker {
            image 'ksolkin/img-oracle-jdk8-maven-with-sh'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    stages {
        stage('test') {
            steps {
                sh(mvn_run_str)
                sh 'echo Finish'
            }
        }
        stage('reports') {
            steps {
                script {
                    allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                    ])
                }
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