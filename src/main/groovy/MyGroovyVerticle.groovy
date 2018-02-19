import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router

def server = vertx.createHttpServer()
def router = Router.router(vertx)


def routeHome = router.route("/").handler({routingHandler ->
    def response = routingHandler.response()
    response.putHeader("content-type", "text/plain")
    response.end("Hello World from Vert.x-Web!")
})

def routeHome1 = router.route("/home/").handler({routingHandler -> 
    def response = routingHandler.response()
    response.setChunked(true)
    response.write("routeHome1\n")
    routingHandler.vertx().setTimer(5000, {tid ->
        routingHandler.next()
    })
})

// Note the star means it will route on /home/hello/
def routeHome2 = router.route("/home/*").handler({routingHandler ->
    def response = routingHandler.response()
    response.setChunked(true)
    response.write("routeHome2\n")
    routingHandler.response().end()
})

// Rest params
def routeParams = router.route('/catalog/:name/:id').handler({routingHandler ->
    def response = routingHandler.response()
    response.putHeader("content-type", "text/plain")
    def name = routingHandler.request().getParam('name');
    def id = routingHandler.request().getParam('id')
    response.end("Hello World name:${name} and id:${id}")
})

server.requestHandler(router.&accept).listen(8080)

