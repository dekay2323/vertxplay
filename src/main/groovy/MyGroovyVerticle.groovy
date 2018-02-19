import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router

def server = vertx.createHttpServer()
def router = Router.router(vertx)

/**
 * My implementations of http://vertx.io/docs/vertx-web/groovy/
 * 
 */
router.route("/").handler({routingContext ->
    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Hello World from Vert.x-Web!")
})

router.route("/home/").handler({routingContext -> 
    def response = routingContext.response()
    response.setChunked(true)
    response.write("routeHome1\n")
    routingContext.vertx().setTimer(5000, {tid ->
        routingContext.next()
    })
})

// Note the star means it will route on /home/hello/
router.route("/home/*").handler({routingContext ->
    def response = routingContext.response()
    response.setChunked(true)
    response.write("routeHome2\n")
    routingContext.response().end()
})

// Rest params
router.route('/catalog/:name/:id').handler({routingContext ->
    def name = routingContext.request().getParam('name');
    def id = routingContext.request().getParam('id')

    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Hello World name:${name} and id:${id}")
})

// Refex param routing
router.route().pathRegex('\\/([^\\/]+)\\/([^\\/]+)').handler({routingContext ->
    def product = routingContext.request().getParam('param0')
    def id = routingContext.request().getParam('param1')

    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("product:${product} and id:${id}")
})

server.requestHandler(router.&accept).listen(8080)

