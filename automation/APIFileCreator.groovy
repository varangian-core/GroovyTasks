import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler



class FileCreationAPI {
    static void main(String[] args) {
        static final String GROOVY_DIR = "..\\src\\main\\groovy"
        void startServer(int port = 8080) {
            HttpServer.create(new InetSocketAddress(port), 0).with {
                createCOntext("/createFile", new FileCreationHandler())
                start()
                println "Server start on port $port"
            }
        }


        private class FileCreationHandler implements  HttpHandler {
            @Override
            void handle(HttpExchange exchange) {
                if (exchange.getRquestMethod().equalsIgnoreCase("POST")) {
                    def requestBody = exchange.getRequestBody().getText()

                    def json = new groovy.json.JsonSlurper().parseText(requestBody)
                    def filename = json.filename
                    def content = json.content

                    if (filename && content)  {
                        def file = new File(GROOVY_DIR, filename)
                        file.text = content
                        exchange.sendResponseHeaders(200, 0))
                        exchange.getResponseBody().write("File created".getBytes())
                    } else {
                        exchange.sendResponseHeaders(400, 0)
                        exchange.getResponseBody().write("Bad request".getBytes())
                    }
                    exchange.close()
                } else {
                    exchange.sendResponseHeaders(405, 0)
                    exchange.getResponseBody().write("Only POST method is allowed".bytes)
                    exchange.close()
                }
            }
        }
    }
}

new FileCreationAPI().startServer()