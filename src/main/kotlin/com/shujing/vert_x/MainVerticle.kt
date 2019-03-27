package com.shujing.vert_x

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  private val log : Logger = LoggerFactory.getLogger(MainVerticle::class.java)

  private val port = 8888

  override fun start(startFuture: Future<Void>) {
    vertx
      .createHttpServer()
      .requestHandler { req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!")
      }
      .listen(port) { http ->
        if (http.succeeded()) {
          startFuture.complete()
          log.info("HTTP server started on port {}", port)
        } else {
          startFuture.fail(http.cause())
        }
      }
  }

}
