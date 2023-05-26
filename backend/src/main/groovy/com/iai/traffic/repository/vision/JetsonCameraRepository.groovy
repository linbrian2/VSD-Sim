package com.iai.traffic.repository.vision

import com.iai.traffic.domain.vision.TrafficCamera
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'vision')
interface JetsonCameraRepository extends CrudRepository<TrafficCamera, Long> {
    Optional<TrafficCamera> findBySerialNo(String serial)
}