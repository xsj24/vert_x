package com.shujing.vertx

import com.shujing.vertx.http.HttpServerVerticle
import com.shujing.vertx.wiki.WikiDatabaseVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.vertx.core.DeploymentOptions


class MainVerticle : AbstractVerticle() {

  companion object {
    private val LOG : Logger = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  override fun start(startFuture: Future<Void>) {

    val dbVerticleDeployment = Future.future<String>()
    vertx.deployVerticle(WikiDatabaseVerticle(), DeploymentOptions(), dbVerticleDeployment.completer())

    dbVerticleDeployment.compose { ar ->
      val httpServerFuture = Future.future<String>()
      vertx.deployVerticle(HttpServerVerticle(), DeploymentOptions(), httpServerFuture.completer())
      httpServerFuture
    }.setHandler { ar ->
      if (ar.succeeded()) {
        startFuture.complete()
      } else{
        startFuture.fail(ar.cause())
      }
    }
  }

}
