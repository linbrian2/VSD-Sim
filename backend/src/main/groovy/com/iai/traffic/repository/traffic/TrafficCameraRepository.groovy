package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficCamera
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface TrafficCameraRepository extends CrudRepository<TrafficCamera, Long> {
    Optional<TrafficCamera> findByCameraId(String cameraId)
    List<TrafficCamera> findByEnabled(boolean enabled)
}