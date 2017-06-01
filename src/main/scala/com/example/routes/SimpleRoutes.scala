package com.example.routes

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport.defaultNodeSeqMarshaller
import akka.http.scaladsl.server.Directives._

/**
 * Routes can be defined in separated classes like shown in here
 */
trait SimpleRoutes {

  // This `val` holds one route (of possibly many more that will be part of your Web App)
  lazy val simpleRoutes =
    path("hello") { // Listens to paths that are exactly `/hello`
      get { // Listens only to GET requests
        complete(<html><body><h1>Say hello to akka-http</h1></body></html>) // Completes with some html page
      } ~
        post {
          complete("foo")
        }
    }
}
