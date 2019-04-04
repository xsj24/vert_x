package com.shujing.vertx.wiki

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.serviceproxy.ServiceBinder




class WikiDatabaseVerticle : AbstractVerticle() {

  companion object {
    private val LOG : Logger = LoggerFactory.getLogger(this::class.java)
    const val CONFIG_WIKIDB_JDBC_URL = "wikidb.jdbc.url"
    const val CONFIG_WIKIDB_JDBC_DRIVER_CLASS = "wikidb.jdbc.driver_class"
    const val CONFIG_WIKIDB_JDBC_USERNAME = "wikidb.jdbc.username"
    const val CONFIG_WIKIDB_JDBC_PASSWORD = "wikidb.jdbc.password"
    const val CONFIG_WIKIDB_JDBC_MAX_POOL_SIZE = "wikidb.jdbc.max_pool_size"
    const val CONFIG_WIKIDB_QUEUE = "wikidb.queue"
  }

  override fun start(startFuture: Future<Void>) {
    val dbClient = JDBCClient.createShared(vertx, JsonObject()
      .put("url", config().getString(CONFIG_WIKIDB_JDBC_URL, "jdbc:h2:~/db/myTest"))
      .put("driver_class", config().getString(CONFIG_WIKIDB_JDBC_DRIVER_CLASS, "org.h2.Driver"))
      .put("user", config().getString(CONFIG_WIKIDB_JDBC_USERNAME, "sa"))
      .put("password", config().getString(CONFIG_WIKIDB_JDBC_PASSWORD, ""))
      .put("max_pool_size", config().getInteger(CONFIG_WIKIDB_JDBC_MAX_POOL_SIZE, 30)))

    dbClient.query("select 1") { ar ->
      ServiceBinder(vertx)
        .setAddress(config().getString(CONFIG_WIKIDB_QUEUE, "wikidb.queue"))
        .register(WikiDatabaseService::class.java, WikiDatabaseService.create(dbClient))
      startFuture.complete()
    }


  }


}
