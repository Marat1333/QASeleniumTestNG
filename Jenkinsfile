def mvn_run_str = "mvn clean test -Dmaven.test.failure.ignore=true -DrunWithIssues=${env.RUN_CASE_WITH_ISSUE} -DxmlPath=testXML/mobile/api/${env.SUITE_XML} -DthreadCount=${env.THREAD_COUNT} -DmRun=${env.RUN} -DmSuite=29833 -DmProject=10 -Denv=${env.ENVIROMENT}"

pipeline {
    agent { label 'dockerhost' }
    stages {

        stage("notification") {
			steps {
				echo(WORKSPACE)
                telegramSend(
                            chatId: env.TELEGRAM_CHAT,
                            message: "Результаты тут -> https://jenkins.lmru.adeo.com/job/lego-front/view/MAGASIN\\%20mobile/job/lego-front-android-Run_API_tests/"+ env.BUILD_NUMBER +"/allure"
                        )
			}
        }
    }

}