import groovy.transform.Field
import groovy.transform.Method



@Field
final PRIMARY_JOB_NAME = "Primary Job";

@Field
final SECONDARY_JOB_NAME = "Secondary";

@Method
def testOutputFunction() {
    script {
        echo "Test outpu"
    }
}

return this;