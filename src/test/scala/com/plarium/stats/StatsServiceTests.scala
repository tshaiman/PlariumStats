package com.plarium.stats


import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.plarium.stats.model._
import com.plarium.stats.pipeline.StatsAggregatorActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.language.postfixOps


class StatsServiceTests(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with AnyWordSpecLike
    with BeforeAndAfterAll {
  //#test-classes

  def this() = this(ActorSystem("PandaStatsUtilitiesSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }


  "user_avg_age diffrent users" in {
    val probe = TestProbe()
    val aggregator = system.actorOf(StatsAggregatorActor.props())

    val gameEvent1:GameEvent = GameEvent("","",20,5,6,"",100,"NY",is_depositor = false,"1")
    val gameEvent2:GameEvent = GameEvent("","",30,5,6,"",100,"NY",is_depositor = false,"2")
    //send data
    aggregator.tell(gameEvent1,probe.ref)
    aggregator.tell(gameEvent2,probe.ref)

    //Get Response
    aggregator.tell(AvgAgeRequest,probe.ref)
    val response = probe.expectMsgType[AvgAgeResponse]
    response.average_players_age shouldBe  25
    response.fetched_records shouldBe 2
  }

  "user_avg_age same users" in {
    val probe = TestProbe()
    val aggregator = system.actorOf(StatsAggregatorActor.props())

    val ev1:GameEvent = GameEvent("","",20,5,6,"",100,"NY",is_depositor = false,"1")
    val ev2:GameEvent = GameEvent("","",30,5,6,"",100,"NY",is_depositor = false,"2")
    val ev3:GameEvent = GameEvent("","",30,6,6,"",100,"NY",is_depositor = false,"2")
    val ev4:GameEvent = GameEvent("","",30,7,6,"",100,"NY",is_depositor = false,"2")
    val ev5:GameEvent = GameEvent("","",30,8,6,"",100,"NY",is_depositor = false,"2")
    val ev6:GameEvent = GameEvent("","",30,9,6,"",100,"NY",is_depositor = false,"2")
    val ev7:GameEvent = GameEvent("","",20,9,6,"",100,"NY",is_depositor = false,"1")
    val events = List(ev1,ev2,ev3,ev4,ev5,ev6,ev7)
    for (evt <- events) {
      aggregator.tell(evt,probe.ref)
    }
    //Get Response
    aggregator.tell(AvgAgeRequest,probe.ref)
    val response = probe.expectMsgType[AvgAgeResponse]
    response.average_players_age shouldBe  25 // STILL 25 since they are the same users
    response.fetched_records shouldBe 7
  }

  "get Country Max Level all countries" in {
    val probe = TestProbe()
    val aggregator = system.actorOf(StatsAggregatorActor.props())

    val ev1:GameEvent = GameEvent("","",20,5,6,"",100,"NY",is_depositor = false,"1")
    val ev2:GameEvent = GameEvent("","",30,2,6,"",100,"AZ",is_depositor = false,"2")
    val ev3:GameEvent = GameEvent("","",30,4,6,"",100,"AZ",is_depositor = false,"2")
    val ev4:GameEvent = GameEvent("","",30,1,6,"",100,"NY",is_depositor = false,"3")
    // => [NY : 5, AZ : 4 ]

    val events = List(ev1,ev2,ev3,ev4)
    for (evt <- events) {
      aggregator.tell(evt,probe.ref)
    }
    //Get Response
    aggregator.tell(CountryMaxLevelRequest(None),probe.ref)
    val response = probe.expectMsgType[CountryMaxLevelResponse]
    val expected:Map[String,Int] = Map("NY"->5, "AZ"->4)
    response.countries should === (expected)
    response.fetched_records shouldBe 4
  }

  "get Country Max Level filter countries" in {
    val probe = TestProbe()
    val aggregator = system.actorOf(StatsAggregatorActor.props())

    val ev1:GameEvent = GameEvent("","",20,5,6,"",100,"NY",is_depositor = false,"1")
    val ev2:GameEvent = GameEvent("","",30,2,6,"",100,"AZ",is_depositor = false,"2")
    val ev3:GameEvent = GameEvent("","",30,4,6,"",100,"AZ",is_depositor = false,"2")
    val ev4:GameEvent = GameEvent("","",30,1,6,"",100,"NY",is_depositor = false,"3")
    // => [NY : 5, AZ : 4 ]

    val events = List(ev1,ev2,ev3,ev4)
    for (evt <- events) {
      aggregator.tell(evt,probe.ref)
    }
    //Get Response
    aggregator.tell(CountryMaxLevelRequest(Some("NY")),probe.ref)
    val response = probe.expectMsgType[CountryMaxLevelResponse]
    val expected:Map[String,Int] = Map("NY"->5)
    response.countries should === (expected)
    response.fetched_records shouldBe 4

    //No Such Country
    aggregator.tell(CountryMaxLevelRequest(Some("NO!")),probe.ref)
    val response2 = probe.expectMsgType[CountryMaxLevelResponse]
    response2.countries should === (Map.empty)
    response2.fetched_records shouldBe 4
  }

}

