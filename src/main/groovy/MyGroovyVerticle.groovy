import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router

def server = vertx.createHttpServer()
def router = Router.router(vertx)

router.route().handler({routingContext ->
    HttpServerResponse response = routingContext.response()
    response.putHeader('content-type', 'text/plain')
    
    response.end('Hello world from vert.x-web')
})

server.requestHandler(router.&accept).listen(8080)

