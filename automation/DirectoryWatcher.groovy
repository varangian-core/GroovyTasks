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

String getNameWithoutExtension(File file) {
    return file.name.take(file.name.lastIndexOf('.'))
}



watchDirectory(Paths.get("..\\src\\main\\groovy"))