package com.eygraber.portal.samples.portal.alarmlist

interface AlarmListAlarm {
  val id: String
  val isEnabled: Boolean
  val time: String
}

interface AlarmListState {
  val alarms: List<AlarmListAlarm>
}
