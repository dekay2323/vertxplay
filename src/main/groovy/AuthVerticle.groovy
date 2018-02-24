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
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.OAuth2AuthHandler

/**
 * https://developer.okta.com/blog/2018/01/11/sso-oauth-vertx
 * localhost:8080/
 * localhost:8080/private/secret
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
        logger.debug("start")
        handleExceptions()
        loadConfigurationFile().getConfig({ ar ->
            if (ar.failed()) {
                logger.error("failed to retrieve config.");
            } else {
                JsonObject json = JsonObject.mapFrom(ar.result());
                config().mergeIn(json);
                startServer();
            }
        })
    }

    private ConfigRetriever loadConfigurationFile() {
        logger.debug("loadConfigurationFile")
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "application.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        retriever
    }

    private Vertx handleExceptions() {
        logger.debug("handleExceptions")
        vertx.exceptionHandler(new Handler<Throwable>() {
            @Override
            void handle(Throwable event) {
                System.err.write("Exception has been thrown : " + event.printStackTrace());
            }
        })
    }

    private void startServer() {
        logger.debug("startServer")
        Router router = Router.router(vertx);
        router.route('/').handler(this.&home)
        AuthHandler authHandler = getOAuthHandler(router)
        router.route("/private/*").handler(authHandler)
        router.route("/private/secret").handler({ ctx -> ctx.response().end("Hi") })

        vertx.createHttpServer()
                .requestHandler(router.&accept)
                .listen(config().getInteger("port"))
    }

    private AuthHandler getOAuthHandler(Router router) {
        logger.debug("getOAuthHandler")
        OAuth2Auth oauth2 = OAuth2Auth.create(vertx, OAuth2FlowType.AUTH_CODE, new OAuth2ClientOptions()
                .setClientID(config().getString("clientId"))
                .setClientSecret(config().getString("clientSecret"))
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

    private void home(RoutingContext routingContext) {
        logger.debug 'home'
        def response = routingContext.response()
        response.end('Home')
    }

}