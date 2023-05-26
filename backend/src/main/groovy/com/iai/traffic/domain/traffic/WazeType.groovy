package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class WazeType {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;

    String   wazeType
    String   name

    Integer  alertId
    Integer  iconicCode
    Integer  type
    Integer  enable

    def String toString() {
        String str = "Id: " + id + ", wazeType: " + wazeType + ", name: " + name + "alertId: " + alertId + 
                     ", iconicCode: " + iconicCode + ", type: " + type + ", enable: " + enable
        return str
    }
}