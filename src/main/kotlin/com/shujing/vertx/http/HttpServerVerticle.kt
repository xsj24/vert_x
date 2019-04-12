package com.shujing.vertx.http

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.shujing.vertx.wiki.WikiDatabaseService
import com.shujing.vertx.wiki.WikiDatabaseVerticle.Companion.CONFIG_WIKIDB_QUEUE
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext


class HttpServerVerticle : AbstractVerticle() {

  companion object {
    private val LOG : Logger = LoggerFactory.getLogger(this::class.java)
    private const val port = 8888
  }

  private lateinit var dbService: WikiDatabaseService

  override fun start(startFuture: Future<Void>) {

    val wikiDbQueue = config().getString(CONFIG_WIKIDB_QUEUE, "wikidb.queue")
    dbService = WikiDatabaseService.createProxy(vertx, wikiDbQueue)

    val httpServer = vertx.createHttpServer()

    httpServer
      .requestHandler(router())
      .listen(port) {arHttp ->
        if (arHttp.succeeded()) {
          startFuture.complete()
          LOG.info("HTTP server started on port {}", port)
        } else{
          startFuture.fail(arHttp.cause())
        }
      }
  }

  private fun router(): Router {
    val router = Router.router(vertx)
    router.get("/").handler(this::indexHandler)
    router.get("/fetch").handler(this::fetchPagesHandler)
    router.get("/hi/:name").handler(this::helloHandler)
    return router
  }

  private fun indexHandler(routingContext: RoutingContext) {
      routingContext.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, "text/html")
        .end("Hello from Vert.x!")
  }

  private fun fetchPagesHandler(routingContext: RoutingContext) {
      dbService.fetchAllPages { ar ->
        LOG.info("{}", ar.result())
      }

    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, "text/html")
      .end("Hello from Vert.x!")
  }

  private fun helloHandler(rc: RoutingContext) {
    var msg = "hello"
    if (rc.pathParam("name") != null) {
      msg += " " + rc.pathParam("name")
    }
    val json = JsonObject().put("msg", msg)
    rc.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(json.encodePrettily())
  }

}
