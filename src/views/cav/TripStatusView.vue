<template>
  <div>
    <SelectionPanel title="Vehicle List">
      <VehicleList />
    </SelectionPanel>
    <MapView />
  </div>
</template>

<script>
import VehicleList from '@/components/cav/VehicleList';
import MapView from '@/components/cav/MapView';
import SelectionPanel from '@/components/cav/SelectionPanel';

export default {
  components: {
    SelectionPanel,
    MapView,
    VehicleList
  }
};
</script>
