<template>
  <div>
    <SelectionPanel title="Vehicle List">
      <VehicleList />
    </SelectionPanel>
    <MapView />
  </div>
</template>

<script>
import VehicleList from '@/components/modules/cav/VehicleList';
import MapView from '@/components/modules/cav/MapView';
import SelectionPanel from '@/components/modules/cav/SelectionPanel';

export default {
  components: {
    SelectionPanel,
    MapView,
    VehicleList
  }
};
</script>
