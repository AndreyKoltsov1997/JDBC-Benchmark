import groovy.transform.Field


@Field
final PRIMARY_JOB_NAME = "Primary Job";

@Field
final SECONDARY_JOB_NAME = "Secondary";

def testOutputFunction() {
    script {
        echo "Test outpu"
    }
}

return this;