package com.shujing.vertx.http

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HttpServerVerticle : AbstractVerticle() {

  companion object {
    private val LOG : Logger = LoggerFactory.getLogger(this::class.java)
    private val port = 8888
  }

  override fun start(startFuture: Future<Void>) {
      vertx
        .createHttpServer()
        .requestHandler { req ->
          req.response()
            .putHeader("content-type", "content-type")
            .end("Hello from Vert.x!")
        }
        .listen(port) {arHttp ->
          if (arHttp.succeeded()) {
            startFuture.complete()
            LOG.info("HTTP server started on port {}", port)
          } else{
            startFuture.fail(arHttp.cause())
          }
        }
  }


}
