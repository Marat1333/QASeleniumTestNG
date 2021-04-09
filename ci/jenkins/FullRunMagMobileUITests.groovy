timestamps {
    node("dockerhost") {
        try {
            stage('Run job #1') {
                build job: 'lego-front-android-Run-UI-tests', parameters: [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: 'dev'],
                        [$class: 'BooleanParameterValue', name: 'TEST_RUN', value: true],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                        [$class: 'StringParameterValue', name: 'TEST_THREAD_COUNT', value: env.TEST_THREAD_COUNT],
                        [$class: 'StringParameterValue', name: 'TEST_XML', value: 'UiGlobalMobileSuite.xml'],
                        [$class: 'StringParameterValue', name: 'RUN_NAME', value: env.RUN_NAME],
                        [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                        [$class: 'BooleanParameterValue', name: 'ONLY_SMOKE', value: false],
                        [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                        [$class: 'BooleanParameterValue', name: 'CHECK_CHANGES', value: true],
                        [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test'],
                        [$class: 'BooleanParameterValue', name: 'WRITE_CHANGE_LOG', value: true],
                        [$class: 'BooleanParameterValue', name: 'PUSH_CHANGES', value: false],
                        [$class: 'BooleanParameterValue', name: 'PUSH_VERSION_TAG', value: false],
                        [$class: 'BooleanParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: true],
                        [$class: 'StringParameterValue', name: 'AUTOTESTS_BRANCH', value: 'master'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_REPORTS_CHAT', value: '-1001343153150(OMFREM autotest reports)'],
                        [$class: 'BooleanParameterValue', name: 'SEND_TO_REPORTS_CHAT', value: true],
                        [$class: 'StringParameterValue', name: 'CALCULATOR_BUNDLE_VERSION', value: 'latest']
                ]
            }
        } finally {
            stage('Run job #2 (Mock)') {
                build job: 'lego-front-android-Run-UI-tests', parameters: [
                        [$class: 'StringParameterValue', name: 'BRANCH', value: 'dev'],
                        [$class: 'BooleanParameterValue', name: 'TEST_RUN', value: true],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: env.TELEGRAM_CHAT],
                        [$class: 'StringParameterValue', name: 'TEST_THREAD_COUNT', value: env.TEST_THREAD_COUNT],
                        [$class: 'StringParameterValue', name: 'TEST_XML', value: 'UIGlobalMobileMockSuite.xml'],
                        [$class: 'StringParameterValue', name: 'RUN_NAME', value: env.RUN_NAME],
                        [$class: 'StringParameterValue', name: 'RETRY_COUNT', value: '1'],
                        [$class: 'BooleanParameterValue', name: 'ONLY_SMOKE', value: false],
                        [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                        [$class: 'BooleanParameterValue', name: 'CHECK_CHANGES', value: true],
                        [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test-mock'],
                        [$class: 'BooleanParameterValue', name: 'WRITE_CHANGE_LOG', value: true],
                        [$class: 'BooleanParameterValue', name: 'PUSH_CHANGES', value: false],
                        [$class: 'BooleanParameterValue', name: 'PUSH_VERSION_TAG', value: false],
                        [$class: 'BooleanParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: true],
                        [$class: 'StringParameterValue', name: 'AUTOTESTS_BRANCH', value: 'master'],
                        [$class: 'StringParameterValue', name: 'TELEGRAM_REPORTS_CHAT', value: '-1001343153150(OMFREM autotest reports)'],
                        [$class: 'BooleanParameterValue', name: 'SEND_TO_REPORTS_CHAT', value: true],
                        [$class: 'StringParameterValue', name: 'CALCULATOR_BUNDLE_VERSION', value: 'latest']
                ]
            }
        }
    }
}