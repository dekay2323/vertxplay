import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router

def server = vertx.createHttpServer()
def router = Router.router(vertx)

/**
 * My implementations of http://vertx.io/docs/vertx-web/groovy/
 * It is a script
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
    response.end("Catalog  name:${name} and id:${id}")
})

// Named capture groups
router.routeWithRegex("\\/(?<product>[^\\/]+)\\/(?<id>[^\\/]+)").handler({ routingContext ->
    def product = routingContext.request().getParam('product')
    def id = routingContext.request().getParam('id')

    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Regex product:${product} and id:${id}")
})

// Multiple routes
router.route("/post/").method(HttpMethod.GET).method(HttpMethod.POST).handler({ routingContext ->
    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Post, with multiple routes")
})

// Consumes content type
router.route("/text/").consumes("text/*").handler({ routingContext ->
    def response = routingContext.response()
    response.putHeader("content-type", "text/plain")
    response.end("Consumes text")
})

server.requestHandler(router.&accept).listen(8080)

