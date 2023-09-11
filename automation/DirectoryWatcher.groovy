import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

def watchDirectory(Path pathToWatch) {
    def watchService = FileSystems.getDefault().newWatchService()
    pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)


    println "Watching directory : ${pathToWatch}"

    while ( true) {
        def watchKey = watchService.take()

        watchKey.pollEvents().each { event ->
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {

                def newfile = pathToWatch.resolve(event.context())
                println "New file created: ${newfile}"
                createTestStructure(newfile.toFile())
            }
            }
       if (!watchKey.reset()) {
           break
       }
    }
}

def createTestStructure(File newFile) {

    def extension = newFile.name.split("\\.").last()

    if (extension == "groovy") {
        createGroovyTestStructure(newFile)
    } else if (extension == "ps1") {
        createPs1TestStructure(newFile)
    }
}


def createGroovyTestStructure(File newFile) {
    // Determine the root project directory
    def rootProjectDir = newFile.absolutePath.split("src\\\\main\\\\groovy")[0]

    // Base directory for tests
    def baseDir = new File(rootProjectDir, "src\\test\\groovy")

    // Calculate the relative path for the new file
    def relativePath = newFile.absolutePath.replace(rootProjectDir + "src\\main\\groovy\\", "").replace(newFile.name, "")

    // Create the test directory based on the relative path
    def testDir = new File(baseDir, relativePath)
    testDir.mkdirs()

    // Create the test file
    def testFileName = getNameWithoutExtension(newFile) + "Test.groovy"
    def testFile = new File(testDir, testFileName)

    testFile.text = """
    class ${getNameWithoutExtension(newFile)}Test extends Specification {
        def "test ${getNameWithoutExtension(newFile)}"() {
            given:
            // setup statement here
            when:
            // action statements here
            then:
            // assertion statements here
        }
    }
    """
    println "Test file created: ${testFile}"
}

def createPs1TestStructure(File newFile) {
    def rootProjectDir = newFile.absolutePath.split("src\\\\main\\\\powershell")[0]
    def baseDir = new File(rootProjectDir, "src\\test\\powershell")

    def relativePath = newFile.absolutePath.replace(rootProjectDir + "src\\main\\powershell\\", "").replace(newFile.name, "")

    def testDir = new File(baseDir, relativePath)
    testDir.mkdirs()


    def testFileName = getNameWithoutExtension(newFile) + "Test.ps1"
    def testFile = new File(testDir, testFileName)


    testFile.text = """Describe "${getNameWithoutExtension(newFile)} functionality" {
Context "Functionality 1" {
It "Does something expected" {
\$true | Should -Be \$true
            }
        }
    }
"""
    println "Pester test file created: ${testFile}"
}


String getNameWithoutExtension(File file) {
    return file.name.take(file.name.lastIndexOf('.'))
}

watchDirectory(Paths.get("..\\src\\main\\groovy"))