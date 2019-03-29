package com.shujing.vertx.wiki;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.AsyncResult;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Handler;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.jdbc.JDBCClient;


@ProxyGen
@VertxGen
public interface WikiDatabaseService {

  @Fluent
  WikiDatabaseService fetchAllPages(Handler<AsyncResult<JsonArray>> resultHandler);

  @GenIgnore
  static WikiDatabaseService create(JDBCClient dbClient) {
    return new WikiDatabaseServiceImpl(dbClient);
  }

  @GenIgnore
  static WikiDatabaseService createProxy(Vertx vertx, String address) {
    return new WikiDatabaseServiceVertxEBProxy(vertx, address);
  }

}
