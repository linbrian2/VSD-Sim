<template>
  <v-container class="page" fill-height>
    <v-row justify="center" style="margin: 40px auto">
      <v-img alt="Logo" class="shrink" src="@/assets/logo.svg" width="450" />
    </v-row>
    <v-row class="justify-center align-center">
      <v-col cols="12">
        <v-card class="pa-3 page-login__card" tile>
          <v-card-title class="justify-center my-2">
            <div class="primary--text display-1">Forgot Your Password?</div>
          </v-card-title>
          <v-card-subtitle>
            Enter your email you use to signin, and we'll send you a link to reset your password.
          </v-card-subtitle>
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
                :rules="emailRules"
                @input="handleInput"
                @keyup.enter="handlePasswordReset"
              />
            </v-form>
            <v-alert :type="type" v-if="message">
              <v-row align="center">
                {{ message }}
              </v-row>
            </v-alert>
          </v-card-text>

          <v-card-actions>
            <v-btn text class="overline grey--text" @click="handleBackToLogin">
              Back to login
            </v-btn>
            <v-spacer />
            <v-btn rounded color="primary" :loading="loading" @click="handlePasswordReset">
              <div class="mx-8">Request Password Reset</div>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import AuthService from '../services/auth.service';
import { RouterPaths } from '@/utils/constants/router';

export default {
  data() {
    return {
      loading: false,
      type: 'success',
      message: '',
      email: '',
      formValid: false,
      emailRules: [v => /.+@.+/.test(v) || 'Invalid Email address'],
      formRule: {
        email: [v => !!v || 'email required']
      }
    };
  },

  created() {
    this.$vuetify.theme.dark = true;
  },

  methods: {
    handlePasswordReset() {
      if (this.$refs.form.validate()) {
        this.loading = true;
        if (this.email) {
          AuthService.forgotPassword(this.email)
            .then(
              data => {
                this.type = 'success';
                this.message = data.message;
                setTimeout(() => {
                  this.$router.push(RouterPaths.AUTH_LOGIN);
                }, 6000);
              },
              error => {
                this.type = 'error';
                this.message = (error.data && error.data.message) || 'Error occurred';
              }
            )
            .finally(() => {
              this.loading = false;
            });
        }
      }
    },

    handleBackToLogin() {
      this.$router.push(RouterPaths.AUTH_LOGIN, () => {});
    },

    handleInput() {
      this.message = '';
    }
  }
};
</script>

<style lang="sass" scoped>
.page
  &__card
  max-width: 700px
  margin: 0 auto
</style>
