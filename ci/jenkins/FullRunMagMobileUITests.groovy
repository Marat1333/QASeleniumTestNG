timestamps {
    node("dockerhost") {
        try {
            stage('Run job #1') {
                build job: 'lego-front-android-Run-UI-tests', parameters: [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: 'dev'],
                        [$class: 'StringParameterValue', name: 'TEST_RUN', value: 'true'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                        [$class: 'StringParameterValue', name: 'TEST_THREAD_COUNT', value: env.TEST_THREAD_COUNT],
                        [$class: 'StringParameterValue', name: 'TEST_XML', value: 'UiGlobalMobileSuite.xml'],
                        [$class: 'StringParameterValue', name: 'RUN_NAME', value: env.RUN_NAME],
                        [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                        [$class: 'StringParameterValue', name: 'ONLY_SMOKE', value: 'false'],
                        [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                        [$class: 'StringParameterValue', name: 'CHECK_CHANGES', value: 'true'],
                        [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test'],
                        [$class: 'StringParameterValue', name: 'WRITE_CHANGE_LOG', value: 'true'],
                        [$class: 'StringParameterValue', name: 'PUSH_CHANGES', value: 'false'],
                        [$class: 'StringParameterValue', name: 'PUSH_VERSION_TAG', value: 'false'],
                        [$class: 'StringParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: 'true'],
                        [$class: 'StringParameterValue', name: 'AUTOTESTS_BRANCH', value: 'master'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_REPORTS_CHAT', value: '-1001343153150(OMFREM autotest reports)'],
                        [$class: 'StringParameterValue', name: 'SEND_TO_REPORTS_CHAT', value: 'true']
                ]
            }
        } finally {
            stage('Run job #2 (Mock)') {
                build job: 'lego-front-android-Run-UI-tests', parameters: [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: 'dev'],
                        [$class: 'StringParameterValue', name: 'TEST_RUN', value: 'true'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                        [$class: 'StringParameterValue', name: 'TEST_THREAD_COUNT', value: env.TEST_THREAD_COUNT],
                        [$class: 'StringParameterValue', name: 'TEST_XML', value: 'UIGlobalMobileMockSuite.xml'],
                        [$class: 'StringParameterValue', name: 'RUN_NAME', value: env.RUN_NAME],
                        [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                        [$class: 'StringParameterValue', name: 'ONLY_SMOKE', value: 'false'],
                        [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                        [$class: 'StringParameterValue', name: 'CHECK_CHANGES', value: 'true'],
                        [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test-mock'],
                        [$class: 'StringParameterValue', name: 'WRITE_CHANGE_LOG', value: 'true'],
                        [$class: 'StringParameterValue', name: 'PUSH_CHANGES', value: 'false'],
                        [$class: 'StringParameterValue', name: 'PUSH_VERSION_TAG', value: 'false'],
                        [$class: 'StringParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: 'true'],
                        [$class: 'StringParameterValue', name: 'AUTOTESTS_BRANCH', value: 'master'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_REPORTS_CHAT', value: '-1001343153150'],
                        [$class: 'StringParameterValue', name: 'SEND_TO_REPORTS_CHAT', value: 'true']
                ]
            }
        }
    }
}