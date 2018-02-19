import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.groovy.core.http.HttpClient_GroovyExtension

HttpClient server = vertx.createHttpServer()
Router router = Router.router(vertx);

router.route().handler({routingContext ->
    HttpServerResponse response = routingContext.response()
    response.putHeader('content-type', 'text/plain')
    
    response.end('Hello world from vert.x-web')
})

server.redirectHandler(router.&accept).listen(8080)

