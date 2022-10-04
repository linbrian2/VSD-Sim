<template>
  <v-card>
    <v-container>
      <v-row>
        <v-col>
          <v-form ref="form" class="mx-2" lazy-validation>
            <div class="d-flex">
              <MenuSelector :items="directions" :selectedItem="currentSelected" @click="directionSelected" />
              <v-divider vertical class="mx-5" />
              <div class="d-flex">
                <v-text-field
                  class="mr-4"
                  v-for="value in values"
                  :key="value.deviceId"
                  :label="value.permit"
                  v-model="value.weight"
                  :rules="[numberRule]"
                ></v-text-field>
              </div>
            </div>
          </v-form>
        </v-col>
        <v-col cols="3">
          <div class="d-flex justify-end mt-3">
            <v-btn small color="blue darken-3" @click="onUpdateWeights">
              Update
            </v-btn>
            <v-btn small color="deep-purple darken-3" class="ml-5" @click="onSaveWeights">
              &nbsp;&nbsp;Save&nbsp;&nbsp;
            </v-btn>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </v-card>
</template>

<script>
import MenuSelector from '@/components/common/MenuSelector';
export default {
  props: {
    items: Object
  },

  components: {
    MenuSelector
  },

  data: () => ({
    tab: null,
    currentSelected: 'NB',
    numberRule: v => {
      if (!isNaN(parseFloat(v)) && v >= 0 && v <= 10) return true;
      return 'Input must be number and between 0 and 10';
    }
  }),

  computed: {
    directions() {
      return Object.keys(this.items);
    },
    values() {
      return this.items[this.currentSelected];
    }
  },

  watch: {
    items() {
      // this.tab = 0;
    }
  },

  mounted() {
    //  this.tab = 0;
  },

  methods: {
    directionSelected(direction) {
      // const idx = this.directions.findIndex(item => item === this.currentSelected);
      this.currentSelected = direction;
    },

    onUpdateWeights() {
      if (this.$refs.form.validate()) {
        // Collect weights object
        const weights = {};
        Object.keys(this.items).forEach(direction => {
          const items = {};
          this.items[direction].forEach(value => {
            // The weight initially is a number, after use modified, it will become a string, so here
            // we only need to record the changed values
            if (typeof value.weight === 'string' || value.weight instanceof String) {
              const permit = value.permit;
              const weight = parseFloat(value.weight + '');
              items[permit] = weight;
            }
          });

          if (Object.keys(items).length > 0) {
            weights[direction] = items;
          }
        });

        // Forward to the parent
        if (Object.keys(weights).length > 0) {
          this.$emit('update-weights', weights);
        }
      }
    },

    onSaveWeights() {
      if (this.$refs.form.validate()) {
        // Collect weights array
        const weights = [];
        Object.keys(this.items).forEach(direction => {
          this.items[direction].forEach(value => {
            // The weight initially is a number, after use modified, it will become a string, so here
            // we only need to record the changed values
            if (typeof value.weight === 'string' || value.weight instanceof String) {
              const item = {};
              Object.assign(item, value);
              item.weight = parseFloat(value.weight);
              weights.push(item);
            }
          });
        });

        if (weights.length > 0) {
          this.$emit('save-weights', weights);
        }
      }
    }
  }
};
</script>
