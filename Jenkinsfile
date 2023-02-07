def user_id
def group_id
node {
    user_id = sh(returnStdout: true, script: 'id -u').trim()
    group_id = sh(returnStdout: true, script: 'id -g').trim()
}

pipeline {
    agent {
        dockerfile {
            dir '.ops/build-docker'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /etc/group:/etc/group'
            additionalBuildArgs "--build-arg USER_ID=${user_id} --build-arg GROUP_ID=${group_id}"
        }
    }
    parameters {
        choice(name: 'JAVA_VERSION', defaultValue: 'JAVA11', description: 'The Java version to use for the build.')
        choice(name: 'RELEASE_TYPE', choices: '\nTEST\nSNAPSHOT\nPRIVATE\nPUBLIC', description: 'Type of release: public, private, snapshot, or test. Required parameter only if this is going to be used as a release build.')
        booleanParam(name: 'CREATE_RELEASE_ZIP', defaultValue: false, description: 'Create the release zip from the dist directory')
        string(name: 'AWS_INSTANCE_TYPE', defaultValue: 'm4.xlarge', description: 'AWS instance type for the databricks, chaos, or release tests (m4.4xlarge or larger is recommended if dataset is >1m)')
        string(name: 'AWS_INSTANCE_CIDR', defaultValue: '127.0.0.1/32', description: 'The CIDR that will have access to Stardog running on 5820 in the environment for databricks, chaos, or release tests (if applicable)')
        string(name: 'ARTIFACT_TAG', defaultValue: '', description: 'The tag used to stage artifacts (requires creating release zip and will push artifacts to artifactory, docker, etc.)')
        string(name: 'SLACK', defaultValue: '', description: 'The slack channel to notify of the builds results.  This is typically #notifications-eng-gh.')
    }
    options {
        timeout(time: 30, unit: 'MINUTES')
    }
    environment {
        jenkinsOsxPub = credentials('jenkins.osx.pub')
        jenkinsOsxPriv = credentials('jenkins.osx.priv')
        artifactoryUsername = credentials('artifactoryUsername')
        artifactoryPassword = credentials('artifactoryPassword')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        STARDOG_GH_CREDS = credentials('stardog-ops-github-jenkins')
        STARDOG_LICENSE = credentials('stardog_license_base64')
        TF_VAR_artifactory_url = 'http://stardog.jfrog.io/stardog/stardog-releases-local'
        TF_VAR_snapshot_url = 'http://stardog.jfrog.io/stardog/stardog-snapshots-local'
        TF_VAR_artifactory_username = credentials('artifactoryUsername')
        TF_VAR_artifactory_password = credentials('artifactoryPassword')
        STARDOG_GITHUB_API_TOKEN = credentials('stardog-github-api-token')

        artifactoryUsernameRO = credentials('artifactory-ci-readonly')

        LANG = 'C.UTF-8'
        S3_LOCAL_HOSTNAME = "s3${env.BUILD_ID}${UUID.randomUUID().toString().replace("-", "")}"
        RHEL_CREDS = credentials('RHEL')
    }
    stages {
        stage('Setup env') {
            steps {
                script {
                    env.ARTIFACT_TAG = params.ARTIFACT_TAG.toLowerCase()
                    setJavaVersion(params.JAVA_VERSION)
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    // The default Java version in the image is 11, which should be used to build
                    printJavaVersion()
                    sh "sbt assembly"
                }
            }
        }
        stage('Publish jars and zip internally') {
            steps {
                script {
                    sh "./ops/jenkins/delete-nightly-snapshot.sh"
                    sh "./ops/jenkins/publish_artifact.sh"
                }
            }
        }
        stage('Slack Message') {
            when { expression { params.SLACK != "" } }
            steps {
                script {
                    notifyBuild("Success :discodancer:", "GREEN", params)
                }
            }
        }
    }
    post {
        failure {
            script {
                if (params.SLACK != "") {
                    notifyBuild("Failed :computerrage:", "RED", params)
                }
            }
        }
    }
}

def setJavaVersion(String javaVersion) {
    echo "Setting Java version to " + javaVersion
    if (javaVersion == "JAVA11") {
        env.JAVA_HOME = "/usr/lib/jvm/java-11-openjdk-amd64/"
        sh "sudo update-java-alternatives --set java-1.11.0-openjdk-amd64"
    }
    else if (javaVersion == "JAVA8") {
        env.JAVA_HOME = "/usr/lib/jvm/java-8-openjdk-amd64/"
        sh "sudo update-java-alternatives --set java-1.8.0-openjdk-amd64"
    }
    else {
        currentBuild.result = 'FAILURE'
        error("Unknown Java version " + javaVersion)
    }
    printJavaVersion()
}

def printJavaVersion() {
    echo "Checking Java version"
    echo "JAVA_HOME=${env.JAVA_HOME}"
    sh "java -version"
}