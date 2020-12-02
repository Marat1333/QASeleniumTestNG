timestamps {
    node("dockerhost") {
        stage('Run job #1') {
            build job: 'lego-front-android-Run-UI_TESTS', parameters: [
                    [$class: 'StringParameterValue', name: 'BRANCH', value: 'kas/update_build_apk'],
                    [$class: 'StringParameterValue', name: 'TEST_RUN', value: 'false'],
                    [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: '-1001283842704'],
                    [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                    [$class: 'StringParameterValue', name: 'CHECK_CHANGES', value: 'true'],
                    [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test'],
                    [$class: 'StringParameterValue', name: 'WRITE_CHANGE_LOG', value: 'true'],
                    [$class: 'StringParameterValue', name: 'PUSH_CHANGES', value: 'false'],
                    [$class: 'StringParameterValue', name: 'PUSH_VERSION_TAG', value: 'false'],
                    [$class: 'StringParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: 'true'],
                    [$class: 'StringParameterValue', name: 'AUTO_TEST_BRANCH', value: 'master']
            ]
        }

        stage('Run job #2') {
            build job: 'lego-front-android-Run-UI_TESTS', parameters: [
                    [$class: 'StringParameterValue', name: 'BRANCH', value: 'kas/update_build_apk'],
                    [$class: 'StringParameterValue', name: 'TEST_RUN', value: 'false'],
                    [$class: 'StringParameterValue', name: 'TELEGRAM_CHAT', value: '-1001283842704'],
                    [$class: 'StringParameterValue', name: 'BUILD_TYPE', value: 'TEST'],
                    [$class: 'StringParameterValue', name: 'CHECK_CHANGES', value: 'true'],
                    [$class: 'StringParameterValue', name: 'ENV_PROFILE', value: 'auto-test-mock'],
                    [$class: 'StringParameterValue', name: 'WRITE_CHANGE_LOG', value: 'true'],
                    [$class: 'StringParameterValue', name: 'PUSH_CHANGES', value: 'false'],
                    [$class: 'StringParameterValue', name: 'PUSH_VERSION_TAG', value: 'false'],
                    [$class: 'StringParameterValue', name: 'SNIFFING_TRAFFIC_AVAILABLE', value: 'true'],
                    [$class: 'StringParameterValue', name: 'AUTO_TEST_BRANCH', value: 'master']
            ]
        }
    }
}