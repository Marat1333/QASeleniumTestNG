TELEGRAM_BOT_URL = 'https://api.telegram.org/bot596012234:AAGAaYCfc2nDS3BAr2J7l0PRTgzOoBqGqy4'
TELEGRAM_REPORTS_CHAT = '-1001343153150'

env.TELEGRAM_CHAT = env.TELEGRAM_CHAT.replaceFirst(/^(.*?)\(.*\)/, '$1')


def telegramMessage(message) {
    if (env.TELEGRAM_CHAT) {
        sh """
           curl -X POST ${TELEGRAM_BOT_URL}/sendMessage \
                -d parse_mode=Markdown \
                -d chat_id=${env.TELEGRAM_CHAT} \
                -d text="${message}"
        """
    }
    if (env.SEND_TO_REPORTS_CHAT == "true") {
        sh """
           curl -X POST ${TELEGRAM_BOT_URL}/sendMessage \
                -d parse_mode=Markdown \
                -d chat_id=${TELEGRAM_REPORTS_CHAT} \
                -d text="${message}"
        """
    }
}

GString getMvnStrRun() {
    return "mvn clean test " +
            "-Dmaven.test.failure.ignore=true " +
            "-DrunWithIssues=${env.RUN_CASE_WITH_ISSUE} " +
            "-DxmlPath=testXML/mobile/api/${env.SUITE_XML} " +
            "-DthreadCount=${env.THREAD_COUNT} " +
            "-DmRun=\"${env.RUN}\" " +
            "-Denv=${env.ENVIRONMENT} " +
            "-DmSuite=29833 -DmProject=10 "

}

timestamps {
    node("dockerhost") {
        stage('Run API auto tests') {

            checkout(
                    [$class                           : 'GitSCM',
                     branches                         : [[name: env.AUTOTESTS_BRANCH ? env.AUTOTESTS_BRANCH : 'master']],
                     doGenerateSubmoduleConfigurations: false,
                     extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'auto-tests']],
                     submoduleCfg                     : [],
                     userRemoteConfigs                : [[credentialsId: 'jenkins-gitlab', url: 'https://gitlab.lmru.adeo.com/lego-front/auto-tests.git']]
                    ])


            try {
                docker.image('maven:3.6.3-jdk-8-openj9').inside("-v $HOME/.m2:/root/.m2") {
                    dir('auto-tests') {
                        withAllureUpload(serverId: 'allure-server', projectId: '13', results: [[path: 'target/allure-results']], name: env.RUN) {
                            sh(getMvnStrRun())
                        }
                    }
                }
            } finally {
                stage('Generate Allure Reports') {
                    allure([
                            includeProperties: false,
                            jdk              : '',
                            properties       : [],
                            reportBuildPolicy: 'ALWAYS',
                            results          : [[path: 'auto-tests/target/allure-results']]
                    ])
                }
            }
        }

        stage('Send notification') {
            telegramMessage("?????? ???????????? API ?????????? ??????????????????. Test run: ${env.RUN} \n " +
                    "[Allure report](" + env.BUILD_URL + "allure)")
        }
    }
}