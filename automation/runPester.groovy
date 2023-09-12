def executePesterTest( testFilePath) {

    def pesterCommand = """
        Import-Module Pester
        Invoke-Pester -Script @{path = '${testFilePath}'} -OutputFile 'C:\\temp\\pesterTestResults.xml' -OutputFormat NUnitXml
"""

    def process = ["powershell.exe", "-Command", pesterCommand].execute()
   process.waitFor()


    return process.text

}

def testDirectoryPath = '/src/test/powershell' //Do we want a buffer queue with mutex lock?
def  testFiles= []

new File(testDirectoryPath).eachFile { file ->
    if (file.name.endsWith('.ps1')) {
        testFiles << file.absolutePath
    }
}

testFiles.each { testFilePath ->
    println "Executing Pester test: ${testFilePath}"
    def results = executePesterTest(testFilePath)
    println results
}
