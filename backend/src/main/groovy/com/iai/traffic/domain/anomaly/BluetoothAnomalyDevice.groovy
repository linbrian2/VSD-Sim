package com.iai.traffic.domain.anomaly

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class BluetoothAnomalyDevice {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    String   linkId
    String   shortName
    String   description

    String   direction
    String   route
    String   startPoint
    String   endPoint

    Integer  freeFlowTime

    Double   distance

    @Column(length = 4096)
    String   coordinates
    String   camera
}