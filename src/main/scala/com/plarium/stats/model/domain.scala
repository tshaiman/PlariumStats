package com.plarium.stats.model

case object Start

final case class Error(msg: String)

final case class GameEvent(firstName: String,
                              lastName: String,
                              age: Int,
                              last_game_level: Int,
                              level_attempts: Int,
                              email: String,
                              event_ts: Long,
                              country_code: String,
                              is_depositor: Boolean,
                              user_uuid: String)
