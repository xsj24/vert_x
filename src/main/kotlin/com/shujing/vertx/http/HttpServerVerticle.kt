package com.shujing.vertx.http

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.shujing.vertx.wiki.WikiDatabaseService
import com.shujing.vertx.wiki.WikiDatabaseVerticle.Companion.CONFIG_WIKIDB_QUEUE
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

    val router = Router.router(vertx)
    router.get("/").handler(this::indexHandler)
    router.get("/fetch").handler(this::fetchPagesHandler)

    httpServer
      .requestHandler(router)
      .listen(port) {arHttp ->
        if (arHttp.succeeded()) {
          startFuture.complete()
          LOG.info("HTTP server started on port {}", port)
        } else{
          startFuture.fail(arHttp.cause())
        }
      }


  }

  private fun indexHandler(routingContext: RoutingContext) {
      routingContext.response()
        .putHeader("content-type", "text/html")
        .end("Hello from Vert.x!")
  }

  private fun fetchPagesHandler(routingContext: RoutingContext) {
      dbService.fetchAllPages { ar ->
        LOG.info("{}", ar.result())
      }

    routingContext.response()
      .putHeader("content-type", "text/html")
      .end("Hello from Vert.x!")
  }

}
