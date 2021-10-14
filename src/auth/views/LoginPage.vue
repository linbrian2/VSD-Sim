<template>
  <v-container class="page-login" fill-height>
    <v-row justify="center" style="margin: 40px auto">
      <v-img alt="Logo" class="shrink" src="@/assets/logo.svg" width="450" />
    </v-row>
    <v-row>
      <v-col :cols="12">
        <v-card class="pa-3 page-login__card" tile>
          <v-card-title class="justify-center">
            <div class="primary--text display-1">Login</div>
          </v-card-title>
          <v-card-text class="mb-5">
            <v-form ref="form" v-model="formValid" class="mt-5" lazy-validation>
              <v-text-field
                v-model="email"
                append-icon="mdi-email"
                autocomplete="off"
                name="login"
                label=""
                placeholder="Email"
                type="text"
                required
                outlined
                :rules="formRule.email"
                @input="handleInput"
              />
              <v-text-field
                v-model="password"
                append-icon="mdi-lock"
                autocomplete="off"
                name="password"
                label=""
                placeholder="Password"
                type="password"
                :rules="formRule.password"
                required
                outlined
                @input="handleInput"
                @keyup.enter="handleLogin"
              />
            </v-form>
            <v-alert type="error" v-if="error">
              <v-row align="center">
                {{ error }}
              </v-row>
            </v-alert>
          </v-card-text>
          <v-card-actions>
            <v-btn text class="grey--text" @click="handleForgetPassword">
              Forget password?
            </v-btn>
            <v-spacer />
            <v-btn rounded color="primary" :loading="loading" @click="handleLogin">
              <div class="mx-8">Login</div>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import { RouterPaths } from '@/utils/constants/router';

export default {
  data() {
    return {
      error: '',
      email: '',
      password: '',
      submitted: false,
      loading: false,
      formValid: false,
      formRule: {
        email: [v => !!v || 'email required'],
        password: [v => !!v || 'password required']
      }
    };
  },

  computed: {
    ...mapState('auth', ['status'])
  },

  created() {
    this.$vuetify.theme.dark = true;
    this.logout();
  },

  methods: {
    handleLogin() {
      if (this.$refs.form.validate()) {
        this.loading = true;
        this.submitted = true;
        const { email, password } = this;
        if (email && password) {
          this.login({
            email,
            password,
            errorCallback: error => {
              this.error = error || 'Error occurred';
            }
          })
            .then(() => {
              this.loading = false;
            })
            .finally(() => {
              this.loading = false;
            });
        }
      }
    },

    handleForgetPassword() {
      this.$router.push({ path: RouterPaths.AUTH_FORGOT_PASSWORD }).catch(() => {});
    },

    handleInput() {
      this.error = '';
    },

    ...mapActions('auth', ['login', 'logout'])
  }
};
</script>

<style lang="sass" scoped>
.page-login
  &__card
  max-width: 600px
  margin: 0 auto
</style>
