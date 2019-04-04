package com.shujing.vertx.wiki

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class WikiDatabaseServiceImpl(val dbClient: JDBCClient) : WikiDatabaseService {

  companion object {
    private val LOG : Logger = LoggerFactory.getLogger(this::class.java)
  }


  override fun fetchAllPages(resultHandler: Handler<AsyncResult<JsonArray>>): WikiDatabaseService {
    dbClient.query("select '1'") { ar ->
      if (ar.succeeded()) {
        val array = JsonArray(ar.result()
          .results
          .stream()
          .map { json -> json.getString(0) }
          .sorted()
          .collect(Collectors.toList<String>())
        )
        resultHandler.handle(Future.succeededFuture(array))
      } else {
        LOG.error("Database query[fetchAllPages] error", ar.cause())
        resultHandler.handle(Future.failedFuture(ar.cause()))
      }
    }
    return this
  }


}
