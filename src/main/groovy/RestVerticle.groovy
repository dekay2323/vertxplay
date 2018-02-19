import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

/**
 * Had some problems getting it to run in the IDE.
 * Make sure the groovy directory is a source path in Intellij (or IDE)
 */
class RestVerticle extends AbstractVerticle {

    // Can be run in ide
    // Can be debugged
    static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new RestVerticle())
    }

    @Override
    void start() {
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get('/home/').handler(this.&home)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8080)
    }

    private void home(RoutingContext routingContext) {
        def response = routingContext.response()
        response.putHeader("content-type", "text/plain")
        response.end("You are home")
    }
}