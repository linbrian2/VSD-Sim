<template>
  <v-card class="mx-auto" elevation="0" style="position: absolute; top: 10px; left:20px; " color="rgb(0, 0, 0, 0.0)">
    <v-col v-for="(x, i) in cardData" :key="x.id" class="pa-0">
      <v-row cols="12" v-if="x.val">
        <v-hover v-slot="{ hover }">
          <v-sheet outlined :color="i == selectedIdx ? 'white' : 'transparent'">
            <v-sheet outlined :color="i == selectedIdx ? 'white' : 'transparent'">
              <v-sheet outlined :color="i == selectedIdx ? 'white' : 'transparent'">
                <v-sheet outlined :color="i == selectedIdx ? 'white' : 'transparent'">
                  <v-tooltip right>
                    <template v-slot:activator="{ on: tooltip }">
                      <v-card
                        tile
                        class="d-flex align-center justify-center"
                        :disabled="!x.val || (x.val && (x.val == 0 || x.val == '-'))"
                        height="65px"
                        width="120px"
                        @click.native="cardClicked(i)"
                        :color="getColor(x)"
                        :elevation="hover ? 12 : 2"
                        :class="{ 'on-hover': hover }"
                        v-on="{ ...tooltip }"
                      >
                        <v-col class="grid-center pa-0">
                          <v-card-title v-show="$vuetify.breakpoint.lgAndUp" class="pa-0" style="font-size:38px">
                            <v-icon class="pr-2" :color="colors[i]" large>{{ x.icon }}</v-icon>
                            <template v-if="!x.val || (x.val && (x.val == 0 || x.val == '-'))">
                              <div class="pt-3"></div>
                              <Spinner />
                            </template>
                            <template v-else>
                              {{ x.val }}
                            </template>
                          </v-card-title>
                        </v-col>
                        <div v-show="hover" class="card-container">
                          <v-btn icon @click="goToPage(x.link)" class="link-button">
                            <v-icon small :class="{ 'show-btns': hover }" color="transparent">
                              mdi-open-in-new
                            </v-icon>
                          </v-btn>
                        </div>
                      </v-card>
                    </template>
                    <span>{{ x.title }}</span>
                  </v-tooltip>
                </v-sheet>
              </v-sheet>
            </v-sheet>
          </v-sheet>
        </v-hover>
      </v-row>
    </v-col>
  </v-card>
</template>

<script>
import Spinner from '@/components/common/Spinner.vue';

export default {
  props: {
    cardData: Array,
    selectedIdx: Number
  },
  components: {
    Spinner
  },

  data() {
    return {
      colors: ['white', 'white', 'white', 'white', 'white', 'white']
    };
  },
  methods: {
    cardClicked(i) {
      // if (this.selectedIdx == i) {
      //   this.goToPage(link);
      // }
      this.$emit('cardClicked', i);
    },
    goToPage(link) {
      window.open(link, '_blank');
    },
    getColor(param) {
      if (param.val == '-') {
        return 'black';
      }
      if (param && param.thresholds) {
        for (let i = 0; i < param.thresholds.length; i++) {
          if (i == param.thresholds.length - 1) {
            return param.thresholds[i].color;
          } else {
            if (param.val >= param.thresholds[i].val && param.val < param.thresholds[i + 1].val) {
              return param.thresholds[i].color;
            }
          }
        }
      } else {
        return 'black';
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.link-button {
  position: absolute;
  bottom: 4px;
  right: -8px;
}
.card-container {
  position: relative;
}

.show-btns {
  color: rgba(255, 255, 255, 1) !important;
}
.show-btns-light {
  color: rgba(0, 0, 0, 1) !important;
}
</style>
