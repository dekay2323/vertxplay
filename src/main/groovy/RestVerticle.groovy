import groovy.json.JsonOutput
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

/**
 * Had some problems getting it to run in the IDE.
 * Make sure the groovy directory is a source path in Intellij (or IDE)
 */
class RestVerticle extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(RestVerticle)

    // Can be run in ide
    // Can be debugged
    static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new RestVerticle())
    }

    @Override
    void start() {
        logger.debug 'start'
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get('/home/')
                .method(HttpMethod.POST)
                .consumes('application/json')
                .produces('application/json')
                .handler(this.&home)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8080)
    }

    private void home(RoutingContext routingContext) {
        logger.debug 'home'
        def inJson = routingContext.getBodyAsJson()
        logger.debug "inJson = ${inJson}"
        def outJson = [text: 'hello']
        def response = routingContext.response()
        response.end(JsonOutput.toJson(outJson))
    }
}