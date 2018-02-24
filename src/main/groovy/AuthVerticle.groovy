import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.OAuth2AuthHandler

/**
 * Make sure the groovy directory is a source path in Intellij (or IDE)
 * https://developer.okta.com/blog/2018/01/11/sso-oauth-vertx
 */
class AuthVerticle extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(AuthVerticle)

    // Can be run in ide
    // Can be debugged
    static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new AuthVerticle())
    }

    @Override
    void start() throws Exception {
        Router router = Router.router(vertx);

        vertx.exceptionHandler(new Handler<Throwable>() {
            @Override
            void handle(Throwable event) {
                System.err.write("Exception has been thrown : " + event.printStackTrace());
            }
        })

        // Load in the config
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "application.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        retriever.getConfig({ ar ->
            if (ar.failed()) {
                logger.error("failed to retrieve config.");
            } else {
                JsonObject json = JsonObject.mapFrom(ar.result());
                config().mergeIn(json);
                startServer();
            }
        })
        logger.info("start")
    }

    void startServer() {
        logger.info("startServer")

        Router router = Router.router(vertx);

        router.route('/').handler(this.&home)
        AuthHandler authHandler = getOAuthHandler(router)
        router.route("/private/*").handler(authHandler)
        router.route("/private/secret")
                .handler({ ctx ->
            ctx.response().end("Hi");
        })

        vertx.createHttpServer()
                .requestHandler(router.&accept)
                .listen(config().getInteger("port"))
    }

    AuthHandler getOAuthHandler(Router router) {
        OAuth2Auth oauth2 = OAuth2Auth.create(vertx, OAuth2FlowType.AUTH_CODE, new OAuth2ClientOptions()
                .setClientID(config().getString("0oae4muh0jw1QZ9Ej0h7"))
                .setClientSecret(config().getString("-LkEK0YLeNhfKWo45uWYywz7nbDan3IQmixQ10jH"))
                .setSite(config().getString("issuer"))
                .setTokenPath("/v1/token")
                .setAuthorizationPath("/v1/authorize")
                .setUserInfoPath("/v1/userinfo")
                .setUseBasicAuthorizationHeader(false)
        )

        OAuth2AuthHandler authHandler = OAuth2AuthHandler.create(oauth2, config().getString("callbackUrl"))
        authHandler.extraParams(new JsonObject("{\"scope\":\"openid profile email\"}"))
        authHandler.setupCallback(router.route())
        return authHandler
    }

    // localhost:8080/
    private void home(RoutingContext routingContext) {
        logger.debug 'home'
        def response = routingContext.response()
        response.end('Home')
    }

}