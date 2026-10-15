package com.chinatower.message.station.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmSoundDTO implements Serializable {
    private Long alarmSoundId;
    private String docUrl;
}
