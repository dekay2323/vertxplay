import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router

def server = vertx.createHttpServer()
def router = Router.router(vertx)


def routeHome = router.route("/").handler({routingContext ->
    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Hello World from Vert.x-Web!")
})

def routeHome1 = router.route("/home/").handler({routingContext -> 
    def response = routingContext.response()
    response.setChunked(true)
    response.write("routeHome1\n")
    routingContext.vertx().setTimer(5000, {tid ->
        routingContext.next()
    })
})

def routeHome2 = router.route("/home/").handler({routingContext ->
    def response = routingContext.response()
    response.setChunked(true)
    response.write("routeHome2\n")
    routingContext.response().end()
})

server.requestHandler(router.&accept).listen(8080)

