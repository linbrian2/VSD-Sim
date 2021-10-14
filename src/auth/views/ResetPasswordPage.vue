<template>
  <v-container class="page-login">
    <v-row justify="center" style="margin: 40px auto">
      <v-img alt="Logo" class="shrink" src="@/assets/logo.svg" width="450" />
    </v-row>

    <v-row>
      <v-col :cols="12" v-if="initialized && !token">
        <div class="ma-2" fill-height>
          <div class="text-center pt-15">
            <v-alert color="orange">
              <h3 class="display-2 overline">
                You are not authorized to reset your password due to invalid or expired token
              </h3>
            </v-alert>
          </div>
        </div>
      </v-col>
      <v-col :cols="12" v-else>
        <v-card class="pa-3 page-login__card" tile>
          <v-card-title class="justify-center my-2">
            <div class="primary--text display-1">Reset Your Password</div>
          </v-card-title>
          <v-card-text class="mb-2">
            <v-form ref="form" v-model="formValid" class="mt-5" lazy-validation>
              <v-text-field
                v-model="password"
                append-icon="mdi-lock"
                autocomplete="off"
                name="password"
                label=""
                placeholder="Password"
                type="password"
                :rules="formRule.password"
                @input="handleInput"
                required
                outlined
              />
              <v-text-field
                v-model="confirmPassword"
                append-icon="mdi-lock"
                autocomplete="off"
                name="confirmPassword"
                label=""
                placeholder="Confirm Password"
                type="password"
                :rules="formRule.password"
                @input="handleInput"
                required
                outlined
              />
            </v-form>
            <v-alert :type="type" v-if="message">
              <v-row align="center">
                {{ message }}
              </v-row>
            </v-alert>
          </v-card-text>

          <v-card-actions>
            <v-btn text class="overline grey--text" @click="handleCancel">
              Cancel
            </v-btn>
            <v-spacer />
            <v-btn rounded color="primary" :loading="loading" @click="handlePasswordReset">
              <div class="mx-5">Reset Password</div>
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
      initialized: false,
      token: '',
      type: 'success',
      message: '',
      password: '',
      confirmPassword: '',
      loading: false,
      formValid: false,
      formRule: {
        password: [v => !!v || 'password required']
      }
    };
  },

  computed: {
    confirmPasswordRules() {
      const rules = [this.password === this.confirmPassword || 'Password must match.'];
      return rules;
    }
  },

  created() {
    this.$vuetify.theme.dark = true;
  },

  mounted() {
    const token = this.$route.query.token;
    if (token) {
      AuthService.validateResetToken(token)
        .then(
          data => {
            this.type = 'success';
            this.message = '';
            this.token = token;
            console.log(data);
          },
          error => {
            this.type = 'error';
            this.message = (error.data && error.data.message) || 'Error occurred';
            console.log('OK');
            setTimeout(() => {
              this.$router.push(RouterPaths.AUTH_LOGIN);
            }, 8000);
          }
        )
        .finally(() => {
          this.loading = false;
          this.initialized = true;
        });
    }
  },

  methods: {
    handlePasswordReset() {
      if (this.$refs.form.validate()) {
        this.loading = true;
        const { token, password, confirmPassword } = this;
        if (password && confirmPassword) {
          AuthService.resetPassword(token, password, confirmPassword)
            .then(
              data => {
                this.type = 'success';
                this.message = data.message;
                setTimeout(() => {
                  this.$router.push(RouterPaths.AUTH_LOGIN);
                }, 5000);
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

    handleCancel() {
      this.$router.push('/', () => {});
    },

    handleInput() {
      this.message = '';
    }
  }
};
</script>

<style lang="sass" scoped>
.page-login
  &__card
  max-width: 700px
  margin: 0 auto
</style>
