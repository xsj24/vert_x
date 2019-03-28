package com.shujing.vert_x

import com.shujing.vert_x.http.HttpServerVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.vertx.core.DeploymentOptions



class MainVerticle : AbstractVerticle() {

  private val log : Logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startFuture: Future<Void>) {
    val httpServerFuture = Future.future<String>()
    vertx.deployVerticle(HttpServerVerticle(), DeploymentOptions(), httpServerFuture.completer())
    httpServerFuture.setHandler { ar ->
      if (ar.succeeded()) {
        startFuture.complete()
      } else{
        startFuture.fail(ar.cause())
      }
    }
  }

}
