package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class WazeRoad {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    String   name
    Integer  roadId

    def String toString() {
        String str = "Id: " + id + ", name: " + name + ", roadId: " + roadId
        return str
    }
}